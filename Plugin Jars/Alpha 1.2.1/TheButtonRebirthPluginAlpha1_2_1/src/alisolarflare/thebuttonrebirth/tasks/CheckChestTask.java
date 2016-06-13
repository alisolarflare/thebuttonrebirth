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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import alisolarflare.thebuttonrebirth.admin.CreateShrine;

public class CheckChestTask extends BukkitRunnable{
	private final JavaPlugin plugin;
	private final ButtonRebirthPlugin BRplugin;
	private World world;

	private int chestX;
	private int chestY;
	private int chestZ;
	
	public CheckChestTask(JavaPlugin plugin, ButtonRebirthPlugin initBRplugin){
		this.plugin = plugin;
		this.BRplugin = initBRplugin;
		
		chestX = plugin.getConfig().getInt("chestX");
		chestY = plugin.getConfig().getInt("chestY");
		chestZ = plugin.getConfig().getInt("chestZ");
	}
	
	@Override
	public void run(){
		//run() activates every 20 server ticks.
		//Auto Disabling code
		if (plugin.isEnabled() == false){
			this.cancel();
		}
		
		//Gets the time - Also, FUCK TIME. FUCK IT TO HELL. LOOK AT THAT SPAGETTHICODE SHIT. FUCK TIIIIIIIIIIIIIIIIME.
		Clock UTCclock = Clock.systemUTC();
		LocalDateTime UTCtime = LocalDateTime.now(UTCclock);
		plugin.getConfig().getInt("buttonHealth");
		LocalDateTime checkedTime = LocalDateTime.parse(plugin.getConfig().getString("lastCheckChestTime"));
		
		//Checks the time
		if (UTCtime.isAfter(checkedTime)){
			
			//Checked Time
			checkedTime = checkedTime.plusHours((long) 24.0);
			plugin.getConfig().set("lastCheckChestTime", checkedTime.toString());
			plugin.saveConfig();
			
			//Checks if the world variable is defined in the config file.
			if (plugin.getConfig().getString("world") == null){
				plugin.getServer().broadcastMessage("BUTTONSHINE ERROR: No world defined in config file.");
				plugin.getServer().broadcastMessage("run /setWorld to define world.");
			}
			
			//Checks if the world exists
			try{
				world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
			}catch (Exception e){
				plugin.getServer().broadcastMessage("BUTTONSHRINE ERROR: World in config file not a world in the server.");
				return;
			}
			
			//Gets the Chest Block from the world
			Block chestBlock = world.getBlockAt(chestX, chestY, chestZ);
			
			//Checks to see if the block is a chest
			if (!(chestBlock.getType() == Material.CHEST)){
				damageShrine();
				
				Block newChest = world.getBlockAt(chestX,chestY,chestZ);
				newChest.setType(Material.CHEST);
				CreateShrine shrineConstructor= new CreateShrine(BRplugin);
				shrineConstructor.createShrine(chestX, chestY, chestZ, 10, Material.BEDROCK);
				
				return;
			}
			
			//Casts the block into a chest
			Chest shrineChest = (Chest) chestBlock.getState();
			Inventory shrineInventory = shrineChest.getInventory();
			int minimumDiamondBlocks = plugin.getConfig().getInt("minimumDiamondBlocks");
			if(shrineInventory.contains(Material.DIAMOND_BLOCK, minimumDiamondBlocks)){
				if (minimumDiamondBlocks == 0){
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "------------------------------------");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "----- THE EXPERIMENT HAS BEGUN -----");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "------------------------------------");
					plugin.getServer().broadcastMessage(ChatColor.BLUE + "    " + minimumDiamondBlocks + " Blocks required");
					world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_WITHER_SPAWN,50,10);
				}else{
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "----- BUTTON REFULED -----");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
					plugin.getServer().broadcastMessage(ChatColor.BLUE + "    " + minimumDiamondBlocks + " Blocks required");
					world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_WITHER_SPAWN,50,10);
				}
				minimumDiamondBlocks++;
				plugin.getConfig().set("minimumDiamondBlocks", minimumDiamondBlocks);
				plugin.saveConfig();
			}else{
				damageShrine();
			}
			CreateShrine shrineConstructor = new CreateShrine(BRplugin);
			shrineConstructor.createShrine(chestX, chestY, chestZ, 10, Material.BEDROCK);
			shrineInventory.clear();
		}else{
			/*
			plugin.getServer().broadcastMessage(UTCtime.toString() + "- Ali Debug");
			plugin.getServer().broadcastMessage(checkedTime.toString() + "- Ali Debug");
			plugin.getServer().broadcastMessage("TIS A DUUUUUUUUUUUD - Ali Debug");
			*/
		}
		
	}
	private void damageShrine(){
		int buttonHealth = plugin.getConfig().getInt("buttonHealth");
		buttonHealth--;
		plugin.getConfig().set("buttonHealth", buttonHealth);
		
		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "--------------------------");
		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "----- BUTTON DAMAGED -----");
		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "--------------------------");
		plugin.getServer().broadcastMessage(ChatColor.RED + "    " + buttonHealth + "s of Health left");
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_ENDERDRAGON_DEATH,50,1);
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_LIGHTNING_THUNDER,50,1);
		world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_GENERIC_EXPLODE,50,50);
	}
}
