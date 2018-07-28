package net.craftersland.money.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class ReloadCmd {
	
	private Money money;
	
	public ReloadCmd(Money m) {
		this.money = m;
	}
	
	public boolean runCmd(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.admin")) {
				try {
					money.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"config.yml"));
				} catch (Exception e) {
					money.getConfigurationHandler().printMessage(p, "chatMessages.reloadFail", "0", p, p.getName());
					money.getSoundHandler().sendPlingSound(p);
					e.printStackTrace();
					return false;
				}
				money.getInterestHandler().resetTask();
				money.getSoundHandler().sendLevelUpSound(p);
				money.getConfigurationHandler().printMessage(p, "chatMessages.reloadComplete", "0", p, p.getName());
				return true;
			}
			money.getSoundHandler().sendPlingSound(p);
			money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
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

}
