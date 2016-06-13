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

public class CyclicalDisplayTask extends BukkitRunnable{
	//Pointers
	private ButtonRebirthPlugin plugin;
	private Server server;
	private World world;

	//Config Variables
	public int buttonHealth;
	public int minimumDiamondBlocks;
	public String lastCheckChestTime;

	//Boss bars
	private BossBar buttonBar;
	private BossBar diamondBar;
	private BossBar timeBar;

	//Time
	private LocalDateTime currentTime;
	private LocalDateTime configTime;
	private Duration timeDifference;

	//Chest
	private int chestX;
	private int chestY;
	private int chestZ;
	private Block chestBlock;
	private Chest shrineChest;
	private Inventory shrineInventory;
	private ItemStack[] chestContents;
	private int diamondsInserted;

	//Players
	private List<Player> playerList;
	
	public List<Player> healthBlocked;
	public List<Player> diamondBlocked;
	public List<Player> timeBlocked;
	private int totalDiamonds;
	private int currentBar;

	public CyclicalDisplayTask(ButtonRebirthPlugin initPlugin){
		//INIT - plugin,server
		plugin = initPlugin;
		server = plugin.getServer();
		
		//INIT - buttonBar,diamondBar,timeBar
		buttonBar = server.createBossBar("INIT Easter Egg!", BarColor.PURPLE, BarStyle.SEGMENTED_20);
		diamondBar = server.createBossBar("INIT Easter Egg!", BarColor.BLUE, BarStyle.SOLID);
		timeBar = server.createBossBar("INIT Easter Egg!", BarColor.RED, BarStyle.SOLID);
		
		//INIT - chestX, chestY, chestZ, diamondsInserted
		chestX = plugin.getConfig().getInt("chestX");
		chestY = plugin.getConfig().getInt("chestY");
		chestZ = plugin.getConfig().getInt("chestZ");
		diamondsInserted = plugin.getConfig().getInt("diamondsInserted");
		
		currentBar = 0;
		
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
			plugin.logger.info("Turning off Display...");
			this.cancel();
			return;
		}

		//INIT - world,buttonHealth,minimumDiamondBlocks, diamondsInserted
		world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		buttonHealth = plugin.getConfig().getInt("buttonHealth");
		minimumDiamondBlocks = plugin.getConfig().getInt("minimumDiamondBlocks");
		diamondsInserted = plugin.getConfig().getInt("diamondsInserted");
		
		//INIT - currentTime, configTime, timeDifference
		currentTime = LocalDateTime.now(Clock.systemUTC());
		configTime = LocalDateTime.parse(plugin.getConfig().getString("lastCheckChestTime"));
		timeDifference = Duration.of(currentTime.until(configTime, ChronoUnit.NANOS), ChronoUnit.NANOS);
		
		
		setBarTitles();
		setButtonBarProgress();
		setDiamondBarProgress();
		setTimeBarProgress();

		//INIT - playerList,buttonPlayers,diamondPlayers,timePlayers
		playerList = world.getPlayers();
		
		//Bar Switching
		currentBar++;
		if (currentBar < 60){
			
			//Health Case
			diamondBar.removeAll();
			timeBar.removeAll();
			for(Player player: playerList) buttonBar.addPlayer(player);
			
		} else if (currentBar < 120){
			
			//Diamond Case
			buttonBar.removeAll();
			timeBar.removeAll();
			
			for(Player player: playerList) diamondBar.addPlayer(player);
			
		} else if (currentBar < 180){
			
			//Time Case
			buttonBar.removeAll();
			diamondBar.removeAll();
			
			for(Player player: playerList) timeBar.addPlayer(player);
			
		} else {
			
			buttonBar.removeAll();
			diamondBar.removeAll();
			
			for(Player player: playerList) timeBar.addPlayer(player);
			currentBar = 0;
		}		
	}
	public void setBarTitles(){
		//SET TITLE - buttonBar,diamondBar,timeBar
		buttonBar.setTitle("Button Health: "+ buttonHealth + "HP");
		diamondBar.setTitle("Diamonds Needed At ("+chestX+","+chestY+","+chestZ+ "): " + (diamondsInserted) + "/" + minimumDiamondBlocks);
		timeBar.setTitle("Button Damage in: " + timeDifference.toHours()+":"+ timeDifference.toMinutes()%60 +":"+ (int)Math.floor(timeDifference.toMillis()%60000/1000));
	}
	public void setButtonBarProgress(){
		//SET PROGRESS - buttonBar
		if (buttonHealth < 0){
			buttonBar.setProgress(0.0);
		}else if (buttonHealth > 60){
			buttonBar.setProgress(1.0);
		}else{
			buttonBar.setProgress((double)buttonHealth/60.0);
		}
	}
	public void setDiamondBarProgress(){
		//SET PROGRESS - diamomndBar
		if (minimumDiamondBlocks < 1 || diamondsInserted > minimumDiamondBlocks){
			diamondBar.setProgress(1.0);
		}else if(diamondsInserted < 0){
			diamondBar.setProgress(0.0);
		}else{

			//GET - chestBlock
			chestBlock = world.getBlockAt(chestX, chestY, chestZ);

			//SANITIZE - chestBlock
			if (!(chestBlock.getType() == Material.CHEST)) return;

			//INIT - shrineChest, shrineInventory
			shrineChest = (Chest) chestBlock.getState();
			shrineInventory = shrineChest.getInventory();

			//SET PROGRESS - diamondBar
			if(shrineInventory.contains(Material.DIAMOND_BLOCK, (minimumDiamondBlocks - diamondsInserted))){
				diamondBar.setProgress(1.0);
			}else{
				//INIT - chestContents,totalDiamonds
				chestContents = shrineInventory.getContents();
				totalDiamonds = 0;
				//CALCULATE - totalDiamonds
				for (ItemStack stack: chestContents){
					try{
						if (stack.getType() == Material.DIAMOND_BLOCK){
							totalDiamonds += stack.getAmount();
						}	
					}catch(Exception e){/*stack isn't a diamond block*/}
				}

				diamondBar.setProgress((diamondsInserted+totalDiamonds)/(double)minimumDiamondBlocks);
			}
		}
	}
	public void setTimeBarProgress(){
		//Time Progress Setting
		Duration dayDuration = Duration.of(1,ChronoUnit.DAYS);
		long timeDifferenceMillis = timeDifference.toMillis();
		long dayDurationMillis = dayDuration.toMillis();
		double durationRatio = (double)timeDifferenceMillis /(double)dayDurationMillis;

		//SET PROGRESS - timeBar
		if (durationRatio > 1){
			timeBar.setProgress(1.0);
		}else if(durationRatio < 0){
			timeBar.setProgress(0.0);
		}else{
			timeBar.setProgress(durationRatio);
		}
	}
}
