package scripts.util;

import java.util.Date;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;

public class Bank {
	
	
	public final static int GP_TO_USE = 1000000;
	public final static int MAX_GP_ALLOWED = 2000000;
	
	public static int herbsInBank = 0;
	public static int vialsInBank = 0;
	
	
	public static int gpInBank = 0;
	private static int maxGPInBank = 0;
	public static int platInBank = 0;
	
	/**
	 * Attempts to open the bank
	 * @return false if the bank could not be opened
	 */
	public static boolean openBank() {
		//Util.log("openBank(): ");
		Util.log("openBank(): Opening Bank");
		if(Banking.isBankScreenOpen()) {
			Util.log("openBank(): Bank already open");
			return true;
		}
		
		// Check if the GE is open
		if(GrandExchange.getWindowState() != null) {
			Util.log("openBank(): Grand Exchange opened");
			GE.closeGE();
			Util.randomSleep();
		}
		
		
		Util.walkToGESpot();
		
		
		Util.log("openBank(): Searching for bank booth");
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
		
		Util.log("openBank(): Opening Bank");

		// If item is selected
		if(Game.getItemSelectionState() == 1) {
			Util.log("openBank(): un-selecting item");
			// click the first item in the inventory
			Inventory.getAll()[0].click();
			Util.randomSleep();
		}
		
		// Right click the closest NPC and exchange
		if(!closestBooth.click("Bank Grand Exchange booth")) {
			return false;
		}
		
		Util.log("openBank(): Waiting for bank to be open");
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    // Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				break;
			}
		}
		
		// Check if "member's object"
//		if(Banking.find("Members object").length != 0) {
//			Network.updateBotSubTask("NO MEMBERSHIP!");
//			Login.logout();
//			Util.randomSleepRange(1000*60*60*24, 1000*60*60*25, false);
//		}
		
		if(Banking.getWithdrawQuantity() != Banking.WITHDRAW_QUANTITY.WITHDRAW_X) {
			Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_X);
		}
		
		gpInBank = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		platInBank = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		setMaxGPInBank(gpInBank);
		return true;
	}
	
	public static boolean hoverBank() {
		Util.log("hoverBank(): Hovering Bank");
		
		if(Banking.isBankScreenOpen()) {
			Util.log("hoverBank(): Bank already open");
			return false;
		}
		
		// Check if the GE is open
		if(GrandExchange.getWindowState() != null) {
			Util.log("hoverBank(): Grand Exchange opened");
			GE.closeGE();
			Util.randomSleep();
		}
		
		
		Util.walkToGESpot();
		
		
		Util.log("hoverBank(): Searching for bank booth");
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
		
		Util.log("hoverBank(): Hovering Bank");

		// If item is selected
		if(Game.getItemSelectionState() == 1) {
			Util.log("hoverBank(): un-selecting item");
			// click the first item in the inventory
			Inventory.getAll()[0].click();
			Util.randomSleep();
		}
		
		// Right click the closest NPC and exchange
		if(!closestBooth.hover()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Empty's all the finished products from the bank
	 * @return 
	 */
	public static boolean emptyBank() {
		// Util.log("emptyBank(): ");
		
		if(!Bank.openBank()) {
			Util.log("emptyBank(): bank not open");
			return false;
		}
		
		Util.randomSleep();
		Banking.depositAll();
		Util.randomSleep();
		
		// Set to notes
		Mouse.moveBox(173,312,218,329);
		Util.randomSleep();
		Mouse.click(1);
		Util.randomSleep();
		
		Util.log("emptyBank(): Grabbing finished products");
		for(ProcessingObject obj : ItemProcessManager.getListOfProcesses()) {
			// Check if more than 1 in the bank
			int amount = Banking.find(obj.result).length != 0 ? Banking.find(obj.result)[0].getStack() : 0;
			if(amount < 10) {
				continue;
			}
			if(amount > 0) {
				Banking.withdraw(0, obj.result);
				Util.randomSleep();
			}
			
			// Check if more than 1 in the bank
			amount = Banking.find(obj.item1).length != 0 ? Banking.find(obj.item1)[0].getStack() : 0;
			if(amount < 10) {
				continue;
			}
			if(amount > 0) {
				Banking.withdraw(0, obj.item1);
				Util.randomSleep();
			}
			
			// Check if more than 1 in the bank
			amount = Banking.find(obj.item2).length != 0 ? Banking.find(obj.item2)[0].getStack() : 0;
			if(amount < 10) {
				continue;
			}
			if(amount > 0) {
				Banking.withdraw(0, obj.item2);
				Util.randomSleep();
			}
		}
		
		Util.log("emptyBank(): Grabbing coins");
		Bank.grabCoins();
		Util.randomSleep();
		
		// Check to make sure coins are in inventory
		if(Inventory.find("Coins").length == 0 || Banking.find("Coins").length != 0) {
			return false;
		}
		
		//Util.log("Coins in inventory: "+ Inventory.find("Coins")[0].getStack());
		
		
		return true;
	}
	
	/**
	 * Counts the amount of extra herbs in the bank
	 * @return int - Amount of herbs
	 */
	public static void countHerbs() {
		//Util.log("countHerbs(): ");
		Util.log("countHerbs(): Counting");
		int totalCount = 0;
		
		// Look for grimy
		for(RSItem herb : Banking.find("Grimy ranarr weed","Grimy toadflax","Grimy irit leaf","Grimy avantoe","Grimy kwuarm","Grimy snapdragon","Grimy cadantine","Grimy lantadyme","Grimy dwarf weed")) {
			totalCount += herb.getStack();
		}

		for(RSItem herb : Banking.find("Ranarr weed","Toadflax","Irit leaf","Avantoe","Kwuarm","Snapdragon","Cadantine","Lantadyme","Dwarf weed")) {
			totalCount += herb.getStack();
		}

		Util.log("countHerbs(): found: "+totalCount);
		herbsInBank = totalCount;
	}
	
	/**
	 * Counts the amount of extra vials in the bank
	 * @return int - Amount of vials
	 */
	public static void countVials() {
		vialsInBank = Banking.find("Vial of water").length != 0 ? Banking.find("Vial of water")[0].getStack() : 0;
	}
	
	/**
	 * Grabs the coins from the bank
	 * @return
	 */
	public static boolean grabCoins() {
		//Util.log("grabCoins(): ");
		Util.log("grabCoins(): Grabbing coins");
		int amount = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		
		if(amount > 0) {
			if(!Banking.withdraw(0, "Coins")) {
				Util.log("grabCoins(): Failed at grabbing coins");
				return false;
			}
		}
		
		Util.randomSleep();
		return true;
	}
	
	/**
	 * Converts the amount of GP in your inventory/bank to plat tokens
	 * @return 
	 */
	public static boolean convertToPlatTokens() {
		//Util.log("convertToPlatTokens(): ");
		
		Util.log("convertToPlatTokens(): Counting coins");
		// Get the amount of coins in the inventory
		int inventoryCoins = Inventory.find("Coins").length != 0 ? Inventory.find("Coins")[0].getStack() : 0;
		int bankCoins = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		
		if(bankCoins > 0) {
			if(!Banking.withdraw(0, "Coins")) {
				Util.log("convertToPlatTokens(): Failed to take coins out");
				return false;
			}
			Util.randomSleep();
		}
		
		int totalCoins = bankCoins+inventoryCoins;
		
		Util.log("convertToPlatTokens(): putting "+GP_TO_USE+" back in bank");
		// leave 2m in the bank
		Banking.deposit(GP_TO_USE, "Coins");
		
		Util.randomSleep();
		
		Util.log("convertToPlatTokens(): Closing bank");
		// Close the bank
		if(!Banking.close()) {
			Util.log("convertToPlatTokens(): Failed to close bank");
			return false;
		}
		
		Util.randomSleep();
		
		
		Util.log("convertToPlatTokens(): Finding banker");
		// Use the coins on the banker
		RSNPC closestNPC = null;
		int closestNPCDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSNPC npc : NPCs.find("Banker")) {
			int distance = Player.getPosition().distanceTo(npc.getPosition());
			
			if(distance < closestNPCDistance) {
				closestNPCDistance = distance;
				closestNPC = npc;
			}
		}
		
		Util.log("convertToPlatTokens(): Using coins on banker");
		// Click the coins
		Inventory.find("Coins")[0].click();
		Util.randomSleepRange(2000, 4000);
		closestNPC.click("Use Coins -> Banker");
		
		Util.log("convertToPlatTokens(): Waiting for chat box");
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Interfaces.get(219) != null) {
				break;
			}
		}
		
		waitTill = Util.secondsLater(10);
		Util.log("convertToPlatTokens(): typing 1 in chat");
		while(true) {
			Keyboard.sendType('1');
			Util.randomSleep();

			// If the current time is larger than the end time
			if(Util.time() > waitTill) {
				Util.log("convertToPlatTokens(): Waited long enough");
				break;
			}
			
			if(Interfaces.get(219) == null) {
				break;
			}
		}
		Util.randomSleepRange(2000, 4000);
		
		// if there are no plat tokens
		if(Inventory.find("Platinum token").length != 0) {
			Util.log("convertToPlatTokens(): No plat found in inventory");
			return false;
		}
		
		return true;
	}

	
	public static boolean takeOutPlat() {
		Util.log("takeOutPlat(): Depositing inventory");
		Banking.depositAll();
		Util.randomSleep();
		// Take out plat tokens
		Util.log("takeOutPlat(): Taking out plat tokens");
		if(!Banking.withdraw(0, "Platinum token")) {
			Util.log("takeOutPlat(): failed taking out Plat");
			return false;
		}
		return true;
	}
	
	public static boolean closeBank() {
		if(!Banking.isBankScreenOpen()) {
			Util.log("closeBank(): Bank already closed");
			return true;
		}
		
		if(!Banking.close()) {
			Util.log("closeBank(): Unable to close bank");
			return false;
		}
		return true;
	}

	public static int maxNumberOfInvens(ProcessingObject process) {
		try {
			// Get the amount of items in the bank
			int item1Count = Banking.find(process.item1)[0].getStack();
			int item2Count = Banking.find(process.item2)[0].getStack();

			// How many items is needed per inventory
			int itemsPer = 28 / (process.item1Count + process.item2Count);
			
			// How many inventories can we do
			int item1Inven = item1Count/itemsPer;
			int item2Inven = item2Count/itemsPer;
			
			// Return the lower number
			return item1Inven > item2Inven ? item2Inven : item1Inven;
		
		}catch(Exception e) {
			return 0;
		}
		
	}


	public static boolean depositAll() {
		Banking.depositAll();
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.getAll().length == 0) {
		    	break;
		    }
		}
		return true;
	}

	
	
	
	public static int getMaxGPInBank() {
		return maxGPInBank;
	}

	public static void setMaxGPInBank(int newMaxGPInBank) {
		Util.log("setMaxGPInBank(): new: "+newMaxGPInBank);
		Util.log("setMaxGPInBank(): old: "+maxGPInBank);
		
		if(newMaxGPInBank > maxGPInBank) {
			maxGPInBank = newMaxGPInBank;
		}
	}

}
