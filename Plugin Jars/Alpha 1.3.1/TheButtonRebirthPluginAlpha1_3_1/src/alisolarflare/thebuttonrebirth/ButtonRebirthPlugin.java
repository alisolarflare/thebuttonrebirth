package alisolarflare.thebuttonrebirth;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import alisolarflare.thebuttonrebirth.admin.CreateShrine;
import alisolarflare.thebuttonrebirth.commands.ButtonHealth;
import alisolarflare.thebuttonrebirth.commands.bossbar.CompactBars;
import alisolarflare.thebuttonrebirth.commands.bossbar.HideBars;
import alisolarflare.thebuttonrebirth.commands.bossbar.HideDiamondBar;
import alisolarflare.thebuttonrebirth.commands.bossbar.HideHealthBar;
import alisolarflare.thebuttonrebirth.commands.bossbar.HideTimeBar;
import alisolarflare.thebuttonrebirth.commands.bossbar.ShowBars;
import alisolarflare.thebuttonrebirth.commands.bossbar.ShowDiamondBar;
import alisolarflare.thebuttonrebirth.commands.bossbar.ShowHealthBar;
import alisolarflare.thebuttonrebirth.commands.bossbar.ShowTimeBar;
import alisolarflare.thebuttonrebirth.listeners.DisplayUpdateListener;
import alisolarflare.thebuttonrebirth.listeners.MidnightListener;
import alisolarflare.thebuttonrebirth.listeners.StealChestListener;

public class ButtonRebirthPlugin extends JavaPlugin{
	public PluginDescriptionFile pdfFile;
	public Logger logger;
	
	public MidnightListener midnightListener;
	public DisplayUpdateListener displayUpdateListener;
	private StealChestListener stealChestListener;
	
	public List<Player> healthBlocked = new ArrayList<Player>();
	public List<Player> diamondBlocked = new ArrayList<Player>();
	public List<Player> timeBlocked = new ArrayList<Player>();
	public List<Player> compactRequest = new ArrayList<Player>();
	
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
	public void registerCommands(){
		getCommand("buttonHealth").setExecutor(new ButtonHealth(this));
		getCommand("createShrine").setExecutor(new CreateShrine(this));
		getCommand("compactBars").setExecutor(new CompactBars(this));
		getCommand("hideBars").setExecutor(new HideBars(this));
		getCommand("hideDiamondBar").setExecutor(new HideDiamondBar(this));
		getCommand("hideHealthBar").setExecutor(new HideHealthBar(this));
		getCommand("hideTimeBar").setExecutor(new HideTimeBar(this));
		getCommand("showBars").setExecutor(new ShowBars(this));
		getCommand("showDiamondBar").setExecutor(new ShowDiamondBar(this));
		getCommand("showHealthBar").setExecutor(new ShowHealthBar(this));
		getCommand("showTimeBar").setExecutor(new ShowTimeBar(this));
		
	}
	
	public void registerEvents(){
		midnightListener = new MidnightListener(this);
		displayUpdateListener = new DisplayUpdateListener(this);
		stealChestListener = new StealChestListener(this);
		
		getServer().getPluginManager().registerEvents(midnightListener, this);
		getServer().getPluginManager().registerEvents(displayUpdateListener,this);
		getServer().getPluginManager().registerEvents(stealChestListener, this);
		
	}
	public void createShrine(){
		int chestX = this.getConfig().getInt("chestX");
		int chestY = this.getConfig().getInt("chestY");
		int chestZ = this.getConfig().getInt("chestZ");
		
		CreateShrine shrineCreator = new CreateShrine(this);
		shrineCreator.createShrine(chestX, chestY, chestZ, 10, Material.BEDROCK);
	}
}
