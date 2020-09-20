package scripts.util;

import java.util.Arrays;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;

public class Zeah {
	
	public static boolean buyFavourItems() {
        // Check if we have compost, if we do then check if we have under 950
        if(Inventory.find("Compost").length > 0 ? Inventory.find("Compost")[0].getStack() < 950 : true) {
        	// Calculate how many to buy
        	int tempAmountToBuy = Inventory.find("Compost").length > 0 ? 950 - Inventory.find("Compost")[0].getStack() : 950;
        	Util.log("buyFavourItems(): Attempting to buy compost");
            if(!GE.openBuyOffer("Compost", 0, tempAmountToBuy)) {
            	Util.log("run(): Unable to buy compost");
            	return false;
            }
        }
        
        // Check if we have raw anchovies, if we do then check if we have under 300
        if(Inventory.find("Saltpetre").length > 0 ? Inventory.find("Saltpetre")[0].getStack() < 950 : true) {
        	// Calculate how many to buy
        	int tempAmountToBuy = Inventory.find("Saltpetre").length > 0 ? 950 - Inventory.find("Saltpetre")[0].getStack() : 950;
        	Util.log("buyFavourItems(): Attempting to buy Saltpetre");
            if(!GE.openBuyOffer("Saltpetre", 0, tempAmountToBuy)) {
            	Util.log("run(): Unable to buy Saltpetre");
            	return false;
            }
        }
        return true;
	}
	
	public static void takeOutCompostAndSalt() {
		for(int i = 0; i!=3; i++) {
			Banking.withdraw(4, "Compost");
			Banking.withdraw(4, "Saltpetre");
			Util.randomSleepRange(500,1000);
		}
	}
	
	public static void createFertilizer() {
	    // Check we still have some in the inventory
		if(Inventory.find("Saltpetre").length == 0 || Inventory.find("Compost").length == 0) {
			return;
		}
		
		// get the min amount of items
		int minAmount = Inventory.find("Saltpetre").length > Inventory.find("Compost").length ? Inventory.find("Compost").length : Inventory.find("Saltpetre").length;
		
		RSItem salt[] = Inventory.find("Saltpetre");
		RSItem compost[] = Inventory.find("Compost");
		for(int i = 0; i < minAmount; i++) {
			compost[i].click("Use Compost");
			salt[i].click("Use Compost -> Saltpetre");
			
			if(Inventory.find("Saltpetre").length == 0 || Inventory.find("Compost").length == 0) {
				break;
			}
		}
		Util.randomSleep();
	}
	
	
	
	public static boolean walkToPlows() {
		Walk.walkToRandom(1768, 3550, 1775, 3550, 0);
		return true;
	}
	
	public static boolean teleportToDraynor() {
		// Teleport to draynor
		return Teleport.draynor();
	}
	
	public static boolean walkToVeos() {
		// Walk to Veos
		return Walk.walkToRandom(3052, 3246, 3055, 3248, 0);
	}
	
	public static boolean takeTheBoat() {
		// Travel to port pisc
		return NPCTalk.veos();
	}
	
	public static boolean walkToPloughs() {
		// Walk to plows
		return Walk.walkToRandom(1768, 3550, 1775, 3550, 0);
	}
	
	public static boolean bankInKitcken() {
		Camera.setCamera(90, 100);
		return Bank.bankChest();
	}
	
	public static boolean bankInTown() {
		Camera.setCamera(90, 100);
		return Bank.bankBooth();
	}
	
	public static boolean walkToHousePortal() {
		return Walk.walkToRandom(1742, 3515, 1745, 3518, 0);
	}
	
	public static boolean walkToClerk() {
		return Walk.walkToPosition(1702, 3529, 0);
	}
	
	public static boolean walkToKitchen() {
		if(!Walk.walkToPosition(1679, 3614, 0)) {
			return false;
		}
		
		if(!Walk.checkForDoor()) {
			return false;
		}
		
		if(!Walk.humanSmallWalk(1675, 3616, 0)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean plough() {
		// Open the quests tab
		GameTab.open(GameTab.TABS.QUESTS);
		Util.randomSleep(true);
		
		// Click the favour teleport
		// Check if we're already on that tab
		if(Interfaces.get(245, 4) == null) {
			// Make sure we can see the
			if(!Interfaces.get(629, 18).isHidden() && Interfaces.get(629, 18).isClickable()) {
				Interfaces.get(629, 18).click();
			}
		}
		// Wait until the drop down is shown
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
		    Util.randomSleep(true);
		    if(Interfaces.get(245,4) != null && !Interfaces.get(245,4).isHidden() && Interfaces.get(245,4).isClickable()) {
				break;
			}
		}
		
		int ploughY = 0;
		while(true) {
			// Get the favour
			int favour = Math.round(Float.parseFloat(Interfaces.get(245,4).getChild(5).getText().replace("%", ""))*10);
			Util.log("Favour: "+(favour/10f)+"%");
			
			if(favour >= 200) {
				Util.log("Enough favour earned");
				return true;
			}
			
			
			RSNPC closestPlough = null;
			if(ploughY == 0) {
				closestPlough = NPCs.findNearest(6924,6925)[0];
				// Save the Y coordinate of the closest plough
				ploughY = closestPlough.getPosition().getY();
			}else {
				for(RSNPC plough : NPCs.findNearest(6924,6925)) {
					if(plough.getPosition().getY() == ploughY) {
						closestPlough = plough;
						break;
					}
				}
			}
			
			
			Util.log("Plough Y: " + ploughY);
			
			int distanceToLeft = closestPlough.getPosition().getX() - 1763;
			int distanceToRight = 1777 - closestPlough.getPosition().getX();
			// Moving Left
			if(distanceToLeft > distanceToRight) {
				Walk.humanSmallWalk(closestPlough.getPosition().getX()+2, closestPlough.getPosition().getY(), 0);
				Util.log("Going left");
			}
			// Moving Right
			else {
				Util.log("Going right");
				Walk.humanSmallWalk(closestPlough.getPosition().getX()-2, closestPlough.getPosition().getY(), 0);
			}
			
			
			// Check if we should push or repair
			if(closestPlough.getID() == 6925) {
				Util.log("Repair Plough");
				closestPlough.click("Repair Plough");
			}else{
				Util.log("Pushing Plough");
				while(!closestPlough.click("Push Plough")) {
					Util.randomSleep();
				}
			}
			
			// wait for our animation to be paused for a while
			
			waitTill = Util.secondsLater(60);
			int idleCount = 0;
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(50,100);
			    if(Player.getAnimation() == -1) {
			    	idleCount++;
			    }else {
			    	idleCount = 0;
			    }
			    
			    if(idleCount > 30) {
			    	break;
			    }
			    
			}
		}
	}
}
