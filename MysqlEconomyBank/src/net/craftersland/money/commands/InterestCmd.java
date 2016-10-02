package net.craftersland.money.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftersland.money.Money;

public class InterestCmd {
	
	private Money money;
	
	public InterestCmd(Money m) {
		this.money = m;
	}
	
	public void runUserCmd(CommandSender sender) {
		if (sender instanceof Player) {
			if (money.getConfigurationHandler().getBoolean("general.interest.enabled") == true) {
				final Player p = (Player) sender;
				if (p.hasPermission("MysqlEconomyBank.cmd.interest")) {
					if (money.cooldown.contains(p.getUniqueId())) {
						money.getConfigurationHandler().printMessage(p, "chatMessages.tooFastInteraction", "0", p, p.getName());
						money.getSoundHandler().sendPlingSound(p);
						return;
	    			}
					p.sendMessage(money.getConfigurationHandler().getStringWithColor("chatMessages.interestCommand").replace("%time", money.getInterestHandler().getNextInterestTime()));
					money.getSoundHandler().sendLevelUpSound(p);
					//add player to cooldown
					money.cooldown.add(p.getUniqueId());
					Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(money.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"));
					int delay = delayCalc.intValue();
					Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(money, new Runnable() {
	                    public void run() {
	                    	//remove player from cooldown
	                    	money.cooldown.remove(p.getUniqueId());
	                    }
	            }, delay);
				} else {
					money.getSoundHandler().sendPlingSound(p);
					money.getConfigurationHandler().printMessage(p, "chatMessages.noPermission", "0", p, p.getName());
				}
			} else {
				sender.sendMessage(money.getConfigurationHandler().getStringWithColor("chatMessages.interestDisabled").replace("%time", money.getInterestHandler().getNextInterestTime()));
				money.getSoundHandler().sendPlingSound((Player) sender);
			}
		} else {
			sender.sendMessage("You cant run this command from console!");
		}
	}

}
