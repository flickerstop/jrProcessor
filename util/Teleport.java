package scripts.util;

import java.util.ArrayList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;

public class Teleport {
	public static boolean burthorpe() {
		
		if(Player.getPosition().distanceTo(new RSTile(2900, 3553, 0)) < 20) {
	    	Util.log("burthorpe(): Already in burthorpe");
	    	return true;
	    }
		
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
		
		
		for(String item : Items.gamesNecky()) {
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
	
	public static int checkGamesNecky() {
		RSItem necky = Equipment.find(Equipment.SLOTS.AMULET).length > 0 ? Equipment.find(Equipment.SLOTS.AMULET)[0] : null;
		
		if(necky == null) {
			return 0;
		}
		
		
		switch(necky.name) {
		case "Games necklace(8)":
			return 8;
		case "Games necklace(7)":
			return 7;
		case "Games necklace(6)":
			return 6;
		case "Games necklace(5)":
			return 5;
		case "Games necklace(4)":
			return 4;
		case "Games necklace(3)":
			return 3;
		case "Games necklace(2)":
			return 2;
		case "Games necklace(1)":
			return 1;
		default:
			return 0;
		}
		
		
		
	}

	public static boolean grandExchange() {
		
		if(Player.getPosition().distanceTo(new RSTile(3165, 3479, 0)) < 20) {
	    	Util.log("grandExchange(): Already in GE");
	    	return true;
	    }
		
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
		
		
		for(String item : Items.ringOfWealth()) {
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

	public static boolean wizardTower() {
		
		if(Player.getPosition().distanceTo(new RSTile(3112, 3177, 0)) < 20) {
	    	Util.log("wizardTower(): Already in Wizard Tower");
	    	return true;
	    }
		
		Util.log("wizardTower(): switch to equipment Tab");
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			GameTab.open(GameTab.TABS.EQUIPMENT);
		    Util.randomSleep(true);
		    if(GameTab.getOpen() == GameTab.TABS.EQUIPMENT) {
		    	break;
		    }
		}
		
		if(GameTab.getOpen() != GameTab.TABS.EQUIPMENT) {
			Util.log("wizardTower(): Unable to switch to equipment Tab");
			return false;
	    }
		
		for(String item : Items.necklaceOfPassage()) {
			Util.log("wizardTower(): Looking for "+item);
			// Check if the item is in the inventory
			if(Equipment.find(item).length != 0) {
				Network.updateSubTask("Using "+item);
				Equipment.find(item)[0].click("Wizards' Tower "+item);
				break;
			}
		}
		
		Util.log("wizardTower(): Checking if within 20 tiles of teleport spot");
		waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(3112, 3177, 0)) < 20) {
		    	Util.log("wizardTower(): Teleported");
		    	return true;
		    }
		}
		
		
		
		Util.log("wizardTower(): Error Teleporting");
		return false;
	}
}
