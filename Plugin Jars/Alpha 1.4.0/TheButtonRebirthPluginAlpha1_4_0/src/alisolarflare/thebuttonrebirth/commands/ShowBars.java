package alisolarflare.thebuttonrebirth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;

public class ShowBars implements CommandExecutor{
	private ButtonRebirthPlugin plugin;
	
	public ShowBars(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command");
			return false;
		}
						
		Player player = (Player) sender;
		plugin.barsHidden.remove(player);
		return false;
	}
	
}
