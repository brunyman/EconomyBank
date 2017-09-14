package net.craftersland.money;

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
		} else if (p.hasPermission("MysqlEconomyBank.balance") || p.hasPermission("MysqlEconomyBank.deposit") || p.hasPermission("MysqlEconomyBank.withdraw")) {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.AQUA + "        Check your bank balance:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank balance");
			p.sendMessage(ChatColor.AQUA + "        Deposit money in your bank:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank deposit <amount>");
			p.sendMessage(ChatColor.AQUA + "        Withdraw money from your bank:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/bank withdraw <amount>");
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
