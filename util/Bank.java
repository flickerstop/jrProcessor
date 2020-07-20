package scripts.util;

import java.util.Date;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;

public class Bank {
	
	
	public final static int GP_TO_USE = 1500000;
	public final static int MAX_GP_ALLOWED = 2000000;
	
	/**
	 * Attempts to open the bank
	 * @return false if the bank could not be opened
	 */
	public static boolean openBank() {
		
		if(Banking.isBankScreenOpen()) {
			return true;
		}
		
		// Check if the GE is open
		if(GrandExchange.getWindowState() != null) {
			GE.closeGE();
			Util.randomSleep();
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
			
			Util.randomSleepRange(3000, 5000);
			
			// Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				break;
			}
			
		}
		
		
		return true;
	}
	
	/**
	 * Empty's all the finished products from the bank
	 * @return 
	 */
	public static boolean emptyBank() {
		Bank.openBank();
		// Set to notes
		Mouse.moveBox(173,312,218,329);
		Mouse.click(1);
		Util.randomSleep();
		
		Util.log("Grabbing finished products");
		for(ProcessingObject obj : ItemProcessManager.getListOfProcesses()) {
			// If this item is to be sold
			if(!obj.isSell) {
				continue;
			}
			
			// Check if more than 1 in the bank
			int amount = Banking.find(obj.result).length != 0 ? Banking.find(obj.result)[0].getStack() : 0;
			
			if(amount > 0) {
				Banking.withdraw(0, obj.result);
				Util.randomSleep();
			}
		}
		
		Util.log("Grabbing coins");
		Bank.grabCoins();
		Util.randomSleep();
		
		// Check to make sure coins are in inventory
		if(Inventory.find("Coins").length == 0 || Banking.find("Coins").length != 0) {
			return emptyBank();
		}
		
		Util.log("Coins in inventory: "+ Inventory.find("Coins")[0].getStack());
		
		
		return true;
	}
	
	/**
	 * Counts the amount of extra herbs in the bank
	 * @return int - Amount of herbs
	 */
	public static int countHerbs() {
		
		int totalCount = 0;
		
		// Look for grimy
		for(RSItem herb : Banking.find("Grimy ranarr weed","Grimy toadflax","Grimy irit leaf","Grimy avantoe","Grimy kwuarm","Grimy snapdragon","Grimy cadantine","Grimy lantadyme","Grimy dwarf weed")) {
			totalCount += herb.getStack();
		}

		for(RSItem herb : Banking.find("Ranarr weed","Toadflax","Irit leaf","Avantoe","Kwuarm","Snapdragon","Cadantine","Lantadyme","Dwarf weed")) {
			totalCount += herb.getStack();
		}

		
		return totalCount;
	}
	
	/**
	 * Counts the amount of extra vials in the bank
	 * @return int - Amount of vials
	 */
	public static int countVials() {
		return Banking.find("Vial of water").length != 0 ? Banking.find("Vial of water")[0].getStack() : 0;
	}
	
	/**
	 * Grabs the coins from the bank
	 * @return
	 */
	public static boolean grabCoins() {
		int amount = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		
		if(amount > 0) {
			Banking.withdraw(0, "Coins");
		}
		
		Util.randomSleep();
		return true;
	}
	
	/**
	 * Converts the amount of GP in your inventory/bank to plat tokens
	 * @return 
	 */
	public static boolean convertToPlatTokens() {
		
		
		
		// Make sure the GE is closed
		GE.closeGE();
		
		Util.randomSleepRange(2000, 4000);
		
		// Open the bank
		Bank.openBank();
		
		Util.randomSleepRange(2000, 4000);
		
		// Get the amount of coins in the inventory
		
		int inventoryCoins = Inventory.find("Coins").length != 0 ? Inventory.find("Coins")[0].getStack() : 0;
		int bankCoins = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		
		if(bankCoins > 0) {
			Banking.withdraw(0, "Coins");
			Util.randomSleepRange(2000, 4000);
		}
		
		int totalCoins = bankCoins+inventoryCoins;
		
		// leave 2m in the bank
		Banking.deposit(GP_TO_USE, "Coins");
		
		Util.randomSleepRange(2000, 4000);
		
		// Close the bank
		Banking.close();
		
		Util.randomSleepRange(2000, 4000);
		
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
		
		while(Inventory.find("Platinum token").length == 0) {
			// Click the coins
			Inventory.find("Coins")[0].click();
			Util.randomSleepRange(3000, 4000);
			closestNPC.click("Use Coins -> Banker");
			Util.randomSleepRange(4000, 6000);
			while(true) {
				

				Keyboard.sendType('1');
				Util.randomSleep();

				long endTime = new Date().getTime() + 10000L;
				// If the current time is larger than the end time
				if(new Date().getTime() > endTime) {
					Util.log("Waited long enough");
					break;
				}
				
				if(Interfaces.get(219) == null) {
					break;
				}
			}
			Util.randomSleepRange(2000, 4000);
		}
		
		
		// Open the bank
		Bank.openBank();
		Util.randomSleepRange(2000, 4000);
		Banking.depositAll();
		Util.randomSleepRange(1000, 2000);
		
		return true;
	}
}
