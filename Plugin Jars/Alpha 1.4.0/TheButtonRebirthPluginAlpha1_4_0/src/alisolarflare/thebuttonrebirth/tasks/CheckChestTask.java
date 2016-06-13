package alisolarflare.thebuttonrebirth.tasks;

import java.time.Clock;
import java.time.LocalDateTime;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.admin.CreateShrine;

public class CheckChestTask extends BukkitRunnable{
	//Plugin
	private final ButtonRebirthPlugin BRplugin;
	private World world;
	
	//Chest
	private int chestX;
	private int chestY;
	private int chestZ;
	private Block chestBlock;
	private Chest shrineChest;
	private Inventory shrineInventory;
	
	//Time
	private LocalDateTime currentTime;
	private LocalDateTime configTime;
	
	private int minimumDiamondBlocks;
	private int diamondsInserted;
	
	public CheckChestTask(ButtonRebirthPlugin initBRplugin){
		//INIT - plugin
		this.BRplugin = initBRplugin;
		
		//INIT - chestX, chestY, chestZ
		chestX = BRplugin.getConfig().getInt("chestX");
		chestY = BRplugin.getConfig().getInt("chestY");
		chestZ = BRplugin.getConfig().getInt("chestZ");
		
		//INIT - World
		world = BRplugin.getServer().getWorld(BRplugin.getConfig().getString("world"));
	}
	
	@Override
	public void run(){
		//run() activates every 20 server ticks.
		
		//CHECK - Plugin is Enabled
		if (BRplugin.isEnabled() == false){
			this.cancel();
		}
		//CHECK - World Exists
		if (!(BRplugin.getServer().getWorlds().contains(BRplugin.getServer().getWorld(BRplugin.getConfig().getString("world"))))) {
			BRplugin.logger.info("Error: Config world does not exist in Server.");
			BRplugin.logger.info("Server Worlds: " + BRplugin.getServer().getWorlds().toString());
			BRplugin.logger.info("Config World:  " + BRplugin.getConfig().getString("world"));
			BRplugin.logger.info("Turning off Display...");
			this.cancel();
			return;
		}
		
		//INIT - currentTime, configTime
		currentTime = LocalDateTime.now(Clock.systemUTC());
		configTime = LocalDateTime.parse(BRplugin.getConfig().getString("lastCheckChestTime"));
		
		//TIME - Current Time after Config Time
		if (currentTime.isAfter(configTime)){ 
			
			//SANITIZE "world"
			if (BRplugin.getConfig().getString("world") == null) BRplugin.getServer().broadcastMessage("Error: No world defined in config file.");
			if (BRplugin.getServer().getWorlds() == null) BRplugin.getServer().broadcastMessage("Error: plugin.getServer().getWorlds() returns null");
			
			//INIT - world, chestBlock
			chestBlock = world.getBlockAt(chestX, chestY, chestZ);
			
			//SANITIZE - chestBlock
			if (!(chestBlock.getType() == Material.CHEST)){
				damageShrine();
				reconstructShrine();
				return;
			}
			
			//INIT - shrineChest, shrineInventory
			shrineChest = (Chest) chestBlock.getState();
			shrineInventory = shrineChest.getInventory();
			
			
			//UPDATE - configTime
			configTime = configTime.plusDays((long) 1.0);
			BRplugin.getConfig().set("lastCheckChestTime", configTime.toString());
			BRplugin.saveConfig();
			
			//INIT - minimumDiamondBlocks, diamondsInserted
			minimumDiamondBlocks = BRplugin.getConfig().getInt("minimumDiamondBlocks");
			diamondsInserted = BRplugin.getConfig().getInt("diamondsInserted");
			
			//CHECK - chest for diamonds
			if(diamondsInserted > minimumDiamondBlocks || shrineInventory.contains(Material.DIAMOND_BLOCK, (minimumDiamondBlocks - diamondsInserted))){
				//INVENTORY SUCCESS
				
				//CHECK - First Time
				if (minimumDiamondBlocks == 0){
					broadcastExperimentHasBegun();
				}else{
					broadcastButtonRefuled();
				}
				
				//UPDATE minimumDiamondBlocks
				minimumDiamondBlocks++;
				BRplugin.getConfig().set("minimumDiamondBlocks", minimumDiamondBlocks);
				BRplugin.getConfig().set("diamondsInserted", 0);
				BRplugin.saveConfig();
			}else{
				//INVENTORY FAILURE
				damageShrine();
			}
			
			//RESET - shrine, shrineInventory
			reconstructShrine();
			shrineInventory.clear();
			BRplugin.getConfig().set("diamondsInserted",0);
			
		}else{
			//currentTime is before config time.
			//therefore wait.
		}
		
	}
	private void damageShrine(){
		//UPDATE - buttonHealth
		int buttonHealth = BRplugin.getConfig().getInt("buttonHealth");
		buttonHealth--;
		BRplugin.getConfig().set("buttonHealth", buttonHealth);
		
		//DISPLAY AND MAKE SOUND
		BRplugin.getServer().broadcastMessage(ChatColor.DARK_RED + "--------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.DARK_RED + "----- BUTTON DAMAGED -----");
		BRplugin.getServer().broadcastMessage(ChatColor.DARK_RED + "--------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.RED + "    " + buttonHealth + "s of Health left");
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_ENDERDRAGON_DEATH,50,1);
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_LIGHTNING_THUNDER,50,1);
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_GENERIC_EXPLODE,50,50);
	}
	private void reconstructShrine(){
		CreateShrine shrineConstructor= new CreateShrine(BRplugin);
		shrineConstructor.createShrine(chestX, chestY, chestZ, 10, Material.BEDROCK);
	}
	private void broadcastExperimentHasBegun(){
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "------------------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "----- THE EXPERIMENT HAS BEGUN -----");
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "------------------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.BLUE + "    " + minimumDiamondBlocks + " Blocks required");
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_WITHER_SPAWN,50,10);
	}
	private void broadcastButtonRefuled(){
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "----- BUTTON REFULED -----");
		BRplugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
		BRplugin.getServer().broadcastMessage(ChatColor.BLUE + "    " + minimumDiamondBlocks + " Blocks required");
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_WITHER_SPAWN,50,10);
	}
}
