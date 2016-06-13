package alisolarflare.thebuttonrebirth.listeners;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.tasks.StealChestTask;

public class StealChestListener implements Listener{
	private final ButtonRebirthPlugin plugin;
	public BukkitTask stealChestTask;
	
	public StealChestListener(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		stealChestTask = new StealChestTask(this.plugin).runTaskTimer(this.plugin, 20, 20);
	}
}
