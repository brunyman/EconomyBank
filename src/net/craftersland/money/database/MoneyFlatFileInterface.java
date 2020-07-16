package net.craftersland.money.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class MoneyFlatFileInterface implements AccountDatabaseInterface<Double> {
	
	private Money money;

	public MoneyFlatFileInterface(Money money) {
		this.money = money;
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		(new File("plugins"+System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml")).exists();
		
		
		return (new File("plugins"+System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml")).exists();
	}
	
	@Override
	public boolean hasAccount(UUID playerUUID) {
		return (new File("plugins"+System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + playerUUID.toString() + ".yml")).exists();
	}

	@Override
	public boolean createAccount(Player player) {
		try {
			File accountFile = new File("plugins" + System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
			accountFile.createNewFile();
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Balance: 0");
			bw.close();
			fw.close();
			return true;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not create Account file " + player.getName() + "!");
		}
		return false;
	}

	@Override
	public Double getBalance(OfflinePlayer player) {
		if (!hasAccount(player)) {
			createAccount(player.getPlayer());
		}
		
		try {
			File accountFile = new File("plugins" + System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Double balance = Double.parseDouble(br.readLine().split(":")[1]);
			br.close();
			fr.close();
			return balance;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not get Balance of " + player.getName() + "!");
		}
		return null;
	}
	
	@Override
	public Double getBalance(UUID playerUUID) {
		try {
			File accountFile = new File("plugins" + System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + playerUUID.toString() + ".yml");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Double balance = Double.parseDouble(br.readLine().split(":")[1]);
			br.close();
			fr.close();
			return balance;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not get Balance of " + playerUUID + "!");
		}
		return null;
	}

	@Override
	public boolean setBalance(OfflinePlayer player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player.getPlayer());
		}
		
		try {
			File accountFile = new File("plugins" + System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts"+System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			String balances = br.readLine();
			br.close();
			fr.close();
			
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(balances.split(":")[0]+": "+amount);
			bw.close();
			fw.close();
			
			return true;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not set Balance of "+player.getName()+"!");
		}
		return false;
	}
	
	@Override
	public boolean setBalance(UUID playerUUID, Double amount) {	
		try {
			File accountFile = new File("plugins" + System.getProperty("file.separator") + "MysqlEconomyBank" + System.getProperty("file.separator") + "Accounts"+System.getProperty("file.separator") + playerUUID.toString() + ".yml");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			String balances = br.readLine();
			br.close();
			fr.close();
			
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(balances.split(":")[0]+": "+amount);
			bw.close();
			fw.close();
			
			return true;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not set Balance of " + playerUUID + "!");
		}
		return false;
	}
}
