package alisolarflare.thebuttonrebirth.commands.bossbar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;
import net.md_5.bungee.api.ChatColor;

public class CompactBars implements CommandExecutor {
	private final ButtonRebirthPlugin plugin;
	
	public CompactBars(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("You need to be a Player to use this command");
			return false;
		}
		
		Player player = (Player) sender;

		plugin.diamondBlocked.add(player);
		plugin.healthBlocked.add(player);
		plugin.timeBlocked.add(player);
		plugin.compactRequest.add(player);
		sender.sendMessage(ChatColor.AQUA + "All Bars Removed");

		return false;
	}
}
