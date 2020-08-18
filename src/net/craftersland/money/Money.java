package net.craftersland.money;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.craftersland.money.commands.BalanceCmd;
import net.craftersland.money.commands.DepositCmd;
import net.craftersland.money.commands.InterestCmd;
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
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Money extends JavaPlugin {
	
	public static Logger log;
	public static Money instance;
	public static Economy econ = null;
	public static Permission perms = null;
	public boolean is14Server = true;
	public boolean is19Server = true;
	public boolean is13Server = false;
	//public String pluginName = "MysqlEconomyBank";
	public Set<UUID> cooldown = new HashSet<UUID>();
	
	private static ConfigHandler cH;
	private DatabaseManagerInterface databaseManager;
	private AccountDatabaseInterface<Double> moneyDatabaseInterface;
	private boolean enabled = false;
	private static SoundHandler sH;
	private static ReloadCmd rCmd;
	private static BalanceCmd bCmd;
	private static SetCmd sCmd;
	private static DepositCmd dCmd;
	private static WithdrawCmd wCmd;
	private static InterestHandler iH;
	private static InterestCmd iCmd;
	
	@Override
    public void onEnable(){
    	log = getLogger();
    	instance = this;
    	getMcVersion();	
    	
    	//Setup Vault for economy and permissions
        if (!setupEconomy() ) {
            log.severe("Warning! Vault installed? If yes Economy system installed?)");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        
        //Setup TitleManager optional dependency
        if (is19Server == false) {
        	if (setupTitleManager() == false) {
            	log.warning("Failed to hook into TitleManager, disabling action bar messages.");
            } else {
            	log.info("Successfully hooked into TitleManager!");
            }
        }
    	
    	//Load Configuration
        cH = new ConfigHandler(this);
        sH = new SoundHandler(this);
        
        //Setup Database
        if (cH.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
        	log.info("Using MySQL as Datasource...");
        	databaseManager = new DatabaseManagerMysql(this);
        	moneyDatabaseInterface = new MoneyMysqlInterface(this);
        } else {
        	//Go for FlatFile
        	if (!new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts").exists()) {
        		(new File("plugins"+System.getProperty("file.separator")+"MysqlEconomyBank"+System.getProperty("file.separator")+"Accounts")).mkdir();
        	}
        	log.info(instance.getDescription().getName() + " loaded successfully!");
        	databaseManager = new DatabaseManagerFlatFile(this);
        	moneyDatabaseInterface = new MoneyFlatFileInterface(this);
        }
        rCmd = new ReloadCmd(this);
        bCmd = new BalanceCmd(this);
        sCmd = new SetCmd(this);
        dCmd = new DepositCmd(this);
        wCmd = new WithdrawCmd(this);
        iH = new InterestHandler(this);
        iCmd = new InterestCmd(this);
      //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerListener(this), this);
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("meb").setExecutor(cH);
    	getCommand("bank").setExecutor(cH);
    	//Activate placeholders using PlaceholdersAPI
    	if (isPlaceHoldersAPIinstalled()) {
    		new Placeholders(this).register();
    		log.info("PlaceholdersAPI detected and placeholders activated!");
    	}
    	
    	enabled = true;
    	log.info(instance.getDescription().getName() + " has been successfully loaded!");
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
		log.info(instance.getDescription().getName() + " is disabled!");
    }
	
	private boolean getMcVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
	    String version = serverVersion[0];
	    
	    if (version.matches("1.7.10") || version.matches("1.7.9") || version.matches("1.7.5") || version.matches("1.7.2") || version.matches("1.8.8") || version.matches("1.8.7") || version.matches("1.8.3") || version.matches("1.8.4") || version.matches("1.8")) {
	    	is19Server = false;
	    	is13Server = false;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.9") || version.matches("1.9.1") || version.matches("1.9.2") || version.matches("1.9.3") || version.matches("1.9.4")) {
	    	is19Server = true;
	    	is13Server = false;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.10") || version.matches("1.10.1") || version.matches("1.10.2")) {
	    	is19Server = true;
	    	is13Server = false;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.11") || version.matches("1.11.1") || version.matches("1.11.2")) {
	    	is19Server = true;
	    	is13Server = false;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.12") || version.matches("1.12.1") || version.matches("1.12.2")) {
	    	is19Server = true;
	    	is13Server = false;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.13") || version.matches("1.13.1") || version.matches("1.13.2")) {
	    	is19Server = true;
	    	is13Server = true;
	    	is14Server = false;
	    	return true;
	    } else if (version.matches("1.14") || version.matches("1.14.1") || version.matches("1.14.2") || version.matches("1.14.3") || version.matches("1.14.4")) {
	    	is19Server = true;
	    	is13Server = true;
	    	is14Server = true;
	    	return true;
	    } else if (version.matches("1.15") || version.matches("1.15.1") || version.matches("1.15.2")) {
	    	is19Server = true;
	    	is13Server = true;
	    	is14Server = true;
	    	return true;
	    } else if (version.matches("1.16") || version.matches("1.16.1") | version.matches("1.16.2")) {
	    	is19Server = true;
	    	is13Server = true;
	    	is14Server = true;
	    	return true;
	    } else {
	    	is19Server = true;
	    	is13Server = true;
	    	is14Server = true;
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
    
    public boolean isPlaceHoldersAPIinstalled() {
    	if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
        	return true;
        }
          else {
        	  return false;        	  
          }
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
    public InterestHandler getInterestHandler() {
    	return iH;
    }
    public InterestCmd getInterestCmd() {
    	return iCmd;
    }

}
