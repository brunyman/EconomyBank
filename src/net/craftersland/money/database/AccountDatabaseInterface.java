package net.craftersland.money.database;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface AccountDatabaseInterface<X> {
	//Accountmethods
	public boolean hasAccount(OfflinePlayer player);
	public boolean hasAccount(UUID player);
	public boolean createAccount(Player player);
	public Double getBalance(OfflinePlayer player);
	public Double getBalance(UUID player);
	public boolean setBalance(OfflinePlayer player, Double amount);
	public boolean setBalance(UUID player, Double amount);
}
