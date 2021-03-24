package net.craftersland.money;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class InterestHandler {
	
	private Money pl;
	private int taskID = -1;
	private long lastInterestTime;
	
	public InterestHandler(Money pl) {
		this.pl = pl;
		interestTask();
	}
	
	public String getNextInterestTime() {
		String timeString = "Error";
		long timeLeftMills = System.currentTimeMillis() - lastInterestTime;
		long timecountSecPass = timeLeftMills / 1000;
		long timecountSec = pl.getConfigurationHandler().getInteger("general.interest.interestTime") * 60 - timecountSecPass;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		if (timecountSec >= 86400) { //It's atleast one day
		    days = (int) (timecountSec / 86400);
		    timecountSec = timecountSec % 86400;
		}
		if (timecountSec >= 3600) {
		    hours = (int) (timecountSec / 3600);
		    timecountSec = timecountSec % 3600;
		}
		if (timecountSec >= 60) {
		    minutes = (int) (timecountSec / 60);
		    timecountSec = timecountSec % 60;
		}
		if (timecountSec > 0) {
		    seconds = (int) timecountSec;
		}
		
		if (days != 0) {
			timeString = days + " days " + hours + " hours " + minutes + " min " + seconds + " sec";
		} else if (days == 0 && hours == 0 && minutes == 0) {
			timeString = seconds + " sec";
		} else if (days == 0 && hours == 0) {
			timeString = minutes + " min " + seconds + " sec";
		} else if (days == 0) {
			timeString = hours + " hours " + minutes + " min " + seconds + " sec";
		}
		return timeString;
	}
	
	public void resetTask() {
		if (taskID != -1) {
			Bukkit.getScheduler().cancelTask(taskID);
		}
		interestTask();
	}
	
	//Interest task
    private void interestTask() {
    	if (pl.getConfigurationHandler().getBoolean("general.interest.enabled") == true) {
    		Money.log.info("Interest task started. Iterest will be given every " + pl.getConfigurationHandler().getInteger("general.interest.interestTime") + " minutes.");
    		BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
    			
        		public void run() {
        			lastInterestTime = System.currentTimeMillis();
        			List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        			if (onlinePlayers.isEmpty() == false) {
        				for (Player p : onlinePlayers) {            			
                			Double intPercentage = Double.parseDouble(pl.getConfigurationHandler().getString("general.interest.percentageAmount").replace("%", ""));
                			Double balance = pl.getMoneyDatabaseInterface().getBalance(p);
                			
                			if (balance < pl.getConfigurationHandler().getInteger("general.maxBankLimitMoney")) {
                				Double interest = (balance / 100) * intPercentage;
                    			
                    			pl.getMoneyDatabaseInterface().setBalance(p, balance + interest);
                    			pl.getConfigurationHandler().printMessage(p, "chatMessages.interest", interest.toString(), p, p.getName());
                			}            			
            			}
            			onlinePlayers.clear();
        			}
        		}
        		
        	}, 20L, pl.getConfigurationHandler().getInteger("general.interest.interestTime") * (60 * 20L));
        	taskID = task.getTaskId();
    	} else {
    		Money.log.info("Interest task is disabled.");
    	}
    }

}
