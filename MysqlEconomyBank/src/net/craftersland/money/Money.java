package net.craftersland.money;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.craftersland.money.commands.BalanceCmd;
import net.craftersland.money.commands.DepositCmd;
import net.craftersland.money.commands.ReloadCmd;
import net.craftersland.money.commands.SetCmd;
import net.craftersland.money.commands.WithdrawCmd;
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
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Money extends JavaPlugin {
	
	public static Logger log;
	public static Economy econ = null;
	public static Permission perms = null;
	public boolean is19Server = true;
	public String pluginName = "MysqlEconomyBank";
	public Set<UUID> cooldown = new HashSet<UUID>();
	
	private ConfigHandler cH;
	private DatabaseManagerInterface databaseManager;
	private AccountDatabaseInterface<Double> moneyDatabaseInterface;
	private boolean enabled = false;
	private SoundHandler sH;
	private ReloadCmd rCmd;
	private BalanceCmd bCmd;
	private SetCmd sCmd;
	private DepositCmd dCmd;
	private WithdrawCmd wCmd;
	
	@Override
    public void onEnable(){
    	log = getLogger();
    	getMcVersion();	
    	
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
        cH = new ConfigHandler(this);
        sH = new SoundHandler(this);
        
      //Setup Database
        if (cH.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
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
        	log.info(pluginName + " loaded successfully!");
        	databaseManager = new DatabaseManagerFlatFile(this);
        	moneyDatabaseInterface = new MoneyFlatFileInterface(this);
        }
        rCmd = new ReloadCmd(this);
        bCmd = new BalanceCmd(this);
        sCmd = new SetCmd(this);
        dCmd = new DepositCmd(this);
        wCmd = new WithdrawCmd(this);
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
    	enabled = true;
    	log.info("MysqlEconomyBank has been successfully loaded!");
	}
	
	@Override
	public void onDisable() {
		if (enabled == true) {
			Bukkit.getScheduler().cancelTasks(this);
			HandlerList.unregisterAll(this);
			//Closing database connection
			if (databaseManager.getConnection() != null) {
				log.info("Closing MySQL connection...");
				databaseManager.closeDatabase();
			}
		}
		log.info(pluginName + " is disabled!");
    }
	
	private boolean getMcVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
	    String version = serverVersion[0];
	    
	    if (version.matches("1.7.10") || version.matches("1.7.9") || version.matches("1.7.5") || version.matches("1.7.2") || version.matches("1.8.8") || version.matches("1.8.7") || version.matches("1.8.3") || version.matches("1.8.4") || version.matches("1.8")) {
	    	is19Server = false;
	    	return true;
	    } else {
	    	log.warning("Possible incompatible server version detected: " + version + " .Attempting to run in 1.9 and 1.10 compatibility mode! If you have errors report them.");
	    }
	    return false;
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
    	if (getServer().getPluginManager().getPlugin("TitleManager") != null) {
        	return true;
        }
          else {
        	  return false;        	  
          }
    }
    
    //Interest task
    public void interestTask() {
    	int time = Integer.parseInt(getConfigurationHandler().getString("general.interest.interestTime"));
    	
    	Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
    		public void run() {
    			List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
    			if (onlinePlayers.isEmpty() == false) {
    				for (Player p : onlinePlayers) {            			
            			//if (econ.get)
            			Double intPercentage = Double.parseDouble(getConfigurationHandler().getString("general.interest.percentageAmount").replace("%", ""));
            			Double balance = getMoneyDatabaseInterface().getBalance(p);
            			
            			if (balance < getConfigurationHandler().getInteger("general.maxBankLimitMoney")) {
            				Double interest = (balance / 100) * intPercentage;
                			
                			getMoneyDatabaseInterface().setBalance(p, balance + interest);
                			getConfigurationHandler().printMessage(p, "chatMessages.interest", interest.toString(), p, p.getName());
            			}            			
        			}
        			
        			onlinePlayers.clear();
    			}
    		}
    	}, time * 1200L, time * 1200L);
    }
    
  //Getter for Database Interfaces
    public AccountDatabaseInterface<Double> getMoneyDatabaseInterface() {
    	return moneyDatabaseInterface;
    }
    public ConfigHandler getConfigurationHandler() {
		return cH;
	}
    public DatabaseManagerInterface getDatabaseManagerInterface() {
		return databaseManager;
	}
    public SoundHandler getSoundHandler() {
    	return sH;
    }
    public ReloadCmd getReloadCmd() {
    	return rCmd;
    }
    public BalanceCmd getBalanceCmd() {
    	return bCmd;
    }
    public SetCmd getSetCmd() {
    	return sCmd;
    }
    public DepositCmd getDepositCmd() {
    	return dCmd;
    }
    public WithdrawCmd getWithdrawCmd() {
    	return wCmd;
    }

}
