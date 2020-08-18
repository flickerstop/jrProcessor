package scripts.util;

import java.util.ArrayList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

public class Teleport {
	public static boolean burthorpe() {
		
		
		
		Util.log("burthorpe(): switch to equipment Tab");
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			GameTab.open(GameTab.TABS.EQUIPMENT);
		    Util.randomSleep(true);
		    if(GameTab.getOpen() == GameTab.TABS.EQUIPMENT) {
		    	break;
		    }
		}
		
		if(GameTab.getOpen() != GameTab.TABS.EQUIPMENT) {
			Util.log("grandExchange(): Unable to switch to equipment Tab");
			return false;
	    }
		
		ArrayList<String> items = new ArrayList<String>();
		items.add("Games necklace(8)");
		items.add("Games necklace(7)");
		items.add("Games necklace(6)");
		items.add("Games necklace(5)");
		items.add("Games necklace(4)");
		items.add("Games necklace(3)");
		items.add("Games necklace(2)");
		items.add("Games necklace(1)");
		
		
		for(String item : items) {
			Util.log("burthorpe(): Looking for "+item);
			// Check if the item is in the inventory
			if(Equipment.find(item).length != 0) {
				Network.updateSubTask("Using "+item);
				Equipment.find(item)[0].click("Burthorpe "+item);
				break;
			}
		}
		
		Util.log("burthorpe(): Checking if within 20 tiles of teleport spot");
		waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(2900, 3553, 0)) < 20) {
		    	Util.log("burthorpe(): Teleported");
		    	return true;
		    }
		}
		
		
		
		Util.log("burthorpe(): Error Teleporting");
		return false;
	}

	public static boolean grandExchange() {
		
		
		
		Util.log("grandExchange(): switch to equipment Tab");
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			GameTab.open(GameTab.TABS.EQUIPMENT);
		    Util.randomSleep(true);
		    if(GameTab.getOpen() == GameTab.TABS.EQUIPMENT) {
		    	break;
		    }
		}
		
		if(GameTab.getOpen() != GameTab.TABS.EQUIPMENT) {
			Util.log("grandExchange(): Unable to switch to equipment Tab");
			return false;
	    }
		
		ArrayList<String> items = new ArrayList<String>();
		items.add("Ring of wealth (5)");
		items.add("Ring of wealth (4)");
		items.add("Ring of wealth (3)");
		items.add("Ring of wealth (2)");
		items.add("Ring of wealth (1)");

		
		
		for(String item : items) {
			Util.log("grandExchange(): Looking for "+item);
			// Check if the item is in the inventory
			if(Equipment.find(item).length != 0) {
				Network.updateSubTask("Using "+item);
				Equipment.find(item)[0].click("Grand Exchange "+item);
				break;
			}
		}
		
		Util.log("grandExchange(): Checking if within 20 tiles of teleport spot");
		waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(3165, 3479, 0)) < 20) {
		    	Util.log("grandExchange(): Teleported");
		    	return true;
		    }
		}
		
		
		
		Util.log("grandExchange(): Error Teleporting");
		return false;
	}

}
