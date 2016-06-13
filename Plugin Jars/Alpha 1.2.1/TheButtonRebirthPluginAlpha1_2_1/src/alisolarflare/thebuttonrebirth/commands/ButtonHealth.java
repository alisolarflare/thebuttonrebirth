package alisolarflare.thebuttonrebirth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;

public class ButtonHealth implements CommandExecutor{
	private final ButtonRebirthPlugin plugin;
	public ButtonHealth(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin; 
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("Button Health: " + plugin.getConfig().getString("buttonHealth"));
		// TODO Auto-generated method stub
		return false;
	}
}
