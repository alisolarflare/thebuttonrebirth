package alisolarflare.thebuttonrebirth.tasks;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import alisolarflare.thebuttonrebirth.ButtonRebirthPlugin;

public class DisplayUpdateTask extends BukkitRunnable{
	public static BossBar buttonBar;
	public static BossBar diamondBar;
	public static BossBar timeBar;
	
	public int buttonHealth;
	public int minimumDiamondBlocks;
	public String lastCheckChestTime;
	
	public ButtonRebirthPlugin plugin;
	public World world;
	
	public DisplayUpdateTask(ButtonRebirthPlugin initPlugin){
		plugin = initPlugin;
		Server server = plugin.getServer();
		
		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		buttonHealth = plugin.getConfig().getInt("buttonHealth");
		minimumDiamondBlocks = plugin.getConfig().getInt("minimumDiamondBlocks");
		
		buttonBar = server.createBossBar("Button Health: "+ buttonHealth, BarColor.PURPLE, BarStyle.SEGMENTED_20);
		diamondBar = server.createBossBar("Diamond Blocks Required at (0,0): "+minimumDiamondBlocks, BarColor.BLUE, BarStyle.SOLID);
		timeBar = server.createBossBar("Time Until Diamond Refuel", BarColor.RED, BarStyle.SOLID);
	}
	
	@Override
	public void run() {
		if (plugin.isEnabled() == false){
			this.cancel();
		}
		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		buttonHealth = plugin.getConfig().getInt("buttonHealth");
		minimumDiamondBlocks = plugin.getConfig().getInt("minimumDiamondBlocks");
		lastCheckChestTime = plugin.getConfig().getString("lastCheckChestTime");
		
		//gets the time. Also. FUCK TIME TO FUCKING HELL. LOOK AT THAT WARPED-CODEASS BULLSHIT. FUCK TIIIIIIIIIIIIIIIIIIIME
		Clock UTCclock = Clock.systemUTC();
		LocalDateTime UTCtime = LocalDateTime.now(UTCclock);
		plugin.getConfig().getInt("buttonHealth");
		LocalDateTime lastCheckChestTime = LocalDateTime.parse(plugin.getConfig().getString("lastCheckChestTime"));
		long NanoDifference = UTCtime.until(lastCheckChestTime, ChronoUnit.NANOS);
		Duration timeDifference = Duration.ZERO;
		timeDifference = timeDifference.plusNanos(NanoDifference);
		
		buttonBar.setTitle("Button Health: "+ buttonHealth + "s");
		diamondBar.setTitle("Diamond Blocks Required at (0,0) " + minimumDiamondBlocks);
		timeBar.setTitle("Time Until Diamond Refuel: " + timeDifference.toHours()+":"+ timeDifference.toMinutes()%60 +":"+ Math.floor(timeDifference.toMillis()%60000/1000));
				
		//Button Health Progress Setting
		if (buttonHealth < 0){
			buttonBar.setProgress(0.0);
		}else if (buttonHealth > 60){
			buttonBar.setProgress(1.0);
		}else{
			buttonBar.setProgress((double)buttonHealth/60.0);
		}
		
		//Diamond Blocks Progress Setting
		if (minimumDiamondBlocks < 1){
			diamondBar.setProgress(1.0);
		}else{
			getChestInventory();
			//Gets chest from world
			int chestX = plugin.getConfig().getInt("chestX");
			int chestY = plugin.getConfig().getInt("chestY");
			int chestZ = plugin.getConfig().getInt("chestZ");
			Block chestBlock = world.getBlockAt(chestX, chestY, chestZ);
			
			//Checks to see if chest is chest
			if (!(chestBlock.getType() == Material.CHEST)) return;
			
			//Checks the amount of diamonds in the chest
			Chest shrineChest = (Chest) chestBlock.getState();
			Inventory shrineInventory = shrineChest.getInventory();
			
			if(shrineInventory.contains(Material.DIAMOND_BLOCK, minimumDiamondBlocks)){
				diamondBar.setProgress(1.0);
			}else{
				ItemStack[] chestContents = shrineInventory.getContents();
				double totalDiamonds = 0.0;
				for (ItemStack stack: chestContents){
					try{
						if (stack.getType() == Material.DIAMOND_BLOCK){
							totalDiamonds += stack.getAmount();
						}	
					}catch(Exception e){
						
					}
				}
				diamondBar.setProgress(totalDiamonds/(double)minimumDiamondBlocks);
			}
		}
		
		//Time Progress Setting
		Duration dayDuration = Duration.of(1,ChronoUnit.DAYS);
		long timeDifferenceMillis = timeDifference.toMillis();
		long dayDurationMillis = dayDuration.toMillis();
		double durationRatio = (double)timeDifferenceMillis /(double)dayDurationMillis;
		
		if (durationRatio > 1){
			timeBar.setProgress(1.0);
		}else if(durationRatio < 0){
			timeBar.setProgress(0.0);
		}else{
			timeBar.setProgress(durationRatio);
		}
		
		List<Player> playerList = world.getPlayers();
		List<Player> buttonPlayers= buttonBar.getPlayers();
		List<Player> diamondPlayers= diamondBar.getPlayers();
		List<Player> timePlayers= timeBar.getPlayers();
		
		for(Player player: playerList){
			if(!(buttonPlayers.contains(player))) buttonBar.addPlayer(player);
			if(!(diamondPlayers.contains(player))) diamondBar.addPlayer(player);
			if(!(timePlayers.contains(player))) timeBar.addPlayer(player);
		}
	}

	private void getChestInventory() {
	}
}
