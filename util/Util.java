package scripts.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.tribot.api.General;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;

public class Util{

	private static boolean isMule = false;
	
	private static long lastPositionUpdate = new Date().getTime() + 30000L;
	
	private static final int MAX_LOG_SIZE = 10;
	
	private static LinkedList<String> dataLog = new LinkedList<String>();
	
	//static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
	static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 
	
	private static long lastLogSend = Util.time();
	private static final long TIME_BETWEEN_LOG = 30000L;
	
	// For screen shots
	private static long lastScreenShot = 0L;
	private static final long SCREEN_SHOT_INTERVAL = 1000L*15L;
	
	

	
	/**
	 * Sleeps for a random number of milliseconds 
	 */
	public static void randomSleep() {
		Util.checkForScreenShot();
		int chance = ThreadLocalRandom.current().nextInt(0, 101);
		
		// 50% chance of a normal sleep
		if(chance < 50) {
			int sleepTime = ThreadLocalRandom.current().nextInt(50, 201);
			General.sleep(sleepTime);
		}else if(chance < 80) { // 30% chance of a slightly longer sleep
			int sleepTime = ThreadLocalRandom.current().nextInt(200, 401);
			General.sleep(sleepTime);
		}else { // 20% chance of an even longer sleep
			int sleepTime = ThreadLocalRandom.current().nextInt(1000, 3000);
			General.sleep(sleepTime);
		}
		
		// Check to make sure the inventory is open
		if(GameTab.getOpen() != GameTab.TABS.INVENTORY) {
			GameTab.open(GameTab.TABS.INVENTORY);
		}
	}
	
	public static void randomSleep(boolean dontChangeInventory) {
		int chance = ThreadLocalRandom.current().nextInt(0, 101);
		
		// 50% chance of a normal sleep
		if(chance < 50) {
			int sleepTime = ThreadLocalRandom.current().nextInt(50, 201);
			General.sleep(sleepTime);
		}else if(chance < 80) { // 30% chance of a slightly longer sleep
			int sleepTime = ThreadLocalRandom.current().nextInt(200, 401);
			General.sleep(sleepTime);
		}else { // 20% chance of an even longer sleep
			int sleepTime = ThreadLocalRandom.current().nextInt(1000, 3000);
			General.sleep(sleepTime);
		}
	}
	
	/**
	 * Sleeps between a set amount of milliseconds
	 * @param min Minimum amount of milliseconds
	 * @param max Maximum amount of milliseconds
	 */
	public static void randomSleepRange(int min, int max) {
		
		// Make sure we're logged in
		if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Login.login();
		}
		
		// Update the position
		if(new Date().getTime() >= lastPositionUpdate && !isMule && Network.isInit) {
			// Update the position
			try {
				Network.updatePosition();
			} catch (Exception e) {
				Util.log("Unable to update position");
				e.printStackTrace();
			}
			
			lastPositionUpdate = new Date().getTime() + 30000L;
		}
		
		Util.checkForScreenShot();
		int sleepTime = ThreadLocalRandom.current().nextInt(min, max+1);
		General.sleep(sleepTime);
		
		
	}
	
	public static void randomSleepRange(int min, int max,boolean isAutoLog) {
		int sleepTime = ThreadLocalRandom.current().nextInt(min, max+1);
		General.sleep(sleepTime);
	}
	
	public static void randomTypeSleep() {
		int rand = ThreadLocalRandom.current().nextInt(1, 100+1);
		if(rand <= 70) {
			randomSleepRange(120,200);
		}else {
			randomSleepRange(800, 1300);
		}
		
		
	}
	
	public static void log(String output) {
		
		General.println(output);
		
		Date date = new Date(); 
		dataLog.add(formatter.format(date) + ": " + output);
		
		// If we are over the max log side OR the max time between logs 
		// AND the network has be init'd
		if((dataLog.size() > MAX_LOG_SIZE || (lastLogSend + TIME_BETWEEN_LOG) < Util.time()) && Network.isInit) {
			
			// Build the output string
			String outputString = "";
			for(String logRow : dataLog) {
				outputString += logRow + "\n";
			}
			
			// Try to send the log
			try {
				Network.sendLog(outputString);
			} catch (Exception e) {
				System.out.println("Log(): Error sending log to server");
				e.printStackTrace();
			}
			
			// Clear the log and reset the time
			dataLog.clear();
			lastLogSend = Util.time();
		}
		
		
		
	}
	
	public static void log(int output) {
		log(output + "");
	}
	
	public static void forceLog() {
		if(!Network.isInit) {
			return;
		}
		// Build the output string
		String outputString = "";
		for(String logRow : dataLog) {
			outputString += logRow + "\n";
		}
		
		// Try to send the log
		try {
			Network.sendLog(outputString);
		} catch (Exception e) {
			System.out.println("Log(): Error sending log to server");
			e.printStackTrace();
		}
		
		// Clear the log and reset the time
		dataLog.clear();
		lastLogSend = Util.time();
	}
	
	public static void waitTillMovingStops() {
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(200,1000);
		    if(!Player.isMoving()) {
		    	break;
		    }
		}
	}
	
	public static void clearConsole() {
		for(int i = 0; i < 10; i++) {
			log("");
		}
	}
	
	public static boolean walkToGESpot() {
		// Get the position of the player
		RSTile currentPosition = Player.getPosition();
		RSTile allowedTiles[] = {new RSTile(3167,3488, 0), new RSTile(3162,3488, 0)};
		
		Camera.setCamera(0,100);
		
		// For each of the allowed tiles to stand on
		for(RSTile tile : allowedTiles) {
			// If the player is standing on this tile
			if(currentPosition.getX() == tile.getX() && currentPosition.getY() == tile.getY()) {
				return true;
			}
		}
		
		// If not standing on the correct tile, find the closest one and move to it
		RSTile closestTile = null;
		int closestTileDistance = Integer.MAX_VALUE;
		// Find which tile is the closest
		for(RSTile tile : allowedTiles) {
			// Get the distance to this tile
			int distance = Player.getPosition().distanceTo(tile);
			// If this tile is closer than the current tile, use this one
			if(distance < closestTileDistance) {
				closestTileDistance = distance;
				closestTile = tile;
			}
		}
		
		// Walk to the closest tile
		if(Walking.clickTileMM(closestTile, 1) == false) {
			Util.log("Could not find GE sqaure to move to");
			return false;
		}
			
		Util.randomSleep();
		// Wait till we stop moving;
		Util.waitTillMovingStops();
		
		// Loop this until we're in the correct spot(s)
		return walkToGESpot();
	}
	
	
	public static void setMule() {
		isMule = true;
	}
	
	public static long time() {
		return new Date().getTime();
	}
	
	public static long secondsLater(int numberOfSeconds) {
		return new Date().getTime() + Long.valueOf(numberOfSeconds*1000);
	}
	
	public static int randomNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min-1, max+1);
	}
	
	/**
	 * Waits 15 seconds or for the interface to be loaded and ready
	 * @param interfaceNum
	 * @return
	 */
	public static void waitForInterface(int interfaceNum, int childNum) {
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    
		    if(Interfaces.get(interfaceNum, childNum) != null && Interfaces.get(interfaceNum, childNum).isClickable()) {
		    	break;
		    }
		}
	}
	
	public static String getMembershipLeft() {
		if(Interfaces.get(109) != null) {
			if(Interfaces.get(109).getChild(25) != null) {
				return Interfaces.get(109).getChild(25).getText().replace("<col=00ff00>", "")
						.replace("<col=ffff00>", "")
						.replace("<col=ff0000>", "")
						.replace("</col>", "")
						.replace("Membership: ", "");
			}
		}
		return null;
	}
	
	public static LinkedList<Integer> addToStartOfArray(LinkedList<Integer> oldList, List<Integer> newValues){
		
		LinkedList<Integer> newList = new LinkedList<Integer>();
		newList.addAll(newValues);
		newList.addAll(oldList);
		
		return newList;
	}
	
	public static void checkForScreenShot() {
		// Check if we need to take another screenshot
		if(Util.time() >= lastScreenShot + SCREEN_SHOT_INTERVAL && Network.isInit) {
			lastScreenShot = Util.time();
			Util.log("run(): Updating screen shot");
			
			new Thread(() -> {
				Network.updateScreenShot();
			}).start();
		}
	}
	
	public static String takeScreenShot() {	
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageIO.write(Screenshots.getScreenshotImage(), "png", output);
		} catch (IOException e) {
			Util.log("takeScreenShot(): Error taking screen shot");
			return null;
		}
		return "data:image/png;base64," + DatatypeConverter.printBase64Binary(output.toByteArray());
	}
}
