package net.craftersland.money.commands;

import net.craftersland.money.Money;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        Double amount = Double.valueOf(Double.parseDouble(args[1]));
        if (Money.econ.getBalance(p) >= amount.doubleValue()) {
          Double bankBalance = m.getMoneyDatabaseInterface().getBalance(p);
          if (bankBalance.doubleValue() + amount.doubleValue() > Double.parseDouble(m.getConfigurationHandler().getString("general.maxBankLimitMoney"))) {
            m.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInAccount", amount + "", p, p.getName());
            m.getSoundHandler().sendPlingSound(p);
            return true;
          }
          Money.econ.withdrawPlayer(p, amount.doubleValue());
          m.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
          m.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
          m.getSoundHandler().sendClickSound(p);
          if (m.setupTitleManager()) {
            m.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
          }
          m.cooldown.add(p.getUniqueId());
          Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(m.getConfigurationHandler().getString("general.timeBetweenTwoInteractions")));
          int delay = delayCalc.intValue();
          Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(m, new Runnable() {
            public void run() {
              m.cooldown.remove(p.getUniqueId());
            }
          }, delay);
        } else {
          m.getConfigurationHandler().printMessage(p, "chatMessages.notEnoughMoneyInPoket", "0", p, p.getName());
          m.getSoundHandler().sendPlingSound(p);
        }
      } else {
        m.getSoundHandler().sendPlingSound(p);
        m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
        return true;
      }
    } else {
      sender.sendMessage("You cant run this command from console!");
    }
    return false;
  }
  
  public boolean runAdminCmd(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (p.hasPermission("MysqlEconomyBank.admin")) {
        Player target = Bukkit.getPlayer(args[2]);
        Double amount = Double.parseDouble(args[1]);
        if (target != null) {
          double balance = this.m.getMoneyDatabaseInterface().getBalance(target).doubleValue();
          m.getMoneyDatabaseInterface().setBalance(target, Double.valueOf(balance + amount.doubleValue()));
          m.getSoundHandler().sendLevelUpSound(p);
          m.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
        } else {
          p.sendMessage(ChatColor.RED + "Not implemented for offline players!");
        }
      } else {
        this.m.getSoundHandler().sendPlingSound(p);
        this.m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
        return true;
      }
    } else {
      Player target = Bukkit.getPlayer(args[2]);
      Double amount = Double.parseDouble(args[1]);
      if (target != null) {
        double balance = m.getMoneyDatabaseInterface().getBalance(target);
        m.getMoneyDatabaseInterface().setBalance(target, Double.valueOf(balance + amount.doubleValue()));
        sender.sendMessage(m.getConfigurationHandler().getString("chatMessages.depositedSuccessfully").replace("%amount", amount.toString()));
      } else {
        sender.sendMessage(ChatColor.RED + "Not implemented for offline players!");
      }
    }
    return false;
  }
}
