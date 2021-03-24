package net.craftersland.database;

import java.sql.Connection;

import net.craftersland.Money;

public class DatabaseManagerFlatFile implements DatabaseManagerInterface {
	
	@SuppressWarnings("unused")
	private Money money;

	public DatabaseManagerFlatFile(Money money) {
		this.money = money;
		
		setupDatabase();
	}

	@Override
	public boolean setupDatabase() {
		return true;
	}

	@Override
	public boolean closeDatabase() {
		return true;
	}
	
	@Override
	public Connection getConnection() {
		return null;
	}

}
