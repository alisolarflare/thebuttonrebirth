package alisolarflare.thebuttonrebirth.admin;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import net.md_5.bungee.api.ChatColor;

public class CreateShrine implements CommandExecutor{
	private Player player;
	private World world;
	private ButtonRebirthPlugin plugin;
	public CreateShrine(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
	}
	
	private void createChest(int x, int y, int z, String name, boolean isTrapped){
		Block block = world.getBlockAt(x,y,z);
		block.setType(Material.CHEST);
	}
	private void createCube(int x, int y, int z, int radius, Material material){
		//Creates a Cube under the Pyramid under the Chest
		
		//---Parameters---
		//Accepts x,y,z coordinates of the top center face, radius from center to edge, and BlockType
		//Blocks placed by Radius does not include center block
		//radius = 0 creates a single block
		//radius = 1 creates a 9x9 cube
		
		for (int layer = 0; layer <= radius; layer++){
			//Places down the cube
			
			//---Variables---
			//layer variable: layer being worked on, from the top
			//layer[0] = top layer of cube
			//layer[height] = bottom layer of cube
			//row variable: relative value from center
			//column variable:relative value from center
			for (int row = -radius; row <= radius; row++){
				for (int column = -radius; column <= radius; column++){
					setBlock(x+row,y-layer,z+column,material);
				}
			}
		}
	}
	private void createPyramid(int x, int y, int z, int height, Material material){
		//creates a pyramid under the chest
		
		//sets the Top Block
		setBlock(x,y,z,material);
		//Places down the rest of the layers
		//layer variable: layer being worked on, from the top
		//layer[0] = top block of pyramid
		//row variable: relative value from center
		//column variable:relative value from center
		for (int layer = 1; layer < height; layer++){
			int radius = layer;
			for (int row = -radius; row <= radius; row++){
				for (int column = -radius; column <= radius; column++){
					setBlock(x+row,y-layer,z+column,material);
				}
			}
		}
	}

	public void createShrine(int x, int y, int z, int radius, Material material){
		
		//Creates Chest
		createChest(x,y,z,"FUCK",false);
		
		//Creates Shrine
		createPyramid(x, y-1, z, radius, material);
		createCube(x, y-radius-1, z, radius, material);
		
		
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command");
			return false;
		}
		
		player = (Player) sender;
		
		try{
			world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		}catch(Exception e){
			//Sends Error message to the Player
			player.sendMessage("Error in CreateShrine Class: No world defined in config...");
			player.sendMessage("Defining world based on player location.");
			
			//Changes world variable in config file
			plugin.getConfig().set("world", player.getWorld().getName());
			plugin.saveConfig();
			world = player.getWorld();
			
			//Sends player the result of the change
			player.sendMessage("World variable set to " + player.getWorld().getName() + "in config file");
		}
		/*
		if (!(player.hasPermission("Moderator") || player.hasPermission("Admin"))){
			player.sendMessage(ChatColor.RED + "You must be in the group Moderator or Admin to access this command!");
			return false;
		}
		*/
		int chestX = plugin.getConfig().getInt("chestX");
		int chestY = plugin.getConfig().getInt("chestY");
		int chestZ = plugin.getConfig().getInt("chestZ");
		
		int shrineRadius = plugin.getConfig().getInt("shrineRadius");
		
		createShrine(chestX,chestY,chestZ,shrineRadius, Material.BEDROCK);
		
		player.sendMessage(ChatColor.AQUA + "Everything worked!");
		return false;
	}
	private void setBlock(int x, int y, int z, Material material){
		try{
			Block block = world.getBlockAt(x,y,z);
			try{
				block.setType(material);
			}catch (Exception e){
				player.sendMessage(ChatColor.RED + "SOMETHING WENT WRONG WHEN TRYING TO SET TYPE");
			}
		}catch (Exception e){
			player.sendMessage(ChatColor.RED + "SOMETHING WENT WRONG WHEN GETTING WORLD BLOCK");
		}
	}
}
