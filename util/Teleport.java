package scripts.util;

import java.util.ArrayList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
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
	
	public static boolean tzhaar() {
		// Open the quests tab
		GameTab.open(GameTab.TABS.QUESTS);
		
		Util.randomSleep(true);
		// Click the minigame teleport
		// Check if we're already on that tab
		if(Interfaces.get(76, 8) == null) {
			// Make sure we can see the
			if(!Interfaces.get(629, 13).isHidden() && Interfaces.get(629, 13).isClickable()) {
				Interfaces.get(629, 13).click();
			}
		}
		
		
		// Wait until the drop down is shown
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep(true);
		    if(Interfaces.get(76, 7) != null && !Interfaces.get(76, 7).isHidden() && Interfaces.get(76,7).isClickable()) {
				break;
			}
		}
		
		if(!Interfaces.get(76, 8).getText().equalsIgnoreCase("TzHaar Fight Pit")) {
			// Click the drop down list if it's not currently being shown
			if(Interfaces.get(76, 18).isHidden() && !Interfaces.get(76,18).isClickable()) {
				Interfaces.get(76, 7).click();
				Util.randomSleep(true);
			}
			
			
			// Wait until the drop down is drawn
			waitTill = Util.secondsLater(15);
			while(Util.time() < waitTill) {
			    Util.randomSleep(true);
			    if(!Interfaces.get(76, 18).isHidden() && Interfaces.get(76,18).isClickable()) {
					break;
				}
			}
			
			// click the down arrow until fight caves show
			waitTill = Util.secondsLater(15);
			while(Util.time() < waitTill) {
				// click the down arrow
				Interfaces.get(76, 19).getChild(5).click();
			    Util.randomSleep(true);
			    // If the text is in the drawing area
			    if(Interfaces.get(76, 18).getChild(19).getAbsoluteBounds().y >= 274 && Interfaces.get(76, 18).getChild(19).getAbsoluteBounds().y <= 397) {
					break;
				}
			}
			Util.randomSleep(true);
			Interfaces.get(76, 18).getChild(19).click();
		}
		// click the teleport button
		Interfaces.get(76, 28).click();
		
		waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(2404,5179,0)) < 20) {
				break;
			}
		}
		
		return true;
	}
	
	public static boolean nardah() {
		long waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(3451,2917,0)) < 20) {
				break;
			}
		}
		
		return true;
	}
	
	public static boolean draynor() {
		if(Player.getPosition().distanceTo(new RSTile(3105, 3251, 0)) < 10) {
	    	Util.log("draynor(): Already in Draynor");
	    	return true;
	    }
		
		Util.log("draynor(): switch to equipment Tab");
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			GameTab.open(GameTab.TABS.EQUIPMENT);
		    Util.randomSleep(true);
		    if(GameTab.getOpen() == GameTab.TABS.EQUIPMENT) {
		    	break;
		    }
		}
		
		if(GameTab.getOpen() != GameTab.TABS.EQUIPMENT) {
			Util.log("draynor(): Unable to switch to equipment Tab");
			return false;
	    }
		
		for(String item : Items.amuletOfGlory()) {
			Util.log("draynor(): Looking for "+item);
			// Check if the item is in the inventory
			if(Equipment.find(item).length != 0) {
				Network.updateSubTask("Using "+item);
				Equipment.find(item)[0].click("Draynor Village "+item);
				break;
			}
		}
		
		Util.log("draynor(): Checking if within 10 tiles of teleport spot");
		waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(new RSTile(3105, 3251, 0)) < 10) {
		    	Util.log("draynor(): Teleported");
		    	return true;
		    }
		}
		
		
		
		Util.log("draynor(): Error Teleporting");
		return false;
	}
}
