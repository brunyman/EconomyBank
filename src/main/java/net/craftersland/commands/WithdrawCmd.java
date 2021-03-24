package net.craftersland.commands;

import net.craftersland.Money;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCmd {
	
	private Money m;
	
	public WithdrawCmd(Money m) {
		this.m = m;
	}
	
	public boolean runUserCmd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.cmd.withdraw")) {
				//check if player is in cooldown
    			if (m.cooldown.contains(p.getUniqueId())) {
    				m.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
    				m.getSoundHandler().sendPlingSound(p);
    				return true;
    			}
    			Double bankBalance = m.getMoneyDatabaseInterface().getBalance(p);
    			Double amount = 0.0;
    			try {
    	        	amount = Double.parseDouble(args[1]);
    	        } catch (Exception e) {
    	        	p.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
    	        	m.getSoundHandler().sendPlingSound(p);
    	        	return true;
    	        }
    			if (amount <= 0) {
    				m.getConfigurationHandler().printMessage(p, "chatMessages.negativeAmount", amount + "", p, p.getName());
        			m.getSoundHandler().sendPlingSound(p);
    				return true;
    			}
    			if (bankBalance >= amount) {
    				if (Money.econ.getBalance(p) + amount > Double.parseDouble(m.getConfigurationHandler().getString("general.maxPocketLimitMoney"))) {
    					m.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInPocket", amount + "", p, p.getName());
    					m.getSoundHandler().sendPlingSound(p);
    					return true;
    				}
    				m.getMoneyDatabaseInterface().setBalance(p, bankBalance - amount);
    				final double fAmount = amount;
    				Bukkit.getScheduler().runTask(m, new Runnable() {

						@Override
						public void run() {
							Money.econ.depositPlayer(p, fAmount);
							//Send Action Bar message. Requires TitleManager
		    				if (m.is19Server || m.setupTitleManager() == true) {
		    					m.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
		    				}
						}
    					
    				});
    				m.getConfigurationHandler().printMessage(p, "chatMessages.withdrewSuccessfully", amount + "", p, p.getName());
    				m.getSoundHandler().sendClickSound(p);
    				//add player to cooldown
    				m.cooldown.add(p.getUniqueId());
					Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(m.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"));
					int delay = delayCalc.intValue();
					Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(m, new Runnable() {
                        public void run() {
                        	//remove player from cooldown
                        	m.cooldown.remove(p.getUniqueId());
                        }
                }, delay);
    				return true;
    			}
    			m.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInAccount", bankBalance.toString(), p, p.getName());
    			m.getSoundHandler().sendPlingSound(p);
    			return true;
			} else {
				m.getSoundHandler().sendPlingSound(p);
				m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return true;
			}
		} else {
			sender.sendMessage("You cant run this command from console, it's for players only!");
		}
		return true;
	}
	
	public boolean runAdminCmd(CommandSender sender, String[] args) {
	    if (sender instanceof Player) {
	      Player p = (Player) sender;
	      if (p.hasPermission("MysqlEconomyBank.admin")) {
	        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
	        Double amount = 0.0;
	        try {
	        	amount = Double.parseDouble(args[1]);
	        } catch (Exception e) {
	        	p.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
	        	m.getSoundHandler().sendPlingSound(p);
	        	return true;
	        }
	        if (amount <= 0) {
				m.getConfigurationHandler().printMessage(p, "chatMessages.negativeAmount", amount + "", p, p.getName());
				m.getSoundHandler().sendPlingSound(p);
				return true;
			}
	        if (target != null && m.getMoneyDatabaseInterface().hasAccount(target)) {
	          double balance = m.getMoneyDatabaseInterface().getBalance(target);
	          if (balance >= amount.doubleValue()) {
	            m.getMoneyDatabaseInterface().setBalance(target, balance - amount);
	            m.getSoundHandler().sendLevelUpSound(p);
	            m.getConfigurationHandler().printMessage(p, "chatMessages.withdrewSuccessfully", amount.toString(), p, p.getName());
	          } else {
	            m.getSoundHandler().sendPlingSound(p);
	            m.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInAccount", amount.toString(), p, p.getName());
	          }
	        } else {
	        	m.getSoundHandler().sendPlingSound(p);
	            m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
	            return true;
	        }
	      } else {
	        m.getSoundHandler().sendPlingSound(p);
	        m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
	        return true;
	      }
	    } else {
	      OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
	      Double amount = 0.0;
	      try {
	        	amount = Double.parseDouble(args[1]);
	        } catch (Exception e) {
	        	sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
	        	return true;
	        }
	      if (target != null && m.getMoneyDatabaseInterface().hasAccount(target)) {
	        double balance = m.getMoneyDatabaseInterface().getBalance(target);
	        if (balance >= amount) {
	          m.getMoneyDatabaseInterface().setBalance(target, balance - amount);
	          sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.withdrewSuccessfully").replace("%amount", amount.toString()));
	        } else {
	          sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.notEnoughMoneyInAccount").replace("%amount", amount.toString()));
	        }
	      } else {
	    	  sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.accountDoesNotExist"));
	      }
	    }
	    return true;
	  }

}
