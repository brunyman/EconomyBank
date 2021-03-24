package net.craftersland.commands;

import net.craftersland.Money;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DepositCmd {
  private Money m;
  
  public DepositCmd(Money m) {
    this.m = m;
  }
  
  public boolean runUserCmd(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      if (p.hasPermission("MysqlEconomyBank.cmd.deposit") || p.hasPermission("MysqlEconomyBank.admin")) {
        if (m.cooldown.contains(p.getUniqueId())) {
          m.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
          m.getSoundHandler().sendPlingSound(p);
          return true;
        }
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
        Double localBalance = Money.econ.getBalance(p);
        if (localBalance >= amount) {
          Double bankBalance = m.getMoneyDatabaseInterface().getBalance(p);
          if (bankBalance.doubleValue() + amount > Double.parseDouble(m.getConfigurationHandler().getString("general.maxBankLimitMoney"))) {
            m.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInAccount", amount + "", p, p.getName());
            m.getSoundHandler().sendPlingSound(p);
            return true;
          }
          final double fAmount = amount;
          Bukkit.getScheduler().runTask(m, new Runnable() {

			@Override
			public void run() {
				Money.econ.withdrawPlayer(p, fAmount);
				if (m.is19Server || m.setupTitleManager()) {
		            m.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
		          }
			}
        	  
          });
          m.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
          m.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
          m.getSoundHandler().sendClickSound(p);
          m.cooldown.add(p.getUniqueId());
          Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(m.getConfigurationHandler().getString("general.timeBetweenTwoInteractions")));
          int delay = delayCalc.intValue();
          Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(m, new Runnable() {
            public void run() {
              m.cooldown.remove(p.getUniqueId());
            }
          }, delay);
        } else {
          m.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInPoket", amount.toString(), p, p.getName());
          m.getSoundHandler().sendPlingSound(p);
        }
      } else {
        m.getSoundHandler().sendPlingSound(p);
        m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
        return true;
      }
    } else {
      sender.sendMessage("You cant run this command from console, it's for players only!");
    }
    return false;
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
          double balance = m.getMoneyDatabaseInterface().getBalance(target).doubleValue();
          m.getMoneyDatabaseInterface().setBalance(target, Double.valueOf(balance + amount.doubleValue()));
          m.getSoundHandler().sendLevelUpSound(p);
          m.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
        } else {
        	m.getConfigurationHandler().printMessage(p, "chatMessages.accountDoesNotExist", "0", null, args[2]);
			m.getSoundHandler().sendPlingSound(p);
        }
      } else {
        this.m.getSoundHandler().sendPlingSound(p);
        this.m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
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
      if (amount <= 0) {
    	    sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.negativeAmount"));
			return true;
		}
      if (target != null && m.getMoneyDatabaseInterface().hasAccount(target)) {
        double balance = m.getMoneyDatabaseInterface().getBalance(target);
        m.getMoneyDatabaseInterface().setBalance(target, Double.valueOf(balance + amount.doubleValue()));
        sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.depositedSuccessfully").replace("%amount", amount.toString()));
      } else {
        sender.sendMessage(m.getConfigurationHandler().getStringWithColor("chatMessages.accountDoesNotExist"));
      }
    }
    return false;
  }
}
