package alisolarflare.thebuttonrebirth.listeners;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.tasks.DisplayUpdateTask;

public class DisplayUpdateListener implements Listener{
	private final ButtonRebirthPlugin plugin;
	public BukkitTask displayUpdateTask;
	
	public DisplayUpdateListener (ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		displayUpdateTask = new DisplayUpdateTask (this.plugin).runTaskTimer(this.plugin, 20, 20);
	}
}
