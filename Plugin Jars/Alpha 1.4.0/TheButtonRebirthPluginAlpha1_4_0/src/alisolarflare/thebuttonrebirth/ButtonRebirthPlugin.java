package alisolarflare.thebuttonrebirth;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import alisolarflare.thebuttonrebirth.admin.CreateShrine;
import alisolarflare.thebuttonrebirth.listeners.CyclicalDisplayListener;
import alisolarflare.thebuttonrebirth.listeners.MidnightListener;
import alisolarflare.thebuttonrebirth.listeners.StealChestListener;

public class ButtonRebirthPlugin extends JavaPlugin{
	public PluginDescriptionFile pdfFile;
	public Logger logger;	
	private CreateShrine shrineCreator;
	
	private MidnightListener midnightListener;
	private CyclicalDisplayListener cyclicalDisplayListener;
	private StealChestListener stealChestListener;
	
	public List<Player> compactRequest = new ArrayList<Player>();
	public List<Player> barsHidden = new ArrayList<Player>();
	
	public void onEnable(){
		//Logs "Plugin Enabled:
		pdfFile = getDescription();
		logger = getLogger();
		
		
		logger.info(pdfFile.getName() + " has been started (V." + pdfFile.getVersion()+ ").");
		
		registerCommands();
		registerEvents();
		
		logger.info(pdfFile.getName() + " has been Enabled (V." + pdfFile.getVersion()+ ").");
	}
	
	public void onDisable(){
		logger.info(pdfFile.getName() + " has been Disabled (V." + pdfFile.getVersion()+ ").");
	}
	private void registerCommands(){
		//Button
		getCommand("createShrine").setExecutor(new CreateShrine(this));
		
	}
	
	private void registerEvents(){
		//INIT - Listeners
		midnightListener = new MidnightListener(this);
		cyclicalDisplayListener = new CyclicalDisplayListener(this);
		stealChestListener = new StealChestListener(this);
		
		//REGISTER - Listeners
		getServer().getPluginManager().registerEvents(midnightListener, this);
		getServer().getPluginManager().registerEvents(cyclicalDisplayListener,this);
		getServer().getPluginManager().registerEvents(stealChestListener, this);
		
	}
	public void createShrine(){
		int chestX = this.getConfig().getInt("chestX");
		int chestY = this.getConfig().getInt("chestY");
		int chestZ = this.getConfig().getInt("chestZ");
		
		shrineCreator = new CreateShrine(this);
		shrineCreator.createShrine(chestX, chestY, chestZ, 10, Material.BEDROCK);
	}
}
