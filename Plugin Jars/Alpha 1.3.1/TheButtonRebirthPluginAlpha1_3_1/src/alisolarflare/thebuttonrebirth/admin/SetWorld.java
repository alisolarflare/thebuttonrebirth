package alisolarflare.thebuttonrebirth.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;

public class SetWorld implements CommandExecutor {
	private ButtonRebirthPlugin plugin;
	public SetWorld(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a Player in a world to use this command!");
		}
		
		Player player = (Player) sender;
		plugin.getConfig().set("world", player.getWorld().getName());
		plugin.saveConfig();
		
		sender.sendMessage("Set World to: " + player.getWorld().getName());
		return false;
	}
	
}
