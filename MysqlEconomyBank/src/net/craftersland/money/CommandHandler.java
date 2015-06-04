package net.craftersland.money;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	
	private Money money;
	
	public CommandHandler(Money money) {
		this.money = money;
	}
	
	public boolean onCommand(final CommandSender sender, final Command command, final String cmdlabel, final String[] args) {
		Player p;
		
		if (cmdlabel.equalsIgnoreCase("meb") || cmdlabel.equalsIgnoreCase("bank")) {
			
			if (args.length == 0) {
				if (sender instanceof Player) {
					p = (Player) sender;
					sendHelp(p);
					return true;
				} else {
					sendConsoleHelp(sender);
					return false;
				}
			}
			
			if (args.length == 1) {
				
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						if (p.hasPermission("MysqlEconomyBank.admin")) {
							try {
								money.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"config.yml"));
							} catch (Exception e) {
								money.getConfigurationHandler().printMessage(p, "chatMessages.reloadFail", "0", p.getUniqueId(), p.getName());
								p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
								e.printStackTrace();
								return false;
							}
							p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
							money.getConfigurationHandler().printMessage(p, "chatMessages.reloadComplete", "0", p.getUniqueId(), p.getName());
							return true;
						}
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
						money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p.getUniqueId(), p.getName());
						return false;
					} else {
						try {
							money.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"config.yml"));
						} catch (Exception e) {
							sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not load config! Check logs!");
							e.printStackTrace();
							return false;
						}
						sender.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + "Configuration reloaded!");
						return true;
					}
				}
				
				if (args[0].equalsIgnoreCase("help")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
				
				if (sender instanceof Player) {
					p = (Player) sender;
					sendHelp(p);
					return false;
				} else {
					sendConsoleHelp(sender);
					return false;
				}
			}
			
			if (args.length == 2) {
				
				if (args[0].equalsIgnoreCase("balance")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						if (p.hasPermission("MysqlEconomyBank.admin")) {
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								if (target.isOnline()) {
									if (money.getMoneyDatabaseInterface().hasAccount(target.getUniqueId()) == false) {
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.accountDoesNotExist", "0", target.getUniqueId(), target.getName());
										p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
										return false;
									}
									String amount = money.getMoneyDatabaseInterface().getBalance(target.getUniqueId()).toString();
									money.getConfigurationHandler().printMessage(target, "chatMessages.balanceCommand", amount, target.getUniqueId(), target.getName());
									p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
									return true;
								    }
								} else {
									try {
										UUID targetUUID = UUID.fromString(args[1]);
										if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
											money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.accountDoesNotExist", "0", targetUUID, targetUUID.toString());
											p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
											return false;
										}
										String amount = money.getMoneyDatabaseInterface().getBalance(targetUUID).toString();
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.balanceCommand", amount, targetUUID, targetUUID.toString());
										p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
										return true;
									} catch (Exception e) {
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.balanceCommandFail", "0", null, null);
										p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
										return false;
									}
							}
							
						} else {
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
							money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p.getUniqueId(), p.getName());
							return false;
						}
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							if (target.isOnline()) {
								if (money.getMoneyDatabaseInterface().hasAccount(target.getUniqueId()) == false) {
									sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.RED + " does not have an account!");
									return false;
								}
								String amount = money.getMoneyDatabaseInterface().getBalance(target.getUniqueId()).toString();
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
				} else {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
				
			}
			
			if (args.length == 3) {
				
				if (args[0].equalsIgnoreCase("set")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						if (p.hasPermission("MysqlEconomyBank.admin")) {
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								if (target.isOnline()) {
									if (money.getMoneyDatabaseInterface().hasAccount(target.getUniqueId()) == false) {
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.accountDoesNotExist", "0", target.getUniqueId(), target.getName());
										p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
										return false;
									}
									
									try {
										Double amount = Double.parseDouble(args[2]);
										money.getMoneyDatabaseInterface().setBalance(target.getUniqueId(), amount);
										money.getConfigurationHandler().printMessage(target, "chatMessages.setCommand", amount.toString(), target.getUniqueId(), target.getName());
										p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
										return true;
									} catch (Exception e) {
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.setCommandFail", "0", null, null);
										p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
										return false;
									}
									
								    }
								} else {
									try {
										UUID targetUUID = UUID.fromString(args[1]);
										if (money.getMoneyDatabaseInterface().hasAccount(targetUUID) == false) {
											money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.accountDoesNotExist", "0", targetUUID, targetUUID.toString());
											p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
											return false;
										}
										
										try {
											Double amount = Double.parseDouble(args[2]);
											money.getMoneyDatabaseInterface().setBalance(targetUUID, amount);
											money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.setCommand", amount.toString(), targetUUID, targetUUID.toString());
											p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
											return true;
										} catch (Exception e) {
											money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.setCommandFail", "0", null, null);
											p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
											return false;
										}
										
									} catch (Exception e) {
										money.getConfigurationHandler().printMessage(((Player) sender).getPlayer(), "chatMessages.balanceCommandFail", "0", null, null);
										p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
										return false;
									}
							}
							
						} else {
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
							money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p.getUniqueId(), p.getName());
							return false;
						}
					} else {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							if (target.isOnline()) {
								if (money.getMoneyDatabaseInterface().hasAccount(target.getUniqueId()) == false) {
									sender.sendMessage(ChatColor.RED +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.RED + " does not have an account!");
									return false;
								}
								
								try {
									Double amount = Double.parseDouble(args[2]);
									money.getMoneyDatabaseInterface().setBalance(target.getUniqueId(), amount);
									sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + target.getName() + ChatColor.GREEN + " balance set to: " + ChatColor.WHITE + "" + amount);
									return true;
								} catch (Exception e) {
									sender.sendMessage(ChatColor.RED +  ">> The amount must be a number!");
									return false;
								}
								
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
										money.getMoneyDatabaseInterface().setBalance(targetUUID, amount);
										sender.sendMessage(ChatColor.GREEN +  ">> " + ChatColor.WHITE + "" + targetUUID + ChatColor.GREEN + " balance set to: " + ChatColor.WHITE + "" + amount);
										return true;
									} catch (Exception e) {
										sender.sendMessage(ChatColor.RED +  ">> The amount must be a number!");
										return false;
									}
									
								} catch (Exception e) {
									sender.sendMessage(ChatColor.RED +  ">> Player offline or wrong UUID!");
									return false;
								}
						}
					}
				} else {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
			}
		}
		
		return false;
	}
	
	public void sendHelp(Player p) {
		p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-=-=-=-=-< " + ChatColor.AQUA + "" + ChatColor.BOLD + "MysqlEconomyBank" + ChatColor.DARK_AQUA + " >-=-=-=-=-=-=-=-=-");
		if (p.hasPermission("MysqlEconomyBank.admin")) {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.AQUA + "        Check other players bank balance:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank balance <playerName>" + ChatColor.GRAY + " - for online players.");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank balance <playerUUID>" + ChatColor.GRAY + " - for offline players.");
			p.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/bank balance John" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/bank balance f694517d-d6cf-32f1-972b-dfc677ceac45");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.AQUA + "        Set a players bank balance:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank set <playerName> amount" + ChatColor.GRAY + " - for online players.");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank set <playerUUID> amount" + ChatColor.GRAY + " - for offline players.");
			p.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/bank set John 100" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/bank set f694517d-d6cf-32f1-972b-dfc677ceac45 100.5");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.AQUA + "        Reload plugin config:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank reload");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-=-=-=-=-=-< " + ChatColor.AQUA + "" + ChatColor.BOLD + "Admin Help Page" + ChatColor.DARK_AQUA + " >-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		} else {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Sign Economy Bank");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Plugin Download: " + ChatColor.WHITE + "http://goo.gl/EPf2R8");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-=-=-=-=-=-=-< " + ChatColor.AQUA + "" + ChatColor.BOLD + "Help Page" + ChatColor.DARK_AQUA + " >-=-=-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		}
	}
	
	public void sendConsoleHelp(CommandSender sender) {
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-=-=-=-=-< " + ChatColor.AQUA + "" + ChatColor.BOLD + "MysqlEconomyBank" + ChatColor.DARK_AQUA + " >-=-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.AQUA + "        Check other players bank balance:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/bank balance <playerName>" + ChatColor.GRAY + " - for online players.");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/bank balance <playerUUID>" + ChatColor.GRAY + " - for offline players.");
			sender.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/bank balance John" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/bank balance f694517d-d6cf-32f1-972b-dfc677ceac45");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.AQUA + "        Set a players bank balance:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/bank set <playerName> amount" + ChatColor.GRAY + " - for online players.");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/bank set <playerUUID> amount" + ChatColor.GRAY + " - for offline players.");
			sender.sendMessage(ChatColor.GRAY + "" + "      Example: " + ChatColor.WHITE + "/bank set John 100" + ChatColor.GRAY + " or " + ChatColor.WHITE + "/bank set f694517d-d6cf-32f1-972b-dfc677ceac45 100.5");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.AQUA + "        Reload plugin config:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/bank reload");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.DARK_AQUA + "-=-=-=-=-=-=-=-=-< " + ChatColor.AQUA + "" + ChatColor.BOLD + "Console Help Page" + ChatColor.DARK_AQUA + " >-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
	}

}
