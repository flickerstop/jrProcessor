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
import org.tribot.api2007.types.RSTile;

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
	
	public static boolean handInFertilizer() {
		
		
		return NPCTalk.hosidiusClerk();
	}
	
	public static boolean walkToPlows() {
		Walk.walkToRandom(1768, 3550, 1775, 3550, 0);
		return true;
	}
	
	public static boolean walkDocksToHosidius() {
		
		RSTile walkPath[] = {
				new RSTile(1815, 3689, 0),
				new RSTile(1800, 3689, 0),
				new RSTile(1786, 3685, 0),
				new RSTile(1779, 3673, 0),
				new RSTile(1771, 3664, 0),
				new RSTile(1771, 3650, 0),
				new RSTile(1771, 3635, 0),
				new RSTile(1771, 3629, 0),
				new RSTile(1769, 3613, 0),
				new RSTile(1758, 3605, 0),
				new RSTile(1755, 3598, 0)
		};
		
		Walk.walkHumanPath(walkPath);
		
		return true;
	}
	
	public static boolean hosidiusToClerk() {
		
		RSTile walkPath[] = {
				new RSTile(1758, 3585, 0),
				new RSTile(1758, 3570, 0),
				new RSTile(1760, 3553, 0),
				new RSTile(1756, 3540, 0),
				new RSTile(1744, 3533, 0),
				new RSTile(1744, 3517, 0),
				new RSTile(1734, 3508, 0),
				new RSTile(1721, 3514, 0),
				new RSTile(1707, 3520, 0),
				new RSTile(1702, 3529, 0)	
		};
		
		Walk.walkHumanPath(walkPath);
		
		return true;
	}
	
	public static boolean clerkToKitchen() {
		
		RSTile walkPath[] = {
				new RSTile(1687, 3535, 0),
				new RSTile(1672, 3534, 0),
				new RSTile(1663, 3537, 0),
				new RSTile(1654, 3549, 0),
				new RSTile(1639, 3551, 0),
				new RSTile(1629, 3563, 0),
				new RSTile(1628, 3576, 0),
				new RSTile(1639, 3585, 0),
				new RSTile(1653, 3596, 0),
				new RSTile(1667, 3604, 0),
				new RSTile(1679, 3613, 0)
		};
		
		Walk.walkHumanPath(walkPath);
		
		Walk.humanSmallWalk(1679, 3614, 0);
		Util.randomSleep();
		Walk.checkForDoor();
		Util.randomSleep();
		Walk.humanSmallWalk(1675, 3616, 0);
		Util.randomSleep();
		
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

	
	public static boolean bankInKitcken() {
		return Bank.bankChest();
	}
	
	public static boolean bankInTown() {
		Camera.setCamera(90, 100);
		return Bank.bankBooth();
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
