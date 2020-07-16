package scripts;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;
import scripts.util.Util;

public class Bank {
	public static boolean openBank() {
		
		if(Banking.isBankScreenOpen()) {
			return true;
		}
		
		Util.walkToGESpot();
		
		// Look for the closest bank booth
		RSObject closestBooth = null;
		int closestBoothDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSObject booth : Objects.findNearest(5, "Grand Exchange booth")) {
			int distance = Player.getPosition().distanceTo(booth.getPosition());
			
			if(distance < closestBoothDistance) {
				closestBoothDistance = distance;
				closestBooth = booth;
			}
		}
		
		
		// Loop till bank is open
		while(true) {
			// If item is selected
			if(Game.getItemSelectionState() == 1) {
				// click the first item in the inventory
				Inventory.getAll()[0].click();
				Util.randomSleep();
			}
			
			// Right click the closest NPC and exchange
			closestBooth.click("Bank Grand Exchange booth");
			
			Util.randomSleepRange(1000, 2000);
			
			// Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				break;
			}
			
		}
		
		
		return true;
	}
	
	public static boolean emptyBank() {
		// Set to notes
		Mouse.moveBox(173,312,218,329);
		Mouse.click(1);
		Util.randomSleep();
		
		for(ProcessingObject obj : ItemProcessManager.getListOfProcesses()) {
			// If this item is to be sold
			if(!obj.isSell) {
				continue;
			}
			
			// Check if more than 1 in the bank
			int amount = Banking.find(obj.result).length != 0 ? Banking.find(obj.result)[0].getStack() : 0;
			
			if(amount > 0) {
				Banking.withdraw(0, obj.result);
			}
			
			Util.randomSleep();
		}
		
		
		return true;
	}
	
	public static boolean grabCoins() {
		int amount = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		
		if(amount > 0) {
			Banking.withdraw(0, "Coins");
		}
		
		Util.randomSleep();
		
		
		return true;
	}
}
