package net.craftersland;

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
			} else 	if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					money.getReloadCmd().runCmd(sender);
				} else if (args[0].equalsIgnoreCase("help")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				} else if (args[0].equalsIgnoreCase("balance")) {
					money.getBalanceCmd().runUserCmd(sender);
				} else if (args[0].equalsIgnoreCase("interest")) {
					money.getInterestCmd().runUserCmd(sender);
				} else {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return false;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("balance")) {
					money.getBalanceCmd().runAdminCmd(sender, args);
				} else if (args[0].equalsIgnoreCase("deposit")) {
					money.getDepositCmd().runUserCmd(sender, args);
				} else if (args[0].equalsIgnoreCase("withdraw")) {
					money.getWithdrawCmd().runUserCmd(sender, args);
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
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("set")) {
					money.getSetCmd().runCmd(sender, args);
				} else if (args[0].equalsIgnoreCase("deposit")) {
					money.getDepositCmd().runAdminCmd(sender, args);
				} else if (args[0].equalsIgnoreCase("withdraw")) {
					money.getWithdrawCmd().runAdminCmd(sender, args);
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
		if (money.is19Server == true) {
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("ANVIL_LAND"), 1.0F, 1.0F);
		}
		for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.Title")) {
			p.sendMessage(s.replaceAll("&", "�"));
		}
		if (p.hasPermission("MysqlEconomyBank.admin")) {
			for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.Admin")) {
				p.sendMessage(s.replaceAll("&", "�"));
			}
		} else if (p.hasPermission("MysqlEconomyBank.balance") || p.hasPermission("MysqlEconomyBank.deposit") || p.hasPermission("MysqlEconomyBank.withdraw")) {
			if (p.hasPermission("MysqlEconomyBank.balance")) {
				for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.BalancePerm")) {
					p.sendMessage(s.replaceAll("&", "�"));
				}
			}
			if (p.hasPermission("MysqlEconomyBank.deposit")) {
				for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.DepositPerm")) {
					p.sendMessage(s.replaceAll("&", "�"));
				}
			}
			if (p.hasPermission("MysqlEconomyBank.withdraw")) {
				for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.WithdrawPerm")) {
					p.sendMessage(s.replaceAll("&", "�"));
				}
			}
		} else {
			for (String s : money.getConfigurationHandler().getStringList("chatMessages.playerHelpMessage.NoPerms")) {
				p.sendMessage(s.replaceAll("&", "�"));
			}
		}
	}
	
	public void sendConsoleHelp(CommandSender sender) {
		for (String s : money.getConfigurationHandler().getStringList("chatMessages.consoleHelpMsg")) {
			sender.sendMessage(s.replaceAll("&", "�"));
		}
	}

}
