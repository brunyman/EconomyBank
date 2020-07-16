package net.craftersland.money.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class SetCmd {
	
	private Money money;
	
	public SetCmd(Money m) {
		this.money = m;
	}
	
	public boolean runCmd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.admin")) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				if (target != null) {
					if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
						money.getConfigurationHandler().printMessage(p, "chatMessages.accountDoesNotExist", "0", target.getPlayer(), target.getName());
						money.getSoundHandler().sendPlingSound(p);
						return false;
					}
					
					try {
						Double amount = Double.parseDouble(args[2]);
						if (amount <= 0) {
							money.getConfigurationHandler().printMessage(p, "chatMessages.negativeAmount", amount + "", p, p.getName());
							money.getSoundHandler().sendPlingSound(p);
							return true;
						}
						money.getMoneyDatabaseInterface().setBalance(target, amount);
						money.getConfigurationHandler().printMessage(p, "chatMessages.setCommand", amount.toString(), target.getPlayer(), target.getName());
						money.getSoundHandler().sendClickSound(p);
						return true;
					} catch (Exception e) {
						money.getConfigurationHandler().printMessage(p, "chatMessages.setCommandFail", "0", null, null);
						money.getSoundHandler().sendPlingSound(p);
						return false;
					}
					} else {
						try {
							UUID targetUUID = UUID.fromString(args[1]);
							if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.accountDoesNotExist", "0", null, "null");
								money.getSoundHandler().sendPlingSound(p);
								return false;
							}
							
							try {
								Double amount = Double.parseDouble(args[2]);
								if (amount <= 0) {
									money.getConfigurationHandler().printMessage(p, "chatMessages.negativeAmount", amount + "", p, p.getName());
									money.getSoundHandler().sendPlingSound(p);
									return true;
								}
								money.getMoneyDatabaseInterface().setBalance(targetUUID, amount);
								money.getConfigurationHandler().printMessage(p, "chatMessages.setCommand", amount.toString(), null, "null");
								if (money.is19Server == true) {
									p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("CLICK"), 1.0F, 1.0F);
								}
								return true;
							} catch (Exception e) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.setCommandFail", "0", null, null);
								money.getSoundHandler().sendPlingSound(p);
								return false;
							}
							
						} catch (Exception e) {
							if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.accountDoesNotExist", "0", target.getPlayer(), target.getName());
								money.getSoundHandler().sendPlingSound(p);
								return false;
							}
							
							try {
								Double amount = Double.parseDouble(args[2]);
								if (amount <= 0) {
									money.getConfigurationHandler().printMessage(p, "chatMessages.negativeAmount", amount + "", p, p.getName());
									money.getSoundHandler().sendPlingSound(p);
									return true;
								}
								money.getMoneyDatabaseInterface().setBalance(target, amount);
								money.getConfigurationHandler().printMessage(p, "chatMessages.setCommand", amount.toString(), target.getPlayer(), target.getName());
								money.getSoundHandler().sendClickSound(p);
								return true;
							} catch (Exception ex) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.setCommandFail", "0", null, null);
								money.getSoundHandler().sendPlingSound(p);
								return false;
							}
						}
				}
				
			} else {
				money.getSoundHandler().sendPlingSound(p);
				money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return false;
			}
		} else {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if (target != null) {
				if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
					sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.RED + " does not have an account!");
					return false;
				}
				
				try {
					Double amount = Double.parseDouble(args[2]);
					if (amount <= 0) {
						sender.sendMessage(money.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
						return true;
					}
					money.getMoneyDatabaseInterface().setBalance(target, amount);
					sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.GREEN + " balance set to: " + ChatColor.WHITE + "" + amount);
					return true;
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED +  ">> The amount must be a number!");
					return false;
				}
				} else {
					try {
						UUID targetUUID = UUID.fromString(args[1]);
						if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
							sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + targetUUID + ChatColor.RED + " does not have an account!");
							return false;
						}
						
						try {
							Double amount = Double.parseDouble(args[2]);
							if (amount <= 0) {
								sender.sendMessage(money.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
								return true;
							}
							money.getMoneyDatabaseInterface().setBalance(targetUUID, amount);
							sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + targetUUID + ChatColor.GREEN + " balance set to: " + ChatColor.WHITE + "" + amount);
							return true;
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED +  ">> The amount must be a number!");
							return false;
						}
						
					} catch (Exception e) {
						if (money.getMoneyDatabaseInterface().hasAccount(target) == false) {
							sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.RED + " does not have an account!");
							return false;
						}
						
						try {
							Double amount = Double.parseDouble(args[2]);
							if (amount <= 0) {
								sender.sendMessage(money.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
								return true;
							}
							money.getMoneyDatabaseInterface().setBalance(target, amount);
							sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.GREEN + " balance set to: " + ChatColor.WHITE + "" + amount);
							return true;
						} catch (Exception ex) {
							sender.sendMessage(ChatColor.RED +  ">> The amount must be a number!");
						}
						return true;
					}
			}
		}
	}

}
