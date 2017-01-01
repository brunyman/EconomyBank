package net.craftersland.money;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
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
	private Set<UUID> safety = new HashSet<UUID>();

	public PlayerListener(Money money) {
		this.money = money;
	}
	
	private boolean isEventSafe(final UUID pU) {
		if (safety.contains(pU) == true) {
			return false;
		}
		safety.add(pU);
		Bukkit.getScheduler().runTaskLaterAsynchronously(money, new Runnable() {

			@Override
			public void run() {
				safety.remove(pU);
			}
			
		}, 2L);
		return true;
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		
		if (event.getClickedBlock() != null) {
		    if (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.SIGN)) {
		    	if (isEventSafe(event.getPlayer().getUniqueId()) == true) {
		    		final Sign sign = (Sign) event.getClickedBlock().getState();
			    	Bukkit.getScheduler().runTaskAsynchronously(money, new Runnable() {

						@Override
						public void run() {
							if (sign.getLine(0).contains(money.getConfigurationHandler().getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]")) {
						    	if (Money.perms.has(p, "MysqlEconomyBank.use")) {
						    		//Check if player is sneaking
						    		if (p.isSneaking()) {
						    			money.getConfigurationHandler().printMessage(p, "chatMessages.denyIfSneaking", "0", p, p.getName());
						    			money.getSoundHandler().sendPlingSound(p);
					    				return;
					    			}
						    		//Balance Signs
						    		if ((sign.getLine(1).equals(money.getConfigurationHandler().getString("signFormat.balance")))) {
						    			//check if player is in cooldown
						    			if (money.cooldown.contains(p.getUniqueId())) {
						    				money.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
						    				money.getSoundHandler().sendPlingSound(p);
						    				return;
						    			}
										money.getMoneyDatabaseInterface().getBalance(p);
										money.getConfigurationHandler().printMessage(p, "chatMessages.balance", "0", p, p.getName());
										money.getSoundHandler().sendClickSound(p);
										//Send Action Bar message. Requires TitleManager
					    				if (money.setupTitleManager() == true) {
					    					money.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balance");
					    				}
										//add player to cooldown
					    				money.cooldown.add(p.getUniqueId());
										Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(money.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"));
										int delay = delayCalc.intValue();
										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(money, new Runnable() {
			                                public void run() {
			                                	//remove player from cooldown
			                                	money.cooldown.remove(p.getUniqueId());
			                                }
			                        }, delay);
										return;
									}
						    		//Deposit Signs
						    		if ((sign.getLine(1).equals(money.getConfigurationHandler().getString("signFormat.deposit")))) {
						    			//check if player is in cooldown
						    			if (money.cooldown.contains(p.getUniqueId())) {
						    				money.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
						    				money.getSoundHandler().sendPlingSound(p);
						    				return;
						    			}
						    			Double amount = Double.parseDouble(sign.getLine(2));
						    			if (Money.econ.getBalance(p) >= amount) {
						    				Double bankBalance = money.getMoneyDatabaseInterface().getBalance(p);
						    				if (bankBalance + amount > Double.parseDouble(money.getConfigurationHandler().getString("general.maxBankLimitMoney"))) {
						    					money.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInAccount", amount + "", p, p.getName());
						    					money.getSoundHandler().sendPlingSound(p);
						    					return;
						    				}
						    				Money.econ.withdrawPlayer(p, amount);
						    				money.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
						    				money.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
						    				money.getSoundHandler().sendClickSound(p);
						    				//Send Action Bar message. Requires TitleManager
						    				if (money.setupTitleManager() == true) {
						    					money.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
						    				}
						    				//add player to cooldown
						    				money.cooldown.add(p.getUniqueId());
											Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(money.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"));
											int delay = delayCalc.intValue();
											Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(money, new Runnable() {
				                                public void run() {
				                                	//remove player from cooldown
				                                        money.cooldown.remove(p.getUniqueId());
				                                }
				                        }, delay);
						    				return;
						    			}
						    			money.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInPoket", amount + "", p, p.getName());
						    			money.getSoundHandler().sendPlingSound(p);
						    			return;
						    		}
						    		//Withdraw Signs
						    		if ((sign.getLine(1).equals(money.getConfigurationHandler().getString("signFormat.withdraw")))) {
						    			//check if player is in cooldown
						    			if (money.cooldown.contains(p.getUniqueId())) {
						    				money.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
						    				money.getSoundHandler().sendPlingSound(p);
						    				return;
						    			}
						    			Double bankBalance = money.getMoneyDatabaseInterface().getBalance(p);
						    			Double amount = Double.parseDouble(sign.getLine(2));
						    			if (bankBalance >= amount) {
						    				if (Money.econ.getBalance(p) + amount > Double.parseDouble(money.getConfigurationHandler().getString("general.maxPocketLimitMoney"))) {
						    					money.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInPocket", amount + "", p, p.getName());
						    					money.getSoundHandler().sendPlingSound(p);
						    					return;
						    				}
						    				money.getMoneyDatabaseInterface().setBalance(p, bankBalance - amount);
						    				Money.econ.depositPlayer(p, amount);
						    				money.getConfigurationHandler().printMessage(p, "chatMessages.withdrewSuccessfully", amount + "", p, p.getName());
						    				money.getSoundHandler().sendClickSound(p);
						    				//Send Action Bar message. Requires TitleManager
						    				if (money.setupTitleManager() == true) {
						    					money.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
						    				}
						    				//add player to cooldown
						    				money.cooldown.add(p.getUniqueId());
											Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(money.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"));
											int delay = delayCalc.intValue();
											Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(money, new Runnable() {
				                                public void run() {
				                                	//remove player from cooldown
				                                	money.cooldown.remove(p.getUniqueId());
				                                }
				                        }, delay);
						    				return;
						    			}
						    			money.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInAccount", amount + "", p, p.getName());
						    			money.getSoundHandler().sendPlingSound(p);
						    			return;
						    		}
								}
						    	money.getConfigurationHandler().printMessage(p, "chatMessages.notAllowed", "0", p, p.getName());
						    }
						}
			    		
			    	});
		    	}
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
						money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
						money.getSoundHandler().sendPlingSound(p);
						p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 3 and 4 must be empty for balance signs.");
						return;
					}
					event.setLine(0, "§" + money.getConfigurationHandler().getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
					event.setLine(1, money.getConfigurationHandler().getString("signFormat.balance"));
					event.setLine(2, "");
					event.setLine(3, "");
					money.getConfigurationHandler().printMessage(p, "chatMessages.createdSignSuccessfully", "0", p, p.getName());
					money.getSoundHandler().sendLevelUpSound(p);
					p.playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
					return;
				}
				// Deposit signs
				if (event.getLine(1).toLowerCase().contains("deposit")) {
					//check if number format is ok on line 3
					if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
						//check if line 3 is not empty
						if (!event.getLine(3).isEmpty()) {
							money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 4 must be empty for deposit signs.");
							money.getSoundHandler().sendPlingSound(p);
							return;
						}
						//convert from string to double to format the number
						Double numberProcessing = Double.parseDouble(event.getLine(2));
						//if processed number = 0 cancel
						if (numberProcessing == 0) {
							money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Use a number bigger then 0, supports format: 1 or 1.0 or 1.00");
							money.getSoundHandler().sendPlingSound(p);
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
						event.setLine(0, "§" + money.getConfigurationHandler().getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, money.getConfigurationHandler().getString("signFormat.deposit"));
						event.setLine(3, "");
						money.getConfigurationHandler().printMessage(p, "chatMessages.createdSignSuccessfully", "0", p, p.getName());
						money.getSoundHandler().sendLevelUpSound(p);
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
							money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Line 4 must be empty for deposit signs.");
							money.getSoundHandler().sendPlingSound(p);
							return;
						}
						//convert from string to double to format the number
						Double numberProcessing = Double.parseDouble(event.getLine(2));
						//if processed number = 0 cancel
						if (numberProcessing == 0) {
							money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Use a number bigger then 0, supports format: 1 or 1.0 or 1.00");
							money.getSoundHandler().sendPlingSound(p);
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
						event.setLine(0, "§" + money.getConfigurationHandler().getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, money.getConfigurationHandler().getString("signFormat.withdraw"));
						event.setLine(2, event.getLine(2));
						event.setLine(3, "");
						money.getConfigurationHandler().printMessage(p, "chatMessages.createdSignSuccessfully", "0", p, p.getName());
						money.getSoundHandler().sendLevelUpSound(p);
						p.playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
						return;
					}
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "On line 3 you must type a number, in format: 1 or 1.0 or 1.00");
					return;
				} else {
					money.getConfigurationHandler().printMessage(p, "chatMessages.errorWhileCreatingSign", "0", p, p.getName());
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Valid options on line 2 are: balance, deposit, withdraw");
					money.getSoundHandler().sendItemBreakSound(p);
					return;
				}
			}
			money.getConfigurationHandler().printMessage(p, "chatMessages.notAllowed", "0", p, p.getName());
			event.setLine(0, "NoPermission");
			money.getSoundHandler().sendPlingSound(p);
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
					if (sign.getLine(0).contains(money.getConfigurationHandler().getString("signFormat.signColor") + ChatColor.BOLD + "[Bank]")) {
						if (Money.perms.has(p, "MysqlEconomyBank.admin")) {
							//Check if sneaking
							if (p.isSneaking()) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.removedSignSuccessfully", "0", p, p.getName());
								money.getSoundHandler().sendItemBreakSound(p);
								return;
							}
							event.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Sneak to break the Bank sign!");
							money.getSoundHandler().sendPlingSound(p);
							return;
						} else {
							money.getConfigurationHandler().printMessage(p, "chatMessages.notAllowed", "0", p, p.getName());
							event.setCancelled(true);
							money.getSoundHandler().sendPlingSound(p);
							return;
							
						}
	                }
				}
			}

}
