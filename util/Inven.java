package scripts.util;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;

public class Inven {
	public static boolean hasCoins() {
		if(Inventory.find("Coins").length == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	public static int countCoins() {
		if(!hasCoins()) {
			return -1;
		}
		return Inventory.find("Coins")[0].getStack();
	}
	
	public static boolean wearQuestItems() {
		
		// Check if we have both items
		boolean hasGames = Inventory.find("Games necklace(8)").length != 0 ? true : false;
		boolean hasWealth = Inventory.find("Ring of wealth (5)").length != 0 ? true : false;
		
		// If either items are missing
		if(!hasGames || !hasWealth) {
			Util.log("takeOutQuestItems(): Missing item(s)");
			return false;
		}
		
		
		// Attempt to wear the items
		Inventory.find("Games necklace(8)")[0].click("Wear Games necklace(8)");
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find("Games necklace(8)").length == 0) {
		    	break;
		    }
		}
		
		Inventory.find("Ring of wealth (5)")[0].click("Wear Ring of wealth (5)");
		waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find("Ring of wealth (5)").length == 0) {
		    	break;
		    }
		}
		
		
		// Check if we have both items
		hasGames = Inventory.find("Games necklace(8)").length != 0 ? true : false;
		hasWealth = Inventory.find("Ring of wealth (5)").length != 0 ? true : false;
		
		// If either items are missing
		if(hasGames || hasWealth) {
			Util.log("takeOutQuestItems(): Items were not put on");
			return false;
		}

		
		
		
		Util.log("takeOutQuestItems(): Equipment worn");
		return true;
	}
}
