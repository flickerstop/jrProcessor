package scripts.util;


import java.util.ArrayList;
import java.util.Date;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import com.sun.glass.events.KeyEvent;

import scripts.JrProcessor;
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
	
	public static boolean needMule = false;
	
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
		
		// Check if we're withdrawing X
		if(Banking.getWithdrawQuantity() != Banking.WITHDRAW_QUANTITY.WITHDRAW_X) {
			Util.log("openBank(): Withdraw X not set, setting now!");
			Util.randomSleep();
			Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_X);
			Util.randomSleep();
		}
		
		// Check if placeholders are on
		if(!Banking.arePlaceholdersOn()) {
			Util.log("openBank(): placeholders not set, setting now!");
			Util.randomSleep();
			Mouse.moveBox(350, 298, 379, 327);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleep();
			
		}
		
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
	public static boolean emptyBank(boolean isNuke) {
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
			if(amount < 10 && !isNuke) {
				continue;
			}
			if(amount > 0) {
				Banking.withdraw(0, obj.result);
				Util.randomSleep();
			}
			
			// Check if more than 1 in the bank
			amount = Banking.find(obj.item1).length != 0 ? Banking.find(obj.item1)[0].getStack() : 0;
			if(amount < 10 && !isNuke) {
				continue;
			}
			if(amount > 0) {
				Banking.withdraw(0, obj.item1);
				Util.randomSleep();
			}
			
			// Check if more than 1 in the bank
			amount = Banking.find(obj.item2).length != 0 ? Banking.find(obj.item2)[0].getStack() : 0;
			if(amount < 10 && !isNuke) {
				continue;
			}
			
			// Vial of water is always item #2
			if(obj.item2.equalsIgnoreCase("Vial of water") && !isNuke) {
				continue;
			}
			
			if(amount > 0) {
				Banking.withdraw(0, obj.item2);
				Util.randomSleep();
			}
		}
		
		Util.log("emptyBank(): Grabbing coins");
		Bank.grabCoins();
		
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Banking.find("Coins").length == 0) {
		    	break;
		    }
		}
		
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
	
	public static boolean grabPlatTokens() {
		//Util.log("grabCoins(): ");
		Util.log("grabPlatTokens(): Grabbing Plat Tokens");
		int amount = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		
		if(amount > 0) {
			if(!Banking.withdraw(0, "Platinum token")) {
				Util.log("grabPlatTokens(): Failed at grabbing Plat Tokens");
				return false;
			}
		}
		
		Util.randomSleep();
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
		
		// Check if the inventory is empty already
		if(Inventory.getAll().length == 0) {
			return true;
		}
		
		
		Banking.depositAll();
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.getAll().length == 0) {
		    	break;
		    }
		}
		
		
		gpInBank = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		platInBank = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		setMaxGPInBank(gpInBank);
		
		return true;
	}

	
	
	
	public static int getMaxGPInBank() {
		return maxGPInBank;
	}

	public static boolean setMaxGPInBank(int newMaxGPInBank) {
		Util.log("setMaxGPInBank(): new: "+newMaxGPInBank);
		Util.log("setMaxGPInBank(): old: "+maxGPInBank);
		
		if(newMaxGPInBank > maxGPInBank) {
			maxGPInBank = newMaxGPInBank;
		}
		
		if(newMaxGPInBank > 2000000) {
			return false;
		}
		
		return true;
	}
	
	public static boolean leave1mInBank() {
		
		int coinsInBank = Banking.find("Coins")[0].getStack();
		
		Util.log("leave1mInBank(): Leaving 1m in the bank");
		//FIXME
		if(!Banking.withdraw(coinsInBank-1500000, "Coins")) {
			Util.log("leave1mInBank(): Unable to withdraw 1m");
			JrProcessor.setStatus(JrProcessor.STATUS.LEAVE_1M_ERROR);
			return false;
		}
		
		return true;
	}


	public static boolean bankBooth() {
		
		Util.log("bankBooth(): Searching for bank booth");
		Network.updateSubTask("Looking for bank booth");
		
		// Look for the closest bank booth
		RSObject closestBooth = null;
		int closestBoothDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSObject booth : Objects.findNearest(10, "Bank booth")) {
			int distance = Player.getPosition().distanceTo(booth.getPosition());
			
			if(distance < closestBoothDistance) {
				closestBoothDistance = distance;
				closestBooth = booth;
			}
		}
		
		if(closestBooth == null) {
			return false;
		}
		
		Util.log("bankBooth(): Opening Bank");
		Network.updateSubTask("Opening bank");
		
		// Right click the closest NPC and exchange
		if(!closestBooth.click("Bank Bank booth")) {
			return false;
		}
		
		Util.log("bankBooth(): Waiting for bank to be open");
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    // Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				return true;
			}
		}
		
		return false;
		
		
	}

	public static boolean takeOutQuestItems() {
		
		boolean hasBeef = Banking.find("Raw beef").length != 0 ? true : false;
		boolean hasRat = Banking.find("Raw rat meat").length != 0 ? true : false;
		boolean hasBear = Banking.find("Raw bear meat").length != 0 ? true : false;
		boolean hasChicken = Banking.find("Raw chicken").length != 0 ? true : false;
		boolean hasGames = Banking.findByNames(Items.gamesNecky()).length != 0 ? true : false;
		boolean hasWealth = Banking.findByNames(Items.ringOfWealth()).length != 0 ? true : false;
		
		if(!hasBeef || !hasRat || !hasBear || !hasChicken || !hasGames || !hasWealth) {
			Util.log("takeOutQuestItems(): Missing item(s)");
			return false;
		}
		
		// Items to take out
		ArrayList<String> items = new ArrayList<String>();
		items.add("Raw beef");
		items.add("Raw rat meat");
		items.add("Raw bear meat");
		items.add("Raw chicken");
		items.add(Banking.findByNames(Items.gamesNecky())[0].getDefinition().getName());
		items.add(Banking.findByNames(Items.ringOfWealth())[0].getDefinition().getName());
		Network.updateSubTask("Taking out items");
		// Loop until we have no more items to move out or 3 minutes have passed
		long maxWait = Util.secondsLater(60*3);
		while(Util.time() < maxWait && items.size() > 0) {
			for(int i = 0; i < items.size(); i++) {
				Banking.withdraw(1, items.get(i));
				
				long waitTill = Util.secondsLater(5);
				while(Util.time() < waitTill) {
				    Util.randomSleep();
				    if(Inventory.find(items.get(i)).length > 0) {
				    	Util.log("takeOutQuestItems(): Withdrawn "+ items.get(i));
				    	items.remove(items.get(i));
				    	i--;
				    	break;
				    }
				}
			}
		}
		
		if(items.size() > 0) {
			Util.log("takeOutQuestItems(): Items still left in bank!");
			return false;
		}
		Util.log("takeOutQuestItems(): Items taken out of the bank");
		return true;
		
		
	}
	
	
	public static boolean openRoguesDenBank() {
		Camera.setCamera(90,100);
		
		//Util.log("openGE(): ");
		
		if(Banking.isBankScreenOpen()) {
			Util.log("openRoguesDenBank(): Bank is open already");
			return true;
		}
		
		RSNPC closestNPC = null;
		int closestNPCDistance = Integer.MAX_VALUE;
		Util.log("openRoguesDenBank(): Finding the closest NPC");
		// Loop through all NPCs with the correct name
		for(RSNPC npc : NPCs.find("Emerald Benedict")) {
			int distance = Player.getPosition().distanceTo(npc.getPosition());
			
			if(distance < closestNPCDistance) {
				closestNPCDistance = distance;
				closestNPC = npc;
			}
		}
		
		if(closestNPC == null) {
			JrProcessor.setStatus(JrProcessor.STATUS.NPC_NOT_FOUND);
			return false;
		}
		
		Network.updateSubTask("Clicking Banker");
		Util.log("openRoguesDenBank(): Clicking Banker");
		// Right click the closest NPC and exchange
		if(!closestNPC.click("Bank Emerald Benedict")) {
			
		}
		
				
		Util.log("openRoguesDenBank(): Waiting for bank to be open");
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    // Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				return true;
			}
		}
		
		return false;
		
		
	}
	
	public static boolean takeOutCookingTrainingFish() {
		
		// Check if we're withdrawing X
		if(Banking.getWithdrawQuantity() != Banking.WITHDRAW_QUANTITY.WITHDRAW_X) {
			Util.log("openBank(): Withdraw X not set, setting now!");
			Util.randomSleep();
			Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_X);
			Util.randomSleep();
		}
				
		String item = null;
		int anchovies = Banking.find("Raw anchovies").length != 0 ? Banking.find("Raw anchovies")[0].getStack() : 0;
		int trout = Banking.find("Raw trout").length != 0 ? Banking.find("Raw trout")[0].getStack() : 0;
		int tuna = Banking.find("Raw tuna").length != 0 ? Banking.find("Raw tuna")[0].getStack() : 0;
		
		// Check what to take out
		if(anchovies > 0) {
			item = "Raw anchovies";
		}else if(trout > 0) {
			item = "Raw trout";
		}else if(tuna > 0) {
			item = "Raw tuna";
		}
		
		// If there are no items to take out
		if(item == null) {
			return false;
		}
		
		// Take the item out
		Banking.withdraw(28, item);
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			
			if(Inventory.getCount(item) > 0) {
				return true;
			}
			
		    Util.randomSleep();
		}
		
		JrProcessor.setStatus(JrProcessor.STATUS.NO_TRAINING_FISH);
		return false;
	}
	
	public static boolean checkForImpCatcherItems() {
		boolean hasRed = Banking.find("Red bead").length != 0 ? true : false;
		boolean hasYellow = Banking.find("Yellow bead").length != 0 ? true : false;
		boolean hasBlack = Banking.find("Black bead").length != 0 ? true : false;
		boolean hasWhite = Banking.find("White bead").length != 0 ? true : false;
		
		boolean hasNecklace = Banking.findByNames(Items.necklaceOfPassage()).length != 0 ? true : false;
		
		if(!hasRed || !hasYellow || !hasBlack || !hasWhite || !hasNecklace) {
			return false;
		}
		
		return true;
	}
	
	public static boolean withdrawImpCatcherItems() {
		
		if(!Banking.withdraw(1, "Red bead")) {
			Util.log("checkForImpCatcherItems(): Failed withdrawing Red Bead");
			return false;
		}
		
		if(!Banking.withdraw(1, "Yellow bead")) {
			Util.log("checkForImpCatcherItems(): Failed withdrawing Yellow Bead");
			return false;
		}
		
		if(!Banking.withdraw(1, "Black bead")) {
			Util.log("checkForImpCatcherItems(): Failed withdrawing Black Bead");
			return false;
		}
		
		if(!Banking.withdraw(1, "White bead")) {
			Util.log("checkForImpCatcherItems(): Failed withdrawing White Bead");
			return false;
		}
		
		if(!Banking.withdrawByNames(1, Items.necklaceOfPassage())) {
			Util.log("checkForImpCatcherItems(): Failed withdrawing Necklace of passage");
			return false;
		}
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    
		    boolean hasRed = Inventory.find("Red bead").length != 0 ? true : Banking.withdraw(1, "Red bead");
			boolean hasYellow = Inventory.find("Yellow bead").length != 0 ? true : Banking.withdraw(1, "Yellow bead");
			boolean hasBlack = Inventory.find("Black bead").length != 0 ? true : Banking.withdraw(1, "Black bead");
			boolean hasWhite = Inventory.find("White bead").length != 0 ? true : Banking.withdraw(1, "White bead");
			boolean hasNecklace = Inventory.find(x -> Items.necklaceOfPassage().contains(x.getDefinition().getName())).length != 0 ? true : Banking.withdrawByNames(1, Items.necklaceOfPassage());
			
			if(hasRed && hasYellow && hasBlack && hasWhite && hasNecklace) {
				return true;
			}
		}
		
		
		return false;
	}
	
	public static boolean convertCoinsToPlat() {
		
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    
		    if(Inventory.find("Coins").length != 0) {
				break;
			}
		}
		
		// Look in the inventory to make sure there's coins
		RSItem coins = Inventory.find("Coins").length != 0?Inventory.find("Coins")[0]:null;
		
		if(coins == null) {
			Util.log("convertCoinsToPlat(): Unable to find coins in inventory");
			return false;
		}
		
		// Click the coins
		if(!coins.click("Use Coins")) {
			Util.log("convertCoinsToPlat(): Unable to click USE on coins");
			return false;
		}
		
		
		// Find the closest banker
		RSNPC closestNPC = null;
		int closestNPCDistance = Integer.MAX_VALUE;
		Util.log("convertCoinsToPlat(): Finding the closest Banker");
		// Loop through all NPCs with the correct name
		for(RSNPC npc : NPCs.find("Banker")) {
			int distance = Player.getPosition().distanceTo(npc.getPosition());
			
			if(distance < closestNPCDistance) {
				closestNPCDistance = distance;
				closestNPC = npc;
			}
		}
		
		if(closestNPC == null) {
			Util.log("convertCoinsToPlat(): Unable to find banker");
			return false;
		}
		
		// Use coins -> Banker
		if(!closestNPC.click("Use Coins -> Banker")) {
			Util.log("convertCoinsToPlat(): Failed using coins on banker");
			return false;
		}
		
		// Click 1 on the keyboard when the dialog is open
		Util.waitForInterface(219, 1);
		
		long endTime = Util.secondsLater(20);
		Util.log("convertCoinsToPlat(): Spamming 1 now");
		while(true) {
			Keyboard.sendType('1');
			Util.randomSleep();
			
			
			// If the current time is larger than the end time
			if(new Date().getTime() > endTime) {
				Util.log("convertCoinsToPlat(): Waited long enough");
				return false;
			}
			
			if(Interfaces.get(219) == null) {
				break;
			}
		}
		
		waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    
		    if(Inventory.find("Platinum token").length > 0) {
				Util.log("convertCoinsToPlat(): Plat tokens in inventory");
				Network.announceNeedMule();
				return true;
			}
		}
		// verify there's plat tokens in the inventory
		Util.log("convertCoinsToPlat(): Failed, no plat tokens found in inventory");
		return false;
	}
	
	public static void withdrawAllFish() {
		
		// Set to notes
		Mouse.moveBox(173,312,218,329);
		Util.randomSleep();
		Mouse.click(1);
		Util.randomSleep();
				
		if(Banking.find("Raw anchovies").length > 0) {
			Banking.withdraw(0, "Raw anchovies");
			Util.randomSleep();
		}
		if(Banking.find("Raw trout").length > 0) {
			Banking.withdraw(0, "Raw trout");
			Util.randomSleep();
		}
		if(Banking.find("Raw tuna").length > 0) {
			Banking.withdraw(0, "Raw tuna");
			Util.randomSleep();
		}
	}
	
	
	public static boolean bankChest() {
		Util.log("bankChest(): Searching for bank chest");
		Network.updateSubTask("Looking for a bank chest");
		
		// Look for the closest bank booth
		RSObject closestBankChest = null;
		int closestChestDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSObject chest : Objects.findNearest(10, "Bank chest")) {
			int distance = Player.getPosition().distanceTo(chest.getPosition());
			
			if(distance < closestChestDistance) {
				closestChestDistance = distance;
				closestBankChest = chest;
			}
		}
		
		if(closestBankChest == null) {
			return false;
		}
		
		Util.log("bankChest(): Opening Bank");
		Network.updateSubTask("Opening bank");
		
		// Right click the closest NPC and exchange
		if(!closestBankChest.click("Use Bank chest")) {
			return false;
		}
		
		Util.log("bankChest(): Waiting for bank to be open");
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    // Make sure the bank is open
			if(Banking.isBankScreenOpen()) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

