package scripts;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

import scripts.util.Util;

public class GE {
	static int sellMinus = 3;
	
	public static boolean openGE() {
		// Get the position of the player
		RSTile currentPosition = Player.getPosition();
		RSTile allowedTiles[] = {new RSTile(3167,3488, 0)};
		boolean onCorrectTile = false;
		
		// For each of the allowed tiles to stand on
		for(RSTile tile : allowedTiles) {
			// If the player is standing on this tile
			if(currentPosition.getX() == tile.getX() && currentPosition.getY() == tile.getY()) {
				onCorrectTile = true;
				break;
			}
		}
		
		// If not standing on the correct tile, find the closest one and move to it
		if(!onCorrectTile) {
			RSTile closestTile = null;
			int closestTileDistance = Integer.MAX_VALUE;
			// Find which tile is the closest
			for(RSTile tile : allowedTiles) {
				// Get the distance to this tile
				int distance = Player.getPosition().distanceTo(tile);
				Util.log(distance);
				// If this tile is closer than the current tile, use this one
				if(distance < closestTileDistance) {
					closestTileDistance = distance;
					closestTile = tile;
				}
			}
			
			// Walk to the closest tile
			if(Walking.clickTileMM(closestTile, 1) == false) {
				Util.log("Could not find GE sqaure to move to");
				return false;
			}
			
		}
		
		// Wait till we stop moving;
		Util.waitTillMovingStops();
		
		RSNPC closestNPC = null;
		int closestNPCDistance = Integer.MAX_VALUE;
		// Loop through all NPCs with the correct name
		for(RSNPC npc : NPCs.find("Grand Exchange Clerk")) {
			int distance = Player.getPosition().distanceTo(npc.getPosition());
			
			if(distance < closestNPCDistance) {
				closestNPCDistance = distance;
				closestNPC = npc;
			}
		}
		
		// Right click the closest NPC and exchange
		closestNPC.click("Exchange Grand Exchange Clerk");
		
		GE.waitForMainPage();
		
		// Check to see if the grand exchange window is open
		if(GrandExchange.getWindowState() != null) {
			return true;
		}else {
			return openGE();
		}
		
	}

	
	/**
	 * Creates and confirms a buy offer
	 * @param itemName Name of the item to buy
	 * @param buyPrice Price to buy the item (0 for spamming increase button)
	 * @param quantity Number of items to sell (0 for default 1)
	 */
	public static boolean openBuyOffer(String itemName, int buyPrice, int quantity) {
		GE.makeSureInGE();
		
		boolean isOfferFree = false;
		// Find the first GE slot that's empty
		for(int i = 7; i <= 14; i++) {
			// Look to see if this GE interface offer box is hidden
			RSInterface geOfferBox = Interfaces.get(GrandExchange.INTERFACE_EXCHANGE_ID).getChild(i).getChild(0);
			// If it's not hidden
			if(!geOfferBox.isHidden()) {
				// Open this GE offer
				geOfferBox.click();
				isOfferFree = true;
				break;
			}
		}
		
		if(!isOfferFree) {
			return false;
		}

		GE.waitForNewOfferPage();
		Util.randomSleep();
		
		// Slowly type out the item name
		for(int i = 0; i < itemName.length(); i++) {
			boolean isClicked = false;
			// Look through the options in the text box area
			int componentNum = 0;
			try {
				for(RSInterface temp : Interfaces.get(GrandExchange.INTERFACE_CHATBOX_MASTER_ID).getChild(GrandExchange.INTERFACE_CHATBOX_ITEM_RESULTS_CHILD).getChildren()) {
					// if the interface contains the item name
					if(temp.getComponentName().contains(itemName)) {
						// Click it
						temp.click();
						Util.randomSleepRange(500, 1000);
						isClicked = true;
						break;
					}
					componentNum++;
					// If the component is off screen
					if(componentNum > 26) {
						break;
					}
				}
			}catch(Exception e) {
				// unable to find ge enter box
			}
			// If no interface has been clicked
			if(!isClicked) {
				Keyboard.sendType(Character.toLowerCase(itemName.charAt(i)));
				Util.randomSleepRange(500, 1000);
				// Make sure we're in the GE window for a new offer
				if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
					GE.resetGEWindow();
					return openBuyOffer(itemName, buyPrice, quantity);
				}
			}else {
				break;
			}
		}
		
		Util.randomSleep();
		if(!isInGE()) {
			GE.resetGEWindow();
			return openBuyOffer(itemName, buyPrice, quantity);
		}
		
		// set the buy price
		if(buyPrice == 0) {
			Mouse.moveBox(433,202, 463,222);
			for(int i = 0; i < ThreadLocalRandom.current().nextInt(4, 7+1); i++) {
				Mouse.click(1);
				Util.randomSleepRange(20, 100);
			}
		}else {
			Mouse.moveBox(376,202,406,222);
			Mouse.click(1);
			Util.randomSleepRange(1000, 4000);
			for(int i = 0; i < (buyPrice+"").length(); i++) {
				Keyboard.sendType((buyPrice+"").charAt(i));
				Util.randomSleepRange(50,100);
			}
			Keyboard.pressEnter();
		}
		
		if(!isInGE()) {
			GE.resetGEWindow();
			return openBuyOffer(itemName, buyPrice, quantity);
		}
		
		Util.randomSleep();
		// set the quantity
		if(quantity != 0) {
			Mouse.moveBox(219,202,249,222);
			Mouse.click(1);
			Util.randomSleepRange(1000, 4000);
			for(int i = 0; i < (quantity+"").length(); i++) {
				Keyboard.sendType((quantity+"").charAt(i));
				Util.randomSleepRange(50,100);
			}
			Keyboard.pressEnter();
		}
		Util.randomSleep();
		
		if(!isInGE()) {
			GE.resetGEWindow();
			return openBuyOffer(itemName, buyPrice, quantity);
		}
		
		
		// Click confirm
		Mouse.moveBox(185,269,334,306);
		Mouse.click(1);
		Util.randomSleepRange(1000, 4000);
		
		// Check to see if we're at the "select offer" screen
		if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			return true;
		}else {
			GE.resetGEWindow();
			return openBuyOffer(itemName, buyPrice, quantity);
		}
	}
	
	/**
	 * Creates a sell offer on the GE
	 * @param itemToSell The item to see in the inventory
	 * @param sellPrice The price to sell the item (0 for default)
	 * @param quantity The amount of items to sell (0 for all, -1 for default)
	 * @return true if offer created, false if not
	 */
	public static boolean openSellOffer(RSItem itemToSell, int sellPrice, int quantity) {
		
		makeSureInGE();
		
		// Click on the item in the inventory
		itemToSell.click();
		
		GE.waitForNewOfferPage();
		
		// set the sell price
		if(sellPrice != 0) {
			Mouse.moveBox(376,202,406,222);
			Mouse.click(1);
			Util.randomSleepRange(1000, 4000);
			for(int i = 0; i < (sellPrice+"").length(); i++) {
				Keyboard.sendType((sellPrice+"").charAt(i));
				Util.randomSleepRange(50,100);
			}
			Keyboard.pressEnter();
		}
		Util.randomSleep();
		if(!isInGE()) {
			return false;
		}
		// Set the amount to sell
		if(quantity == 0) {
			Mouse.moveBox(177,202,207,222);
			Mouse.click(1);
		}else if(quantity > 0) {
			Mouse.moveBox(219,202,249,222);
			Mouse.click(1);
			Util.randomSleepRange(1000, 4000);
			for(int i = 0; i < (quantity+"").length(); i++) {
				Keyboard.sendType((quantity+"").charAt(i));
				Util.randomSleepRange(50,100);
			}
			Keyboard.pressEnter();
		}
		Util.randomSleep();
		if(!isInGE()) {
			return false;
		}
		
		// Click confirm
		Mouse.moveBox(185,269,334,306);
		Mouse.click(1);
		Util.randomSleepRange(1000, 4000);
		
		// Check to see if we're at the "select offer" screen
		if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			return true;
		}else {
			return false;
		}
	}

	
	/**
	 * Checks for the best price you can sell an item for
	 * @param itemToCheck The item to check
	 * @return int with the exact best price to sell for
	 */
	public static int checkSellPrice(RSItem itemToCheck) {
		
		return checkSellPrice(itemToCheck.name);
	}
	
	/**
	 * Checks for the best price you can sell an item for
	 * @param itemToCheck The item to check
	 * @return int with the exact best price to sell for
	 */
	public static int checkSellPrice(String itemToCheck) {
		
		if(!openBuyOffer(itemToCheck,0,0)){
			// Offer did not work
			return -1;
		}
		
		
		// find the offer that holds this item bought
		while(true) {
			makeSureInGE();
			
			RSGEOffer boughtItem = null;
			for(RSGEOffer offer : GrandExchange.getOffers()) {
				if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
					if(offer.getItemName().equalsIgnoreCase(itemToCheck)) {
						boughtItem = offer;
					}
				}
			}
			
			if(boughtItem == null) {
				Util.log("ERROR");
				return -1;
			}
			

			
			// Wait till this offer is done
			if(boughtItem.getStatus() != RSGEOffer.STATUS.COMPLETED) {
				Util.randomSleep();
				Util.log("Waiting for offer to end: " + boughtItem.getStatus().name());
			}else {
				// Open the offer
				boughtItem.click();
				GE.waitForOldOfferPage();

				
				// Get the price of the item
				int cost = boughtItem.getTransferredGP();
				
				// Collect the items
				while(GrandExchange.getCollectItems().length != 0) {
					if(!isInGE()) {
						cost = -1;
						break;
					}
					
					
					for(RSItem collect : GrandExchange.getCollectItems()) {
						Util.randomSleep();
						collect.click();
					}
					Util.randomSleepRange(1000, 2000);
				}
				if(cost != -1) {
					GE.waitForMainPage();
					return cost;
				}
				
			}
		}
	}

	public static int checkBuyPrice(RSItem itemToCheck) {
		return GE.checkBuyPrice(itemToCheck,-1);
	}
	
	public static int checkBuyPrice(RSItem itemToCheck, int quantity) {

		if(!openSellOffer(itemToCheck,1,quantity)){
			// Offer did not work
			return -1;
		}
		
		GE.waitForMainPage();
		
		// find the offer that holds this item bought
		while(true) {
			makeSureInGE();
			
			RSGEOffer boughtItem = null;
			for(RSGEOffer offer : GrandExchange.getOffers()) {
				if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
					if(offer.getItemName().equalsIgnoreCase(itemToCheck.name)) {
						boughtItem = offer;
					}
				}
			}
			
			if(boughtItem == null) {
				Util.log("ERROR");
				return -1;
			}
			

			
			// Wait till this offer is done
			if(boughtItem.getStatus() != RSGEOffer.STATUS.COMPLETED) {
				Util.randomSleep();
				Util.log("Waiting for offer to end: " + boughtItem.getStatus().name());
			}else {
				// Open the offer
				boughtItem.click();
				GE.waitForOldOfferPage();

				
				// Get the price of the item
				int cost = boughtItem.getTransferredGP();
				
				// Collect the items
				while(GrandExchange.getCollectItems().length != 0) {
					if(!isInGE()) {
						cost = -1;
						break;
					}
					
					
					for(RSItem collect : GrandExchange.getCollectItems()) {
						Util.randomSleep();
						collect.click();
					}
					Util.randomSleepRange(1000, 2000);
				}
				if(cost != -1) {
					GE.waitForMainPage();
					return cost;
				}
			}
		}
	}
	
	/**
	 * Cancels/collects all currently running/finished offers 
	 */
	private static void collectAndCancel() {
		makeSureInGE();
		
		// Click the collect all button first
		Mouse.moveBox(414,61,492,76);
		Mouse.click(1);
		
		Util.randomSleepRange(1000, 2000);
		
		// loop until no offers are found
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
				offer.click();
				GE.waitForOldOfferPage();
				
				// If this offer is not finished
				if(offer.getStatus() == RSGEOffer.STATUS.IN_PROGRESS) {
					// Cancel it
					Mouse.moveBox(349,266,367,283);
					Mouse.click(1);
					
					// Wait for offer to end
					while(true) {
						if(!isInGE()) {
							collectAndCancel();
							return;
						}
						
						Util.randomSleep();
						if(GrandExchange.getStatus() == RSGEOffer.STATUS.CANCELLED || GrandExchange.getStatus() == RSGEOffer.STATUS.COMPLETED) {
							break;
						}
					}
				}
				//Util.randomSleepRange(2000, 5000);
				
				// Collect the items
				while(GrandExchange.getCollectItems().length != 0) {	
					if(!isInGE()) {
						collectAndCancel();
						return;
					}
					for(RSItem collect : GrandExchange.getCollectItems()) {
						Util.randomSleep();
						collect.click();
					}
					Util.randomSleepRange(1000, 2000);
				}
				
				GE.waitForMainPage();
			}
		}
	}
	
	/**
	 * Counts the number of free spots on the GE
	 * @return
	 */
	public static int numFreeSpots() {
		int count = 8;
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
				count--;
			}
		}
		return count;
	}
	
	/**
	 * Sells the entire inventory
	 * @return true if all sold
	 */
	public static boolean sellInventory() {
		while(Inventory.getAll().length > 1) {
			makeSureInGE();
			
			int sellPriceOffset = 2;
			// Loop through the items and sell them all
			for(RSItem item : Inventory.getAll()) {
				if(item.name.equalsIgnoreCase("coins")) {
					continue;
				}
				
				int sellPrice = checkSellPrice(item);
				
				GE.openSellOffer(item, sellPrice-5-sellPriceOffset, 0);
				
				// If there are no free spots
				if(numFreeSpots() == 0) {
					GE.waitThenCancel();
				}
			}
			sellPriceOffset *= 5;
			// If there are no more items to sell, check to see if any offers are taking too long
			GE.waitThenCancel();
		}
		
		
		
		
		
		return true;
	}
	
	private static void makeSureInGE() {
		if(GrandExchange.getWindowState() == null) {
			GE.openGE();
			Util.randomSleepRange(4000, 6000);
		}
	}
	
	private static boolean isInGE() {
		if(GrandExchange.getWindowState() == null) {
			return false;
		}
		return true;
	}
	
	private static void resetGEWindow() {
		GE.closeGE();
		GE.makeSureInGE();
	}
	
	/**
	 * Waits 3 minutes then cancels/collects all remaining offers
	 */
	public static void waitThenCancel() {
		// Calculate 3 minutes in the future
		long endTime = new Date().getTime() + 180000L;
		
		Util.log("Waiting for offers to finish or time to run out...");
		
		while(true) {
			makeSureInGE();
			
			// Get the current time
			long currentTime = new Date().getTime();
			
			// If the current time is larger than the end time
			if(currentTime > endTime) {
				break;
			}
			
			
			boolean isAllDone = true;
			// Scan all the offers
			for(RSGEOffer offer : GrandExchange.getOffers()) {
				if(offer.getStatus() == RSGEOffer.STATUS.IN_PROGRESS) {
					isAllDone = false;
				}
			}
			
			// If there are no offers in progress
			if(isAllDone) {
				break;
			}
				
			Util.randomSleepRange(2000, 4000);
		}
		
		GE.collectAndCancel();
	}
	
	public static void closeGE() {
		while(true) {
			GrandExchange.close();
			Util.randomSleep();
			if(Interfaces.get(GrandExchange.INTERFACE_EXCHANGE_ID) == null) {
				return;
			}
		}
	}
	
 	private static void waitForNewOfferPage() {
		while(true) {
			if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
				Util.randomSleepRange(500,1000);
				return;
			}else {
				Util.randomSleep();
			}
		}	
	}
	
	private static void waitForOldOfferPage() {
		while(true) {
			if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
				Util.randomSleep();
			}else {
				Util.randomSleepRange(500,1000);
				return;
			}
		}	
	}
	
	private static void waitForMainPage() {
		while(true) {
			if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				Util.randomSleep();
			}else {
				Util.randomSleepRange(500,1000);
				return;
			}
		}	
	}
	
}

