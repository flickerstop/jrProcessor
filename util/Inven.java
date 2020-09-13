package scripts.util;

import java.util.LinkedList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

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
	
	public static boolean wearHerbQuestItems() {
		// Check if we have both items
		boolean hasGames = Inventory.find(x -> Items.gamesNecky().contains(x.getDefinition().getName())).length != 0 ? true : false;
		boolean hasWealth = Inventory.find(x -> Items.ringOfWealth().contains(x.getDefinition().getName())).length != 0 ? true : false;
		
		// If either items are missing
		if(!hasGames || !hasWealth) {
			Util.log("wearHerbQuestItems(): Missing item(s)");
			return false;
		}
		
		Network.updateSubTask("Wearing games necky");
		// Attempt to wear the items
		Inventory.find(x -> Items.gamesNecky().contains(x.getDefinition().getName()))[0].click();
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find(x -> Items.gamesNecky().contains(x.getDefinition().getName())).length == 0) {
		    	break;
		    }
		}
		
		Network.updateSubTask("Wearing RoW");
		Inventory.find(x -> Items.ringOfWealth().contains(x.getDefinition().getName()))[0].click();
		waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find(x -> Items.ringOfWealth().contains(x.getDefinition().getName())).length == 0) {
		    	break;
		    }
		}
		
		
		// Check if we have both items
		hasGames = Inventory.find(x -> Items.gamesNecky().contains(x.getDefinition().getName())).length != 0 ? true : false;
		hasWealth = Inventory.find(x -> Items.ringOfWealth().contains(x.getDefinition().getName())).length != 0 ? true : false;
		
		// If either item are found in inventory
		if(hasGames || hasWealth) {
			Util.log("wearHerbQuestItems(): Items were not put on");
			return false;
		}

		
		
		
		Util.log("wearHerbQuestItems(): Equipment worn");
		return true;
	}

	public static boolean wearImpCatcherItems() {
		// Check if we have both items
		boolean hasPassage = Inventory.find(x -> Items.necklaceOfPassage().contains(x.getDefinition().getName())).length != 0 ? true : false;
		
		// If either items are missing
		if(!hasPassage) {
			Util.log("wearImpCatcherItems(): Missing item(s)");
			return false;
		}
		
		Network.updateSubTask("Wearing necklace of passage");
		// Attempt to wear the items
		Inventory.find(x -> Items.necklaceOfPassage().contains(x.getDefinition().getName()))[0].click();
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find(x -> Items.necklaceOfPassage().contains(x.getDefinition().getName())).length == 0) {
		    	break;
		    }
		}
		
		
		// Check if we have both items
		hasPassage = Inventory.find(x -> Items.necklaceOfPassage().contains(x.getDefinition().getName())).length != 0 ? true : false;
		
		// If either item are found in inventory
		if(hasPassage) {
			Util.log("wearImpCatcherItems(): Items were not put on");
			return false;
		}

		
		
		
		Util.log("wearImpCatcherItems(): Equipment worn");
		return true;
	}
}
