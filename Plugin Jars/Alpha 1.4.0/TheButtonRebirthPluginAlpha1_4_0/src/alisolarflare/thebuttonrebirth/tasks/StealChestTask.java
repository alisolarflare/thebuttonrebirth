package alisolarflare.thebuttonrebirth.tasks;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
		if (plugin.getConfig().getString("world") == null) plugin.getServer().broadcastMessage("Error: No world defined in config file.");
		if (plugin.getServer().getWorlds() == null) plugin.getServer().broadcastMessage("Error: plugin.getServer().getWorlds() returns null");
		
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
				}
				if (stack.getType() == Material.DIAMOND){
					totalDiamonds += (stack.getAmount() *1);
				}
				if (stack.getType() == Material.DIAMOND_SWORD){
					totalDiamonds += 2;
				}
				if (stack.getType() == Material.DIAMOND_HELMET){
					totalDiamonds += 5;
				}
				if (stack.getType() == Material.DIAMOND_CHESTPLATE){
					totalDiamonds += 8;
				}
				if (stack.getType() == Material.DIAMOND_LEGGINGS){
					totalDiamonds += 7;
				}
				if(stack.getType() == Material.DIAMOND_BOOTS){
					totalDiamonds += 4;
				}
				
			}catch(Exception e){/*stack isn't a diamond block*/}
		}
		shrineInventory.clear();
		diamondsInserted += totalDiamonds;
		plugin.getConfig().set("diamondsInserted", diamondsInserted);
		plugin.saveConfig();
	}
}
