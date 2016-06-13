package alisolarflare.thebuttonrebirth;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import alisolarflare.thebuttonrebirth.admin.CreateShrine;
import alisolarflare.thebuttonrebirth.admin.SetWorld;
import alisolarflare.thebuttonrebirth.commands.ButtonHealth;
import alisolarflare.thebuttonrebirth.listeners.DisplayUpdateListener;
import alisolarflare.thebuttonrebirth.listeners.MidnightListener;

public class ButtonRebirthPlugin extends JavaPlugin{
	public void onEnable(){
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();
		logger.info(pdfFile.getName() + " has been started (V." + pdfFile.getVersion()+ ").");
		//grabs default config.yml class, so I can use my custom config.yml class
		this.getConfig();
		this.getConfig();
		
		//Registers Commands and Events
		registerCommands();
		registerEvents();
		
		//Logs Plugin Enabled onto Minecraft Log
		logger.info(pdfFile.getName() + " has been Enabled (V." + pdfFile.getVersion()+ ").");
	}
	public void onDisable(){
		//Logs Plugin Disabled onto Minecraft Log
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();
		logger.info(pdfFile.getName() + " has been Enabled (V." + pdfFile.getVersion()+ ").");
		
	}
	public void registerCommands(){
		//Creates Chest
		//Creates Beacon Base
		//Creates the Button Health bar using the Boss Health Effect
		
		//Updates the Health Bar
		getCommand("buttonHealth").setExecutor(new ButtonHealth(this));
	}
	public void registerEvents(){
		//Checks Time every second
		 getServer().getPluginManager().registerEvents(new MidnightListener(this), this);
		 getServer().getPluginManager().registerEvents(new DisplayUpdateListener(this), this);
	}
}
