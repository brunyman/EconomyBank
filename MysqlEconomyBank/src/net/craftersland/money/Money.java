package net.craftersland.money;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.craftersland.money.database.AccountDatabaseInterface;
import net.craftersland.money.database.DatabaseManagerFlatFile;
import net.craftersland.money.database.DatabaseManagerInterface;
import net.craftersland.money.database.DatabaseManagerMysql;
import net.craftersland.money.database.MoneyFlatFileInterface;
import net.craftersland.money.database.MoneyMysqlInterface;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Money extends JavaPlugin {
	
	public static Logger log;
	public static Economy econ = null;
	public static Permission perms = null;
	public ArrayList<Player> onlinePlayers = new ArrayList<Player>();
	public HashMap<UUID, Double> onlinePlayersBalance = new HashMap<UUID, Double>();
	
	private ConfigurationHandler configurationHandler;
	private DatabaseManagerInterface databaseManager;
	private AccountDatabaseInterface<Double> moneyDatabaseInterface;
	
	@Override
    public void onEnable(){
    	log = getLogger();
    	log.info("Loading MysqlEconomyBank v" + getDescription().getVersion()+"... ");    	
    	
    	//Setup Vault for economy and permissions
        if (!setupEconomy() ) {
            log.severe("Warning! Vault installed? If yes Economy system installed?)");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        
        //Setup TitleManager optional dependency
        if (setupTitleManager() == false) {
        	log.warning("Failed to hook into TitleManager, disabling action bar messages.");
        } else {
        	log.info("Successfully hooked into TitleManager!");
        }
        
      //Create MysqlEconomyBridge folder
        if (!new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank").exists()) {
    	(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank")).mkdir();
        }
    	
    	//Load Configuration
        configurationHandler = new ConfigurationHandler(this);
        
      //Setup Database
        if (configurationHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
        	log.info("Using MySQL as Datasource...");
        	databaseManager = new DatabaseManagerMysql(this);
        	moneyDatabaseInterface = new MoneyMysqlInterface(this);
        	//If mysql connection fails disable plugin
        	if (databaseManager.getConnection() == null)
        	{
        		getServer().getPluginManager().disablePlugin(this);
                return;
        	}
        } else {
        	//Go for FlatFile
        	if (!new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts").exists()) {
        		(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts")).mkdir();
        	}
        	log.info("Using FlatFile as Datasource...");
        	databaseManager = new DatabaseManagerFlatFile(this);
        	moneyDatabaseInterface = new MoneyFlatFileInterface(this);
        }
        
      //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerListener(this), this);
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("meb").setExecutor(cH);
    	getCommand("bank").setExecutor(cH);
    	
    	//Start interest task
    	if (getConfigurationHandler().getString("general.interest.enabled") == "true") {
    		interestTask();
    		log.info("Interest task started.");
    	} else {
    		log.info("Interest task is disabled.");
    	}
    	
    	log.info("MysqlEconomyBank has been successfully loaded!");
	}
	
	@Override
	public void onDisable() {
		if (this.isEnabled()) {
			//Closing database connection
			if (databaseManager.getConnection() != null) {
				log.info("Closing MySQL connection...");
				databaseManager.closeDatabase();
			}
		}
    	log.info("MysqlEconomyBank has been disabled");
    }
	
	//Methods for setting up Vault
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        log.info("Using economy system: " + rsp.getProvider().getName());
        return econ != null;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        log.info("Using permission system: " + rsp.getProvider().getName());
        return perms != null;
    }
    //Check if TitleManager is available
    public boolean setupTitleManager() {
    	if (getServer().getPluginManager().getPlugin("TitleManager") != null && getServer().getPluginManager().getPlugin("TitleManager").isEnabled()) {
        	return true;
        }
          else {
        	  return false;        	  
          }
    }
    
    //Interest task
    @SuppressWarnings("deprecation")
	public void interestTask() {
    	int time = Integer.parseInt(getConfigurationHandler().getString("general.interest.interestTime"));
    	
    	Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
    		public void run() {
    			onlinePlayers.addAll(Bukkit.getOnlinePlayers());
    			if (onlinePlayers.isEmpty() == true) return;
    			
    			for (int i = 0; i < onlinePlayers.size(); i++) {
    				
        			Player p = onlinePlayers.get(i);
        			UUID u = p.getUniqueId();
        			
        			Double intPercentage = Double.parseDouble(getConfigurationHandler().getString("general.interest.percentageAmount").replace("%", ""));
        			Double balance = getMoneyDatabaseInterface().getBalance(u);
        			Double interest = (balance / 100) * intPercentage;
        			
        			getMoneyDatabaseInterface().addToAccount(u, interest);
        			getConfigurationHandler().printMessage(p, "chatMessages.interest", interest.toString(), p.getUniqueId(), p.getName());
        			
    			}
    			
    			onlinePlayers.clear();
    		}
    	}, time * 1200L, time * 1200L);
    }
    
  //Getter for Database Interfaces
    public AccountDatabaseInterface<Double> getMoneyDatabaseInterface() {
    	return moneyDatabaseInterface;
    }
    
    public ConfigurationHandler getConfigurationHandler() {
		return configurationHandler;
	}
    
    public DatabaseManagerInterface getDatabaseManagerInterface() {
		return databaseManager;
	}

}
