package net.craftersland;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler {
	
	private Money plugin;
	
	public SoundHandler(Money m) {
		this.plugin = m;
	}
	
	public void sendItemBreakSound(Player p) {
		if (plugin.is19Server == true) {
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1.0F, 1.0F);
		}
	}
	
	public void sendClickSound(Player p) {
		if (plugin.is19Server == true) {
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("CLICK"), 1.0F, 1.0F);
		}
	}
	
	public void sendLevelUpSound(Player p) {
		if (plugin.is19Server == true) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
		}
	}
	
	public void sendPlingSound(Player p) {
		if (plugin.is13Server) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 3.0F);
		} else if (plugin.is19Server == true) {
			p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 3.0F, 3.0F);
		} else {
			p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3.0F, 3.0F);
		}
	}

}
