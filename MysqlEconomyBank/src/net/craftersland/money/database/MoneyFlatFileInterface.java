package net.craftersland.money.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

import net.craftersland.money.Money;

public class MoneyFlatFileInterface implements AccountDatabaseInterface<Double> {
	
	private Money money;

	public MoneyFlatFileInterface(Money money) {
		this.money = money;
	}
	
	@Override
	public boolean hasAccount(UUID player) {
		return (new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".yml")).exists();
	}

	@Override
	public boolean createAccount(UUID player) {
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".yml");
			accountFile.createNewFile();
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Balance: 0");
			bw.close();
			fw.close();
			return true;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not create Account file " + player + "!");
		}
		return false;
	}

	@Override
	public Double getBalance(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".yml");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Double balance = Double.parseDouble(br.readLine().split(":")[1]);
			br.close();
			fr.close();
			return balance;
			
		} catch (Exception e) {
			money.getLogger().severe("Could not get Balance of "+player+"!");
		}
		return null;
	}

	@Override
	public boolean setBalance(UUID player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".yml");
			
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
			money.getLogger().severe("Could not set Balance of "+player+"!");
		}
		return false;
	}

	@Override
	public boolean addToAccount(UUID player, Double amount) {
		if (amount < 0) {
			return removeFromAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance <= Double.MAX_VALUE-amount) {
			setBalance(player, currentBalance+amount);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeFromAccount(UUID player, Double amount) {

		if (amount < 0) {
			return addToAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance-amount >= -Double.MAX_VALUE) {
			setBalance(player, currentBalance-amount);
			return true;
		}
		return false;
	}

}
