package scripts.util;

import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;

public class Util{

	/**
	 * Sleeps for a random number of milliseconds 
	 */
	public static void randomSleep() {
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
		int sleepTime = ThreadLocalRandom.current().nextInt(min, max+1);
		General.sleep(sleepTime);
	}
	
	public static void log(String output) {
		System.out.println(output);
	}
	
	public static void log(int output) {
		System.out.println(output + "");
	}
	
	public static void waitTillMovingStops() {
		while(Player.isMoving()) {
			randomSleep();
		}
	}
	
	public static void clearConsole() {
		for(int i = 0; i < 10; i++) {
			System.out.println("");
		}
	}
	
	public static boolean walkToGESpot() {
		// Get the position of the player
		RSTile currentPosition = Player.getPosition();
		RSTile allowedTiles[] = {new RSTile(3167,3488, 0)};
		
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
}
