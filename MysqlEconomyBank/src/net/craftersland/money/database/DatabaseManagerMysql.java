package net.craftersland.money.database;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.craftersland.money.Money;

public class DatabaseManagerMysql implements DatabaseManagerInterface{
	
	private Connection conn = null;
	  
	// Hostname
	private String dbHost;
	// Port -- Standard: 3306
	private String dbPort;
	// Databankname
	private String database;
	// Databank username
	private String dbUser;
	// Databank password
	private String dbPassword;

	private Money money;
	
	public String dataTableName;
	
	public DatabaseManagerMysql(Money money) {
		this.money = money;
		
		dataTableName = money.getConfigurationHandler().getString("database.mysql.tableName");
		
		setupDatabase();
	}
	
	@Override
	public boolean setupDatabase() {
		try {
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            
            dbHost = money.getConfigurationHandler().getString("database.mysql.host");
            dbPort = money.getConfigurationHandler().getString("database.mysql.port");
            database = money.getConfigurationHandler().getString("database.mysql.databaseName");
            dbUser = money.getConfigurationHandler().getString("database.mysql.user");
            dbPassword = money.getConfigurationHandler().getString("database.mysql.password");
            
            String passFix = dbPassword.replaceAll("%", "%25");
            String passFix2 = passFix.replaceAll("\\+", "%2B");
            
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
            Money.log.warning("Database connection established!");
          } catch (ClassNotFoundException e) {
        	  Money.log.severe("Could not locate drivers for mysql!");
            return false;
          } catch (SQLException e) {
        	  Money.log.severe("Could not connect to mysql database!");
            return false;
          }
		
		//Create tables if needed
	      Statement query = null;
	      try {
	        query = conn.createStatement();
	        
	        String accounts = "CREATE TABLE IF NOT EXISTS `" + dataTableName + "` (id int(10) AUTO_INCREMENT, player_uuid varchar(50) NOT NULL UNIQUE, player_name varchar(50) NOT NULL, money double(30,2) NOT NULL, last_seen varchar(30) NOT NULL, sync_complete varchar(5) NOT NULL, PRIMARY KEY(id));";
	        query.executeUpdate(accounts);
	      } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	      } finally {
	    	  if (query != null) {
	    		  try {
	    			  query.close();
	    		  } catch (Exception e) {
	    			  e.printStackTrace();
	    		  }
	    	  }
	      }
	      
	      //Update Tables
	      updateTables();
		return true;
	}
	
	@Override
	public Connection getConnection() {
		checkConnection();
		return conn;
	}
	
	public boolean checkConnection() {
		try {
			if (conn == null) {
				Money.log.warning("Connection failed. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			if (!conn.isValid(3)) {
				Money.log.warning("Connection is idle or terminated. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			if (conn.isClosed() == true) {
				Money.log.warning("Connection is closed. Reconnecting...");
				if (reConnect() == true) return true;
				return false;
			}
			return true;
		} catch (Exception e) {
			Money.log.severe("Could not reconnect to Database!");
		}
		return true;
	}
	
	public boolean reConnect() {
		try {
			dbHost = money.getConfigurationHandler().getString("database.mysql.host");
            dbPort = money.getConfigurationHandler().getString("database.mysql.port");
            database = money.getConfigurationHandler().getString("database.mysql.databaseName");
            dbUser = money.getConfigurationHandler().getString("database.mysql.user");
            dbPassword = money.getConfigurationHandler().getString("database.mysql.password");
            
            String passFix = dbPassword.replaceAll("%", "%25");
            String passFix2 = passFix.replaceAll("\\+", "%2B");
            
            long start = 0;
			long end = 0;
			
		    start = System.currentTimeMillis();
		    Money.log.info("Attempting to establish a connection to the MySQL server!");
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
		    end = System.currentTimeMillis();
		    Money.log.info("Connection to MySQL server established!");
		    Money.log.info("Connection took " + ((end - start)) + "ms!");
            return true;
		} catch (Exception e) {
			Money.log.severe("Could not connect to MySQL server! because: " + e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean closeDatabase() {
		try {
			conn.close();
			conn = null;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void updateTables() {
		if (conn != null) {
			DatabaseMetaData md = null;
	    	ResultSet rs1 = null;
	    	ResultSet rs2 = null;
	    	ResultSet rs3 = null;
	    	PreparedStatement query1 = null;
	    	PreparedStatement query2 = null;
	    	PreparedStatement query3 = null;
	    	try {
	    		md = conn.getMetaData();
	            rs1 = md.getColumns(null, null, dataTableName, "sync_complete");
	            if (rs1.next()) {
			    } else {
			        String data = "ALTER TABLE `" + dataTableName + "` ADD sync_complete varchar(5) NOT NULL DEFAULT 'true';";
			        query1 = conn.prepareStatement(data);
			        query1.execute();
			    }
	            rs2 = md.getColumns(null, null, dataTableName, "player_name");
	            if (rs2.next()) {
			    } else {
			        String data = "ALTER TABLE `" + dataTableName + "` ADD player_name varchar(50) NOT NULL DEFAULT 'true';";
			        query2 = conn.prepareStatement(data);
			        query2.execute();
			    }
	            rs3 = md.getColumns(null, null, dataTableName, "last_seen");
	            if (rs3.next()) {
			    } else {
			        String data = "ALTER TABLE `" + dataTableName + "` ADD last_seen varchar(30) NOT NULL DEFAULT 'true';";
			        query3 = conn.prepareStatement(data);
			        query3.execute();
			    }
	    	} catch (Exception e) {
	    		Money.log.severe("Error updating inventory table! Error: " + e.getMessage());
    			e.printStackTrace();
	    	} finally {
	    		try {
	    			if (query1 != null) {
	    				query1.close();
	    			}
	    			if (query2 != null) {
	    				query2.close();
	    			}
	    			if (query3 != null) {
	    				query3.close();
	    			}
	    			if (rs1 != null) {
	    				rs1.close();
	    			}
	    			if (rs2 != null) {
	    				rs2.close();
	    			}
	    			if (rs3 != null) {
	    				rs3.close();
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
		}
	}

}
