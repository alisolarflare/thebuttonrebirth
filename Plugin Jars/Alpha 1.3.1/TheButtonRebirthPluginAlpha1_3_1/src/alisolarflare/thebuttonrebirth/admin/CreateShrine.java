package alisolarflare.thebuttonrebirth.admin;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import net.md_5.bungee.api.ChatColor;

public class CreateShrine implements CommandExecutor{

	private ButtonRebirthPlugin plugin;
	private World world;
	
	private int chestX;
	private int chestY;
	private int chestZ;
	private int shrineRadius;
	
	public CreateShrine(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		chestX = plugin.getConfig().getInt("chestX"); 
		chestY = plugin.getConfig().getInt("chestY");
		chestZ = plugin.getConfig().getInt("chestZ");
		shrineRadius = plugin.getConfig().getInt("shrineRadius");
	}

	private void createCube(int x, int y, int z, int radius, Material material){
		//Creates a Cube under the Pyramid under the Chest

		//---Parameters---
		//Accepts x,y,z coordinates of the top center face, radius from center to edge, and BlockType
		//Blocks placed by Radius does not include center block
		//radius = 0 creates a single block
		//radius = 1 creates a 9x9 cube
		//Places down the cube

		//---Variables---
		//layer variable: layer being worked on, from the top
		//layer[0] = top layer of cube
		//layer[height] = bottom layer of cube
		//row variable: relative value from center
		//column variable:relative value from center
		for (int layer = 0; layer <= radius; layer++){
			for (int row = -radius; row <= radius; row++){
				for (int column = -radius; column <= radius; column++){
					world.getBlockAt(x+row,y-layer,z+column).setType(material);
				}
			}
		}
	}
	private void createPyramid(int x, int y, int z, int height, Material material){
		//creates a pyramid under the chest

		//sets the Top Block
		//Places down the rest of the layers
		//layer variable: layer being worked on, from the top
		//layer[0] = top block of pyramid
		//row variable: relative value from center
		//column variable:relative value from center
		
		//SET - top block, rest of the blocks
		world.getBlockAt(x,y,z).setType(material);
		for (int layer = 1; layer < height; layer++){
			int radius = layer;
			for (int row = -radius; row <= radius; row++){
				for (int column = -radius; column <= radius; column++){
					world.getBlockAt(x+row,y-layer,z+column).setType(material);
				}
			}
		}
	}

	public void createShrine(int x, int y, int z, int radius, Material material){
		//Creates Chest
		world.getBlockAt(x, y, z).setType(Material.CHEST);
		createPyramid(x, y-1, z, radius, material);
		createCube(x, y-radius-1, z, radius, material);


	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player){
			sender.sendMessage("You can't be a player to use this command");
			return false;
		}
		
		try{
			world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		}catch(Exception e){
			//Sends Error message to the Player
			sender.sendMessage("Error in CreateShrine Class: No world defined in config...");
			sender.sendMessage("Defining world based on player location.");
			
			/*
			//Changes world variable in config file
			if (sender instanceof Player){
				Player player = (Player) sender;
				plugin.getConfig().set("world", player.getWorld().getName());
				plugin.saveConfig();
				world = player.getWorld();

				//Sends player the result of the change
				sender.sendMessage("World variable set to " + player.getWorld().getName() + "in config file");
			}
			*/
		}
		/*
		if (!(player.hasPermission("Moderator") || player.hasPermission("Admin"))){
			player.sendMessage(ChatColor.RED + "You must be in the group Moderator or Admin to access this command!");
			return false;
		}
		 */
		chestX = plugin.getConfig().getInt("chestX");
		chestY = plugin.getConfig().getInt("chestY");
		chestZ = plugin.getConfig().getInt("chestZ");
		shrineRadius = plugin.getConfig().getInt("shrineRadius");

		createShrine(chestX,chestY,chestZ,shrineRadius, Material.BEDROCK);
		sender.sendMessage(ChatColor.AQUA + "Everything worked!");

		return false;
	}
}
