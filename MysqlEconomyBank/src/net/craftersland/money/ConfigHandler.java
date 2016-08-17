package net.craftersland.money;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConfigHandler {
	
	private Money money;

	//Config Generation and load
	public ConfigHandler(Money money) {
		this.money = money;
		if (!(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"config.yml").exists())) {
			Money.log.info("No config file found! Creating new one...");
			money.saveDefaultConfig();
		}
		try {
			money.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"config.yml"));
		} catch (Exception e) {
			Money.log.info("Could not load config file!");
			e.printStackTrace();
		}
	}
	
	//Get config contents by strings
	public String getString(String key) {
		if (!money.getConfig().contains(key)) {
			money.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the MysqlEconomyBank folder! (Try generating a new one by deleting the current)");
			return "errorCouldNotLocateInConfigYml:"+key;
		} else {
			if (key.toLowerCase().contains("color")) {
				return "§"+money.getConfig().getString(key);
			}
			return money.getConfig().getString(key);
		}
	}
	
	public Integer getInteger(String key) {
		if (!money.getConfig().contains(key)) {
			money.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the MysqlEconomyBank folder! (Try generating a new one by deleting the current)");
			return 0;
		} else {
			return money.getConfig().getInt(key);
		}
	}
	
	//Send player messages using the config predefined messages
	public void printMessage(Player player, String messageKey, String amount, Player player2, String player2Name) {
		if (money.getConfig().contains(messageKey)) {
			List<String> message = new ArrayList<String>();
			message.add(money.getConfig().getString(messageKey));
			
			//if message is set to '' cancel the message
			if (getString(messageKey).equals("")) {
				return;
			}
			
			if (player2 != null) {
				if (!player2.equals("")) {
					message.set(0, message.get(0).replaceAll("%player2", player2Name));
				}
			}
			
			DecimalFormat f = new DecimalFormat("#,##0.00");
			
			if (amount != null && !amount.equals("")) {
				Double amountDouble = Double.parseDouble(amount);
				if (amountDouble.toString().endsWith(".0")) {
					DecimalFormat fr = new DecimalFormat("#,##0");
					message.set(0, message.get(0).replaceAll("%amount", "" + fr.format(amountDouble)));
				} else {
				message.set(0, message.get(0).replaceAll("%amount", "" + f.format(amountDouble)));
				}
			}

			message.set(0, message.get(0).replaceAll("%pocket", ""+Money.econ.getBalance(player)));
			
			if (money.getMoneyDatabaseInterface().hasAccount(player2)) 
			{
				if (money.getMoneyDatabaseInterface().getBalance(player2).toString().endsWith(".0")) {
					DecimalFormat fr = new DecimalFormat("#,##0");
					message.set(0, message.get(0).replaceAll("%balance", "" + fr.format(money.getMoneyDatabaseInterface().getBalance(player2))));
				} else 
				message.set(0, message.get(0).replaceAll("%balance", "" + f.format(money.getMoneyDatabaseInterface().getBalance(player2))));
				} else {
				message.set(0, message.get(0).replaceAll("%balance", "0.00"));
				}
				
			message.set(0, message.get(0).replaceAll("%player", player.getName()));
			
			if (player != null) {				
				//Message format
				player.sendMessage(parseFormattingCodes(getString("chatMessages.prefix")) + parseFormattingCodes(message.get(0)));
				for (int i = 1; i < message.size(); i++) {
					player.sendMessage(parseFormattingCodes(message.get(i)));
				}
			}
			
		} else {
			money.getLogger().severe("Could not locate '"+messageKey+"' in the config.yml inside of the MysqlEconomyBank folder!");
			player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not locate '"+messageKey+"' in the config.yml inside of the MysqlEconomyBank folder!");
		}
	}
	
	//Color and Format codes support
	public String parseFormattingCodes(String message) {
		message = message.replaceAll("&0", ChatColor.BLACK + "");
		message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
		message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
		message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
		message = message.replaceAll("&4", ChatColor.DARK_RED + "");
		message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		message = message.replaceAll("&6", ChatColor.GOLD + "");
		message = message.replaceAll("&7", ChatColor.GRAY + "");
		message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
		message = message.replaceAll("&9", ChatColor.BLUE + "");
		message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
		message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
		message = message.replaceAll("(?i)&c", ChatColor.RED + "");
		message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
		message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
		message = message.replaceAll("(?i)&l", ChatColor.BOLD + "");
		message = message.replaceAll("(?i)&o", ChatColor.ITALIC + "");
		message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "");
		message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE + "");
		message = message.replaceAll("(?i)&k", ChatColor.MAGIC + "");
		message = message.replaceAll("(?i)&r", ChatColor.RESET + "");
		return message;
	}
	
	//Send action bar message. Requires TitleManager.
	public void actionBarMessage(Player player, String messageKey) {
		if (money.getConfig().contains(messageKey)) {
			List<String> message = new ArrayList<String>();
			message.add(money.getConfig().getString(messageKey));
			
			//if message is set to '' cancel the message
			if (message.get(0) == "") {
				return;
			}
			//replace placeholders
			DecimalFormat f = new DecimalFormat("#,##0.00");
			if (money.getMoneyDatabaseInterface().getBalance(player).toString().endsWith(".0")) {
				DecimalFormat fr = new DecimalFormat("#,##0");
				message.set(0, message.get(0).replaceAll("%bankBalance", "" + fr.format(money.getMoneyDatabaseInterface().getBalance(player))));
			} else {
				message.set(0, message.get(0).replaceAll("%bankBalance", "" + f.format(money.getMoneyDatabaseInterface().getBalance(player))));
			}
			
			if (Double.toString(Money.econ.getBalance(player)).endsWith(".0")) {
				DecimalFormat fr = new DecimalFormat("#,##0");
				message.set(0, message.get(0).replaceAll("%pocketBalance", "" + fr.format(Money.econ.getBalance(player))));
			} else {
				message.set(0, message.get(0).replaceAll("%pocketBalance", "" + f.format(Money.econ.getBalance(player))));
			}
			//send the action bar message
			if (player != null) {
			ActionbarTitleObject object = new ActionbarTitleObject(parseFormattingCodes(message.get(0)));
			object.send(player);
			return;
			}
			
		} else {
			money.getLogger().severe("Could not locate '"+messageKey+"' in the config.yml inside of the MysqlEconomyBank folder!");
			player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not locate '"+messageKey+"' in the config.yml inside of the MysqlEconomyBank folder!");
			return;
		}
		
	}

}
