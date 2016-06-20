package alisolarflare.thebuttonrebirth.listeners;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.tasks.CyclicalDisplayTask;

public class CyclicalDisplayListener implements Listener{
	private final ButtonRebirthPlugin plugin;
	public CyclicalDisplayTask cyclicalDisplayTask;
	public BukkitTask bukkitTask;
	
	public CyclicalDisplayListener (ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		cyclicalDisplayTask = new CyclicalDisplayTask (this.plugin);
		bukkitTask = cyclicalDisplayTask.runTaskTimer(this.plugin, 20, 20);
	}
}
