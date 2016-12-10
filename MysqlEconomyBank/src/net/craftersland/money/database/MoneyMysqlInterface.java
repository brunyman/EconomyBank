package net.craftersland.money.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class MoneyMysqlInterface implements AccountDatabaseInterface <Double>{
	
	private Money money;
	
	public MoneyMysqlInterface(Money money) {
		this.money = money;
	}
	
	@Override
	public boolean hasAccount(Player player) {
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		      try {		 
		        String sql = "SELECT `player_uuid` FROM `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) {
		        	return true;
		        }
		      } catch (SQLException e) {
		        e.printStackTrace();
		      } finally {
		    	  try {
		    		  if (result != null) {
		    			  result.close();
			    	  }
		    		  if (preparedUpdateStatement != null) {
		    			  preparedUpdateStatement.close();
		    		  }
	    		  } catch (Exception e) {
	    			  e.printStackTrace();
	    		  }
		      }
		      return false;
	}
	
	@Override
	public boolean hasAccount(UUID playerUUID) {
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		      try {		 
		        String sql = "SELECT `player_uuid` FROM `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, playerUUID.toString());
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) {
		        	return true;
		        }
		      } catch (SQLException e) {
		        e.printStackTrace();
		      } finally {
		    	  try {
		    		  if (result != null) {
		    			  result.close();
			    	  }
		    		  if (preparedUpdateStatement != null) {
		    			  preparedUpdateStatement.close();
		    		  }
	    		  } catch (Exception e) {
	    			  e.printStackTrace();
	    		  }
		      }
		      return false;
	}
	
	@Override
	public boolean createAccount(Player player) {
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedStatement = null;
		try {			 
	        String sql = "INSERT INTO `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "`(`player_uuid`, `player_name`, `money`, `last_seen`, `sync_complete`) " + "VALUES(?, ?, ?, ?, ?)";
	        preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, player.getUniqueId().toString());
	        preparedStatement.setString(2, player.getName());
	        preparedStatement.setDouble(3, 0.0);
	        preparedStatement.setString(4, System.currentTimeMillis() + "");
	        preparedStatement.setString(5, "true");
	        
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
	    	  try {
	    		  if (preparedStatement != null) {
	    			  preparedStatement.close();
	    		  }
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
	      }
		return false;
	}
	
	@Override
	public Double getBalance(Player player) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
	      try {	 
	        String sql = "SELECT `money` FROM `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ? LIMIT 1";
	        
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
	        result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return Double.parseDouble(result.getString("money"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
	    	  try {
	    		  if (result != null) {
	    			  result.close();
	    		  }
	    		  if (preparedUpdateStatement != null) {
	    			  preparedUpdateStatement.close();
	    		  }
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
	      }
		return null;
	}
	
	@Override
	public Double getBalance(UUID playerUUID) {
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
	      try {	 
	        String sql = "SELECT `money` FROM `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ? LIMIT 1";
	        
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, playerUUID.toString());
	        result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return Double.parseDouble(result.getString("money"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } finally {
	    	  try {
	    		  if (result != null) {
	    			  result.close();
	    		  }
	    		  if (preparedUpdateStatement != null) {
	    			  preparedUpdateStatement.close();
	    		  }
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
	      }
		return null;
	}
	
	@Override
	public boolean setBalance(Player player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
        try {        	
			String updateSql = "UPDATE `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` " + "SET `money` = ?" + "WHERE `player_uuid` = ?";
			preparedUpdateStatement = conn.prepareStatement(updateSql);
			preparedUpdateStatement.setDouble(1, amount);
			preparedUpdateStatement.setString(2, player.getUniqueId().toString());
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	    	  try {
	    		  if (preparedUpdateStatement != null) {
	    			  preparedUpdateStatement.close();
	    		  }
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
	      }
        return false;
	}
	
	@Override
	public boolean setBalance(UUID playerUUID, Double amount) {
		Connection conn = money.getDatabaseManagerInterface().getConnection();
		PreparedStatement preparedUpdateStatement = null;
        try {        	
			String updateSql = "UPDATE `" + money.getConfigurationHandler().getString("database.mysql.tableName") + "` " + "SET `money` = ?" + "WHERE `player_uuid` = ?";
			preparedUpdateStatement = conn.prepareStatement(updateSql);
			preparedUpdateStatement.setDouble(1, amount);
			preparedUpdateStatement.setString(2, playerUUID.toString());
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	    	  try {
	    		  if (preparedUpdateStatement != null) {
	    			  preparedUpdateStatement.close();
	    		  }
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
	      }
        return false;
	}

}
