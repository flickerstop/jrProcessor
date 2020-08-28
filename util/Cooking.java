package scripts.util;

import java.util.ArrayList;
import java.util.Date;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;

public class Cooking {
	private static int MAKE_INTERFACE_ID = 270;
	
	
	public static boolean cookFishOnFire() {
		String typeOfFish = "";
		// Make sure we have items in the inventory
		if(Inventory.getAll().length == 0) {
			return false;
		}
		
		// Find the type of fish in the inventory
		typeOfFish = fishInInventory();
		if(typeOfFish == null) {
			return false;
		}
		
		// Use the fish on the fire
		if(!useFishOnFire(typeOfFish)) {
			return false;
		}
		
		boolean isThereFishToCook = true;
		long endTime = Util.secondsLater(90);
		// wait for done or level up
		while(isThereFishToCook) {
			
			
			// Check if we're done cooking the fish
			if(Inventory.getCount(typeOfFish) == 0) {
				Util.randomSleepRange(0, 5000);
				return true;
			}
			
			
			// If there is a level up message
			if(Interfaces.get(233) != null) {
				// Check how many fish there are left to cook
				if(Inventory.getCount(typeOfFish) >= Util.randomNumber(2, 10)) {
					Util.randomSleepRange(0, 10000);
					
					// 30% chance to just bank
					if(Util.randomNumber(0, 100) > 70) {
						return true;
					}
					
					if(!useFishOnFire(typeOfFish)) {
						return false;
					}
					endTime = Util.secondsLater(90);
				}
			}
			
			// If the current time is larger than the end time
			if(Util.time() > endTime) {
				Util.log("cookFishOnFire(): Cooked Long Enough");
				break;
			}
			Util.randomSleep();
		}
		

		return false;
	}
	
	private static boolean useFishOnFire(String fishName) {
		

		// Click use on the first item in the inventory
		if(!Inventory.find(fishName)[0].click("Use "+fishName)) {
			return false;
		}
		
		
		// Find the fire
		RSObject closestFire = null;
		int closestfireDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSObject fire : Objects.findNearest(10, "Fire")) {
			int distance = Player.getPosition().distanceTo(fire.getPosition());
			
			if(distance < closestfireDistance) {
				closestfireDistance = distance;
				closestFire = fire;
			}
		}
		
		if(closestFire == null) {
			return false;
		}
		
		// Use the fish on the fire
		if(!closestFire.click("Use "+fishName+" -> Fire")) {
			return false;
		}
		
		// Wait for the interface to open
		long endTime = Util.secondsLater(10);
		System.out.println("cookFishOnFire(): Looking for make interface...");
		while(Util.time() < endTime) {
			if(Interfaces.get(MAKE_INTERFACE_ID) != null && Interfaces.get(MAKE_INTERFACE_ID).isBeingDrawn() && Interfaces.get(MAKE_INTERFACE_ID).isClickable()) {
				break;
			}else {
				Util.randomSleep();
			}
			
			// If the current time is larger than the end time
			if(new Date().getTime() > endTime) {
				return false;
			}
		}
		
		// Spam space until the make interface is gone
		endTime = Util.secondsLater(20);
		Util.log("cookFishOnFire(): Spamming SPACE now");
		while(true) {
			Keyboard.sendPress(' ',32);
			Util.randomSleep();
			Keyboard.sendRelease(' ',32);
			
			
			// If the current time is larger than the end time
			if(new Date().getTime() > endTime) {
				Util.log("cookFishOnFire(): Waited long enough");
				return false;
			}
			
			if(Interfaces.get(MAKE_INTERFACE_ID) == null) {
				break;
			}
		}
		
		Mouse.leaveGame(true);
		
		return true;
	}
	
	private static String fishInInventory() {
		String typeOfFish = null;
		
		ArrayList<String> typesOfFish = new ArrayList<String>();
		typesOfFish.add("Raw anchovies");
		typesOfFish.add("Raw trout");
		typesOfFish.add("Raw tuna");
		
		for(String fish : typesOfFish) {
			if(Inventory.getCount(fish) > 0) {
				return fish;
			}
		}
		
		return null;
	}
}
