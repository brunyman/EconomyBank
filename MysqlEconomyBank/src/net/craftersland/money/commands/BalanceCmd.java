package net.craftersland.money.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class BalanceCmd {
	
	private Money money;
	
	public BalanceCmd(Money m) {
		this.money = m;
	}
	
	public boolean runUserCmd(CommandSender sender) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.cmd.balance")) {
				if (money.cooldown.contains(p.getUniqueId())) {
					money.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
					money.getSoundHandler().sendPlingSound(p);
    				return true;
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
			} else {
				money.getSoundHandler().sendPlingSound(p);
				money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return false;
			}
		} else {
			sender.sendMessage("You cant run this command from console!");
		}
		return true;
	}
	
	public boolean runAdminCmd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.admin")) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					if (target.isOnline()) {
						if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
							money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.accountDoesNotExist", "0", target, target.getName());
							money.getSoundHandler().sendPlingSound(p);
							return false;
						}
						String amount = money.getMoneyDatabaseInterface().getBalance(target).toString();
						money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.balanceCommand", amount, target, target.getName());
						money.getSoundHandler().sendClickSound(p);
						return true;
					    }
					} else {
						try {
							UUID targetUUID = UUID.fromString(args[1]);
							if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
								money.getConfigurationHandler().printMessage((Player) sender, "chatMessages.accountDoesNotExist", "0", null, "null");
								money.getSoundHandler().sendPlingSound(p);
								return false;
							}
							String amount = money.getMoneyDatabaseInterface().getBalance(targetUUID).toString();
							money.getConfigurationHandler().printMessage((Player) sender, "chatMessages.balanceCommand", amount, null, "null");
							money.getSoundHandler().sendClickSound(p);
							return true;
						} catch (Exception e) {
							money.getConfigurationHandler().printMessage((Player) sender, "chatMessages.balanceCommandFail", "0", null, null);
							money.getSoundHandler().sendPlingSound(p);
							return false;
						}
				}
				
			} else {
				money.getSoundHandler().sendPlingSound(p);
				money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return false;
			}
		} else {
			Player target = Bukkit.getPlayer(args[1]);
			if (target != null) {
				if (target.isOnline()) {
					if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
						sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.RED + " does not have an account!");
						return false;
					}
					String amount = money.getMoneyDatabaseInterface().getBalance(target).toString();
					sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.GREEN + " balance: " + ChatColor.WHITE + "" + amount);
					return true;
				    }
				} else {
					try {
						UUID targetUUID = UUID.fromString(args[1]);
						if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
							sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + targetUUID + ChatColor.RED + " does not have an account!");
							return false;
						}
						String amount = money.getMoneyDatabaseInterface().getBalance(targetUUID).toString();
						sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + targetUUID + ChatColor.GREEN + " balance: " + ChatColor.WHITE + "" + amount);
						return true;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED +  ">> Player offline or wrong UUID!");
						return false;
					}
			}
		}
		return true;
	}

}
