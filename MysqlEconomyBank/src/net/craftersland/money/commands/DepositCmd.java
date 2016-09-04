package net.craftersland.money.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class DepositCmd {
	
	private Money m;
	
	public DepositCmd(Money m) {
		this.m = m;
	}
	
	public boolean runUserCmd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.cmd.deposit")) {
				if (m.cooldown.contains(p.getUniqueId())) {
    				m.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
    				m.getSoundHandler().sendPlingSound(p);
    				return true;
    			}
    			Double amount = Double.parseDouble(args[1]);
    			if (Money.econ.getBalance(p) >= amount) {
    				Double bankBalance = m.getMoneyDatabaseInterface().getBalance(p);
    				if (bankBalance + amount > Double.parseDouble(m.getConfigurationHandler().getString("general.maxBankLimitMoney"))) {
    					m.getConfigurationHandler().printMessage(p, "chatMessages.reachedMaximumMoneyInAccount", amount + "", p, p.getName());
    					m.getSoundHandler().sendPlingSound(p);
    					return true;
    				}
    				Money.econ.withdrawPlayer(p, amount);
    				m.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
    				m.getConfigurationHandler().printMessage(p, "chatMessages.depositedSuccessfully", amount + "", p, p.getName());
    				m.getSoundHandler().sendClickSound(p);
    				//Send Action Bar message. Requires TitleManager
    				if (m.setupTitleManager() == true) {
    					m.getConfigurationHandler().actionBarMessage(p, "actionBarMessages.balanceLeft");
    				}
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
			} else {
				m.getSoundHandler().sendPlingSound(p);
				m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return true;
			}
		} else {
			sender.sendMessage("You cant run this command from console!");
		}
		}
		return false;
	}
	
	public boolean runAdminCmd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("MysqlEconomyBank.admin")) {
				//TODO
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					
				} else {
					
				}
			} else {
				m.getSoundHandler().sendPlingSound(p);
				m.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				return true;
			}
		} else {
			//TODO
		}
		return false;
	}

}
