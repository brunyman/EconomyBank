package net.craftersland.money;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener{
	
	private Money money;
	public static Economy econ = null;
	private ConfigurationHandler coHa;
	
	private ArrayList<Player> cooldown = new ArrayList<Player>();

	public PlayerListener(Money money) {
		this.money = money;
		this.coHa = money.getConfigurationHandler();
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event) throws Exception {
		final Player p = event.getPlayer();
		
		if (event.getClickedBlock() != null) {
		    if (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.SIGN)) {
		    	Sign sign = (Sign) event.getClickedBlock().getState();
			    if (sign.getLine(0).contains(coHa.getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]")) {
			    	if (Money.perms.has(p, "MysqlEconomyBank.use")) {
			    		//Check if player is sneaking
			    		if (p.isSneaking()) {
			    			coHa.printMessage(p, "chatMessages.denyIfSneaking", "0", p.getUniqueId(), p.getName());
			    			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
		    				return;
		    			}
			    		//Balance Signs
			    		if ((sign.getLine(1).equals(coHa.getString("signFormat.balance")))) {
			    			//check if player is in cooldown
			    			if (cooldown.contains(p)) {
			    				coHa.printMessage(p, "chatMessages.tooFastInteraction", "0", p.getUniqueId(), p.getName());
			    				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    				return;
			    			}
							money.getMoneyDatabaseInterface().getBalance(p.getUniqueId());
							coHa.printMessage(p, "chatMessages.balance", "0", p.getUniqueId(), p.getName());
							p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
							//Send Action Bar message. Requires TitleManager
		    				if (money.setupTitleManager() == true) {
		    					coHa.actionBarMessage(p, "actionBarMessages.balance");
		    				}
							//add player to cooldown
							cooldown.add(p);
							Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(coHa.getString("general.timeBetweenTwoInteractions"));
							int delay = delayCalc.intValue();
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(money, new Runnable() {
                                public void run() {
                                	//remove player from cooldown
                                        cooldown.remove(p);
                                }
                        }, delay);
							return;
						}
			    		//Deposit Signs
			    		if ((sign.getLine(1).equals(coHa.getString("signFormat.deposit")))) {
			    			//check if player is in cooldown
			    			if (cooldown.contains(p)) {
			    				coHa.printMessage(p, "chatMessages.tooFastInteraction", "0", p.getUniqueId(), p.getName());
			    				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    				return;
			    			}
			    			String amount = sign.getLine(2);
			    			if (Money.econ.getBalance(p) >= Double.parseDouble(amount)) {
			    				if (money.getMoneyDatabaseInterface().getBalance(p.getUniqueId()) + Double.parseDouble(amount) > Double.parseDouble(money.getConfigurationHandler().getString("general.maxBankLimitMoney"))) {
			    					money.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInAccount", amount, p.getUniqueId(), p.getName());
			    					p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    					return;
			    				}
			    				Money.econ.withdrawPlayer(p, Double.parseDouble(amount));
			    				money.getMoneyDatabaseInterface().addToAccount(p.getUniqueId(), Double.parseDouble(amount));
			    				money.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount, p.getUniqueId(), p.getName());
			    				p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
			    				//Send Action Bar message. Requires TitleManager
			    				if (money.setupTitleManager() == true) {
			    					coHa.actionBarMessage(p, "actionBarMessages.balanceLeft");
			    				}
			    				//add player to cooldown
								cooldown.add(p);
								Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(coHa.getString("general.timeBetweenTwoInteractions"));
								int delay = delayCalc.intValue();
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(money, new Runnable() {
	                                public void run() {
	                                	//remove player from cooldown
	                                        cooldown.remove(p);
	                                }
	                        }, delay);
			    				return;
			    			}
			    			money.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInPoket", amount, p.getUniqueId(), p.getName());
			    			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    			return;
			    		}
			    		//Withdraw Signs
			    		if ((sign.getLine(1).equals(coHa.getString("signFormat.withdraw")))) {
			    			//check if player is in cooldown
			    			if (cooldown.contains(p)) {
			    				coHa.printMessage(p, "chatMessages.tooFastInteraction", "0", p.getUniqueId(), p.getName());
			    				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    				return;
			    			}
			    			String amount = sign.getLine(2);
			    			if (money.getMoneyDatabaseInterface().getBalance(p.getUniqueId()) >= Double.parseDouble(amount)) {
			    				if (Money.econ.getBalance(p) + Double.parseDouble(amount) > Double.parseDouble(money.getConfigurationHandler().getString("general.maxPocketLimitMoney"))) {
			    					money.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInPocket", amount, p.getUniqueId(), p.getName());
			    					p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    					return;
			    				}
			    				money.getMoneyDatabaseInterface().removeFromAccount(p.getUniqueId(), Double.parseDouble(amount));
			    				Money.econ.depositPlayer(p, Double.parseDouble(amount));
			    				money.getConfigurationHandler().printMessage(p, "chatMessages.withdrewSuccessfully", amount, p.getUniqueId(), p.getName());
			    				p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
			    				//Send Action Bar message. Requires TitleManager
			    				if (money.setupTitleManager() == true) {
			    					coHa.actionBarMessage(p, "actionBarMessages.balanceLeft");
			    				}
			    				//add player to cooldown
								cooldown.add(p);
								Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(coHa.getString("general.timeBetweenTwoInteractions"));
								int delay = delayCalc.intValue();
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(money, new Runnable() {
	                                public void run() {
	                                	//remove player from cooldown
	                                        cooldown.remove(p);
	                                }
	                        }, delay);
			    				return;
			    			}
			    			money.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInAccount", amount, p.getUniqueId(), p.getName());
			    			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			    			return;
			    		}
			    		
					}
			    	coHa.printMessage(p, "chatMessages.notAllowed", "0", p.getUniqueId(), p.getName());
			    	return;
			    }
			    return;
			}
		}
				
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		Player p = event.getPlayer();
		if (event.getLine(0).contains("[Bank]")){
			if (Money.perms.has(p, "MysqlEconomyBank.admin")) {
				//Balance signs
				if (event.getLine(1).toLowerCase().contains("balance")) {
					if (!event.getLine(2).isEmpty() || !event.getLine(3).isEmpty()) {
						coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 3 and 4 must be empty for balance signs.");
						return;
					}
					event.setLine(0, coHa.getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
					event.setLine(1, coHa.getString("signFormat.balance"));
					event.setLine(2, "");
					event.setLine(3, "");
					coHa.printMessage(p, "chatMessages.createdSignSuccessfully", "0", p.getUniqueId(), p.getName());
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
					p.playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
					return;
				}
				// Deposit signs
				if (event.getLine(1).toLowerCase().contains("deposit")) {
					//check if number format is ok on line 3
					if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
						//check if line 3 is not empty
						if (!event.getLine(3).isEmpty()) {
							coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 4 must be empty for deposit signs.");
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						}
						//convert from string to double to format the number
						Double numberProcessing = Double.parseDouble(event.getLine(2));
						//if processed number = 0 cancel
						if (numberProcessing == 0) {
							coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Use a number bigger then 0, supports format: 1 or 1.0 or 1.00");
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						}
						//convert processed number back to string
						String formatedNumber = numberProcessing.toString();
						//if the number decimals = 0 remove the decimals
						if (formatedNumber.endsWith(".0")) {
							Double.parseDouble(event.getLine(2));
							DecimalFormat format1 = new DecimalFormat("#0");
							event.setLine(2, format1.format(numberProcessing));
						} else {
							DecimalFormat format2 = new DecimalFormat("#0.00");
							event.setLine(2, format2.format(numberProcessing));
						}
						event.setLine(0, coHa.getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, coHa.getString("signFormat.deposit"));
						event.setLine(3, "");
						coHa.printMessage(p, "chatMessages.createdSignSuccessfully", "0", p.getUniqueId(), p.getName());
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						p.playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
						return;
					}
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "On line 3 you must type a number, in format: 1 or 1.0 or 1.00");
					return;
				}
				//Withdraw signs
				if (event.getLine(1).toLowerCase().contains("withdraw")) {
					//check if number format is ok on line 3
					if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
						//check if line 3 is not empty
						if (!event.getLine(3).isEmpty()) {
							coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 4 must be empty for deposit signs.");
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						}
						//convert from string to double to format the number
						Double numberProcessing = Double.parseDouble(event.getLine(2));
						//if processed number = 0 cancel
						if (numberProcessing == 0) {
							coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Use a number bigger then 0, supports format: 1 or 1.0 or 1.00");
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						}
						//convert processed number back to string
						String formatedNumber = numberProcessing.toString();
						//if the number decimals = 0 remove the decimals
						if (formatedNumber.endsWith(".0")) {
							Double.parseDouble(event.getLine(2));
							DecimalFormat format1 = new DecimalFormat("#0");
							event.setLine(2, format1.format(numberProcessing));
						} else {
							DecimalFormat format2 = new DecimalFormat("#0.00");
							event.setLine(2, format2.format(numberProcessing));
						}
						event.setLine(0, coHa.getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, coHa.getString("signFormat.withdraw"));
						event.setLine(2, event.getLine(2));
						event.setLine(3, "");
						coHa.printMessage(p, "chatMessages.createdSignSuccessfully", "0", p.getUniqueId(), p.getName());
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						p.playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
						return;
					}
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "On line 3 you must type a number, in format: 1 or 1.0 or 1.00");
					return;
				} else {
					coHa.printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p.getUniqueId(), p.getName());
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Valid options on line 2 are: balance, deposit, withdraw");
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
					return;
				}
			}
			coHa.printMessage(p, "chatMessages.notAllowed", "0", p.getUniqueId(), p.getName());
			event.setLine(0, "NoPermission");
			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			return;
		}

	}
	
	@EventHandler
	public void onSignRemove(BlockBreakEvent event){
		Player p = event.getPlayer();
		Block testblock = event.getBlock();			
			//if sign found
			if (testblock.getType().equals(Material.WALL_SIGN) || testblock.getType().equals(Material.SIGN_POST) || testblock.getType().equals(Material.SIGN)) {
					//check the found sign for the players name.
					Sign sign = (Sign) testblock.getState();
					if (sign.getLine(0).contains(coHa.getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]")) {
						if (Money.perms.has(p, "MysqlEconomyBank.admin")) {
							//Check if sneaking
							if (p.isSneaking()) {
								coHa.printMessage(p, "chatMessages.removedSignSuccessfully", "0", p.getUniqueId(), p.getName());
								p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
								return;
							}
							event.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Sneak to break the Bank sign!");
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
						} else {
							coHa.printMessage(p, "chatMessages.notAllowed", "0", p.getUniqueId(), p.getName());
							event.setCancelled(true);
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
							return;
							
						}
	                }
				}
			}

}
