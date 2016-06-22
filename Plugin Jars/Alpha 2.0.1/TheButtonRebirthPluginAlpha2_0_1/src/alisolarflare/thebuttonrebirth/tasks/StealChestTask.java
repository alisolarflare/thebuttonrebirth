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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;

public class StealChestTask extends BukkitRunnable{
	private ButtonRebirthPlugin plugin;
	private World world;
	private int diamondsInserted;
	private int chestX;
	private int chestY;
	private int chestZ;
	private Block chestBlock;
	private Inventory shrineInventory;
	
	private LocalDateTime currentTime;
	private LocalDateTime configTime;
	private LocalDateTime messageAllowedTime;
	private int noRefuelMessagePeriod = 1;//minutes
	private Player presserPlayer;
	private String presserName;
	
	public StealChestTask(ButtonRebirthPlugin initPlugin){
		//INIT - plugin,server,world
		plugin = initPlugin;
		diamondsInserted = plugin.getConfig().getInt("diamondsInserted");

		//INIT - chestX, chestY, chestZ
		chestX = plugin.getConfig().getInt("chestX");
		chestY = plugin.getConfig().getInt("chestY");
		chestZ = plugin.getConfig().getInt("chestZ");

		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
	}

	@Override
	public void run() {
		//CHECK - Plugin is Enabled
		if (plugin.isEnabled() == false){
			this.cancel();
		}
		
		//CHECK - World Exists
		if (!(plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(plugin.getConfig().getString("world"))))) {
			plugin.logger.info("Error: Config world does not exist in Server.");
			plugin.logger.info("Server Worlds: " + plugin.getServer().getWorlds().toString());
			plugin.logger.info("Config World:  " + plugin.getConfig().getString("world"));
			plugin.logger.info("Turning off StealChest Task...");
			this.cancel();
			return;
		}

		//INIT - diamondsInserted
		diamondsInserted = plugin.getConfig().getInt("diamondsInserted");

		//SANITIZE - world
		if (plugin.getConfig().getString("world") == null) {
			plugin.logger.info("Error: No world defined in config file.");
			return;
		}
		if (plugin.getServer().getWorlds() == null) {
			plugin.logger.info("plugin.getServer().getWorlds() returns null");
			return;
		}
		
		//INIT - chestBlock
		chestBlock = world.getBlockAt(chestX, chestY, chestZ);
		
		//SANITIZE - chestBlock
		if (!(chestBlock.getType() == Material.CHEST)){
			return;
		}
		
		//INIT - shrineInventory, minimumDiamondBlocks
		shrineInventory = ((Chest) chestBlock.getState()).getInventory();
		
		//STEAL - from shrine chest, put into button health
		int totalDiamonds = 0;
		ItemStack[] chestContents = shrineInventory.getContents();
		//CALCULATE - totalDiamonds
		for (ItemStack stack: chestContents){
			try{
				if (stack.getType() == Material.DIAMOND_BLOCK){
					totalDiamonds += (stack.getAmount() *9);
					
				}else if (stack.getType() == Material.DIAMOND){
					totalDiamonds += (stack.getAmount() *1);
					
				}else if (stack.getType() == Material.DIAMOND_ORE){
					totalDiamonds += (stack.getAmount() *1);
					
				}else if (stack.getType() == Material.DIAMOND_SWORD){
					totalDiamonds += (stack.getAmount() *2);
					
				}else if(stack.getType() == Material.DIAMOND_PICKAXE){
					totalDiamonds += (stack.getAmount() *3);
					
				}else if (stack.getType() == Material.DIAMOND_HELMET){
					totalDiamonds += (stack.getAmount() *5);
					
				}else if (stack.getType() == Material.DIAMOND_CHESTPLATE){
					totalDiamonds += (stack.getAmount() *8);
					
				}else if (stack.getType() == Material.DIAMOND_LEGGINGS){
					totalDiamonds += (stack.getAmount() *7);
					
				}else if(stack.getType() == Material.DIAMOND_BOOTS){
					totalDiamonds += (stack.getAmount() *4);
					
				}else if(stack.getType() == Material.DIAMOND_AXE){
					totalDiamonds += (stack.getAmount() *3);
					
				}else if(stack.getType() == Material.DIAMOND_HOE){
					totalDiamonds += (stack.getAmount() *2);
				
				}else if(stack.getType() == Material.DIAMOND_SPADE){
					totalDiamonds += (stack.getAmount() *1);
				
				}else if(stack.getType() == Material.DIAMOND_BARDING){
					totalDiamonds += (stack.getAmount() *1);
					
				}
			}catch(Exception e){/*stack is empty*/}
		}
		shrineInventory.clear();
		diamondsInserted += totalDiamonds;
		plugin.getConfig().set("diamondsInserted", diamondsInserted);
		plugin.saveConfig();
		//RESET TIMER CHECK
		if (plugin.getConfig().getInt("diamondsInserted") >= plugin.getConfig().getInt("minimumDiamondBlocks")){
			
			plugin.getConfig().set("diamondsInserted", 0);
			plugin.saveConfig();
			
			currentTime = LocalDateTime.now(Clock.systemUTC());
			configTime = LocalDateTime.parse(plugin.getConfig().getString("lastCheckChestTime"));
			
			plugin.getConfig().set("lastCheckChestTime", currentTime.plusMinutes(plugin.getConfig().getInt("barDuration")).toString());
			plugin.saveConfig();

			messageAllowedTime = configTime.minusMinutes(plugin.getConfig().getInt("barDuration")).plusMinutes(noRefuelMessagePeriod);
			
			if(currentTime.isAfter(messageAllowedTime)){
				
				try {presserPlayer = getNearestPresser();} catch(Exception noPlayers) {return;}
				
				presserName = presserPlayer.getDisplayName();
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "SHRINE REFULED BY " + presserName);
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "--------------------------");
				for (Player player: plugin.getServer().getOnlinePlayers()){
					Location tempLocation = player.getLocation();
					world.playSound(tempLocation, Sound.ENTITY_WITHER_SPAWN,30,10);
				}
				
			}else{

				try {presserPlayer = getNearestPresser();} catch(Exception noPlayers) {return;}
				
				presserPlayer.sendMessage(ChatColor.AQUA + "---------------");
				presserPlayer.sendMessage(ChatColor.AQUA + "SHRINE REFUELED");
				presserPlayer.sendMessage(ChatColor.AQUA + "---------------");
			}
			world.playSound(new Location(world,chestX,chestY,chestZ), Sound.ENTITY_WITHER_SPAWN,50,10);
			((CyclicalDisplayTask) plugin.cyclicalDisplayListener.cyclicalDisplayTask).setBar(2);
		}
	}

	private Player getNearestPresser() throws Exception{
		if (plugin.getServer().getOnlinePlayers().isEmpty()) throw new Exception();
		Player closestPlayer = null;
		int closestDistance = 0;
		for (Player player: plugin.getServer().getOnlinePlayers()){
			int distance = (int) player.getLocation().distance(new Location(world, chestX,chestY,chestZ));
			if (closestPlayer == null){
				closestPlayer = player;
				closestDistance = distance;
			}else if(distance < closestDistance){
				closestPlayer = player;
				closestDistance = distance;
			}
		}
		return closestPlayer;
	}
}
