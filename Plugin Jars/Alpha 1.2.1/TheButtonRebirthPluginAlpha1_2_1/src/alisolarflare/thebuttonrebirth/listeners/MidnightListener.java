package alisolarflare.thebuttonrebirth.listeners;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.tasks.CheckChestTask;

public class MidnightListener implements Listener{
	private final ButtonRebirthPlugin plugin;
	public BukkitTask checkChestTask;
	
	public MidnightListener(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		checkChestTask = new CheckChestTask(this.plugin,this.plugin).runTaskTimer(this.plugin, 20, 60);
	}
}
