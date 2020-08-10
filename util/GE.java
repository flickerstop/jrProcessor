package scripts.util;

import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
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

import scripts.JrProcessor;

public class GE {
	
	
	private static final int LONGEST_WAIT_TIME_SECONDS = 60*1;
	/**
	 * Attempts to open the GE after walking to the square
	 * @return FAILED_CLOSING, SQUARE_NOT_FOUND, NPC_NOT_FOUND, GE_NOT_OPEN, SUCCESS
	 */
	public static boolean openGE() {
		//Util.log("openGE(): ");
		
		if(Banking.isBankScreenOpen()) {
			Util.log("openGE(): Bank is open, closing the bank");
			Network.updateSubTask("Bank is open, closing bank");
			if(!Banking.close()) {
				JrProcessor.setStatus(JrProcessor.STATUS.FAILED_CLOSING);
				return false;
			}
			Util.randomSleep();
		}
		
		if(GE.isInGE()) {
			Network.updateSubTask("Already in GE");
			Util.log("openGE(): Going back to selection window");
			GrandExchange.goToSelectionWindow();
			Util.randomSleep();
		}
		
		// Get the position of the player
		RSTile currentPosition = Player.getPosition();
		RSTile allowedTiles[] = {new RSTile(3167,3488, 0)};
		boolean onCorrectTile = false;
		
		Util.log("openGE(): Checking if on a correct tile");
		Network.updateSubTask("Checking if standing in correct spot");
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
			Network.updateSubTask("Moving to correct spot");
			Util.log("openGE(): No on a correct tile, finding closest");
			RSTile closestTile = null;
			int closestTileDistance = Integer.MAX_VALUE;
			// Find which tile is the closest
			for(RSTile tile : allowedTiles) {
				// Get the distance to this tile
				int distance = Player.getPosition().distanceTo(tile);
				// If this tile is closer than the current tile, use this one
				if(distance < closestTileDistance) {
					closestTileDistance = distance;
					closestTile = tile;
				}
			}
			
			// Walk to the closest tile
			if(Walking.clickTileMM(closestTile, 1) == false) {
				Util.log("openGE(): Could not find GE square to move to");
				JrProcessor.setStatus(JrProcessor.STATUS.SQUARE_NOT_FOUND);
				return false;
			}else {
				Util.log("openGE(): Walked to correct tile");
			}
			
		}
		
		// Wait till we stop moving;
		Util.waitTillMovingStops();
		
		RSNPC closestNPC = null;
		int closestNPCDistance = Integer.MAX_VALUE;
		Util.log("openGE(): Finding the closest NPC");
		// Loop through all NPCs with the correct name
		for(RSNPC npc : NPCs.find("Grand Exchange Clerk")) {
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
		Network.updateSubTask("Clicking Clerk");
		Util.log("openGE(): Clicking clerk");
		// Right click the closest NPC and exchange
		if(!closestNPC.click("Exchange Grand Exchange Clerk")) {
			
		}
		
		
		Util.log("openGE(): Waiting till window opens");
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
			Util.randomSleep();
			if(GrandExchange.getWindowState() != null) {
				break;
			}
		}
		
		// Check to see if the grand exchange window is open
		if(GrandExchange.getWindowState() != null) {
			Network.updateSubTask("GE open");
			Util.log("openGE(): Done");
			JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
			return true;
		}else {
			Util.log("openGE(): Failed! GE not open");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
	}

	
	/**
	 * Creates and confirms a buy offer
	 * @param itemName Name of the item to buy
	 * @param buyPrice Price to buy the item (0 for spamming increase button)
	 * @param quantity Number of items to sell (0 for default 1)
	 * @return GE_NOT_OPEN, NEW_OFFER_ERROR, NO_FREE_OFFER, SUCCESS
	 */
	public static boolean openBuyOffer(String itemName, int buyPrice, int quantity) {
		//Util.log("openBuyOffer(): ");
		if(!GE.isInGE()) {
			Util.log("openBuyOffer(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		Network.updateSubTask("Buying: "+itemName+" #"+quantity+" @"+buyPrice);
		
		
		
		Util.log("openBuyOffer(): Looking for free offer spot");
		boolean isOfferFree = false;
		// Find the first GE slot that's empty
		for(int i = 7; i <= 14; i++) {
			// Look to see if this GE interface offer box is hidden
			RSInterface geOfferBox = Interfaces.get(GrandExchange.INTERFACE_EXCHANGE_ID).getChild(i).getChild(0);
			// If it's not hidden
			if(!geOfferBox.isHidden()) {
				// Open this GE offer
				if(!geOfferBox.click()) {
					Util.log("openBuyOffer(): Failed at clicking offer spot");
					JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
					return false;
				}
				isOfferFree = true;
				break;
			}
		}
		
		// If there is no free spots
		if(!isOfferFree) {
			Util.log("openBuyOffer(): No free offer spot");
			JrProcessor.setStatus(JrProcessor.STATUS.NO_FREE_OFFER);
			return false;
		}

		Network.updateSubTask("Opening new offer window");
		Util.log("openBuyOffer(): Waiting for new offer window");
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
			Util.randomSleep();
		    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
		    	break;
		    }
		}
		
		// Make sure we're still in the ge
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		
		Util.log("openBuyOffer(): Waiting for the chat box to be clickable");
		waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
			Util.randomSleep();
			// check if the chat box is click able
			if(Interfaces.get(GrandExchange.INTERFACE_CHATBOX_MASTER_ID).isClickable()) {
				break;
			} 
		}
		
		if(!Interfaces.get(GrandExchange.INTERFACE_CHATBOX_MASTER_ID).isClickable()) {
			Util.log("openBuyOffer(): Interface not clickable");
			JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
			return false;
		} 
		
		Util.waitForInterface(162, 45);
		
		Network.updateSubTask("Typing out name: " + itemName);
		
		Util.log("openBuyOffer(): Start typing out name");
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
						Util.randomSleepRange(500, 1000);
						if(!temp.click()) {
							continue;
						}
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
				Util.randomTypeSleep();
				// Make sure we're in the GE window for a new offer
				if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
					Util.log("openBuyOffer(): Attempting to type name while not in GE");
					JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
					return false;
				}
			}else {
				Network.updateSubTask("Clicking item to buy");
				// Wait and see if we clicked the correct thing
				waitTill = Util.secondsLater(5);
				while(Util.time() < waitTill) {
				    Util.randomSleep();
				    if(GrandExchange.getItemName().equalsIgnoreCase(itemName)) {
						break;
					}
				}
				// If we did click the correct thing
				if(GrandExchange.getItemName().equalsIgnoreCase(itemName)) {
					break;
				}
			}
			
			if(i == itemName.length()-1) {
				Util.randomSleepRange(1000, 2000);
			}
		}
		
		// Make sure we're still in the ge
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		
		// Make sure we're putting in an offer for the correct item
		if(!GrandExchange.getItemName().equalsIgnoreCase(itemName)) {
			Util.log(GrandExchange.getItemName());
			Util.log(itemName);
			Util.log("openBuyOffer(): Attempted to make offer on wrong item");
			JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
			return false;
		}
		
		Util.log("openBuyOffer(): Setting buy price: "+ buyPrice);
		
		Network.updateSubTask("Typing buy price");
		// set the buy price
		if(buyPrice == 0) {
			// If the price is less than 2000
			if(GrandExchange.getGuidePrice() < 2000) {
				Util.log("openBuyOffer(): Offer lower than 2k, buying for 10k");
				// Buy it for 10k
				Mouse.moveBox(376,202,406,222);
				Util.randomSleep();
				Mouse.click(1);
				Util.randomSleepRange(3000, 4000);
				for(int i = 0; i < ("10000").length(); i++) {
					Keyboard.sendType(("10000").charAt(i));
					Util.randomSleepRange(120,200);
				}
				Keyboard.pressEnter();
			}else { // spam the 10% up button
				Util.log("openBuyOffer(): Spaming +10% button");
				Mouse.moveBox(433,202, 463,222);
				Util.randomSleep();
				for(int i = 0; i < ThreadLocalRandom.current().nextInt(7, 15+1); i++) {
					Mouse.click(1);
					Util.randomSleepRange(50, 120);
				}
			}
			
		}else if(buyPrice > (GrandExchange.getGuidePrice()*2) && !itemName.equalsIgnoreCase("Vial of water")){
			Util.log("openBuyOffer(): Attempting to buy item for double guide price!");
			Util.log("openBuyOffer(): Guide: "+GrandExchange.getGuidePrice());
			Util.log("openBuyOffer(): Buy offer: "+buyPrice);
			JrProcessor.setStatus(JrProcessor.STATUS.BUYING_OVER_PRICE_ERROR);
			return false;
		}else if(buyPrice == 1) {
			JrProcessor.setStatus(JrProcessor.STATUS.BUYING_1GP_ERROR);
			return false;
		}else {
			Mouse.moveBox(376,202,406,222);
			Util.randomSleep();
			Mouse.click(1);
			
			Util.waitForInterface(162, 44);
			
			Util.log("openBuyOffer(): Typing in item price");
			for(int i = 0; i < (buyPrice+"").length(); i++) {
				Keyboard.sendType((buyPrice+"").charAt(i));
				Util.randomSleepRange(120,200);
			}
			Keyboard.pressEnter();
		}
		
		// Make sure we're still in the ge
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
			return false;
		}
		
		Network.updateSubTask("Typing Quantity");
		Util.randomSleep();
		// set the quantity
		if(quantity != 0) {
			Util.log("openBuyOffer(): Setting quantity");
			
			Mouse.moveBox(219,202,249,222);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleepRange(3000, 4000);
			for(int i = 0; i < (quantity+"").length(); i++) {
				Keyboard.sendType((quantity+"").charAt(i));
				Util.randomSleepRange(120,200);
			}
			Keyboard.pressEnter();
		}
		Util.randomSleep();
		
		// Make sure we're still in the ge
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
			return false;
		}
		
		Network.updateSubTask("Confirming");
		// Click confirm
		Mouse.moveBox(185,269,334,306);
		Util.randomSleep();
		Mouse.click(1);
		Util.randomSleepRange(2000, 3000);
		
		// Check to see if we're at the "select offer" screen
		if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			Network.updateSubTask("Buy offer created");
			Util.log("openBuyOffer(): done");
			JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
			return true;
		}else {
			Util.log("openBuyOffer(): failed");
			JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
			return false;
		}
	}
	
	/**
	 * Creates a sell offer on the GE
	 * @param itemToSell The item to see in the inventory
	 * @param sellPrice The price to sell the item (0 for default)
	 * @param quantity The amount of items to sell (0 for all, -1 for default)
	 * @return GE_NOT_OPEN, NEW_OFFER_ERROR, NO_FREE_OFFER, SUCCESS
	 */
	public static boolean openSellOffer(RSItem itemToSell, int sellPrice, int quantity) {
		//Util.log("openSellOffer(): ");
		if(!GE.isInGE()) {
			Util.log("openSellOffer(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		Network.updateSubTask("Selling: "+itemToSell.name+" #"+quantity+" @"+sellPrice);
		Util.log("Selling: "+itemToSell.name+" #"+quantity+" @"+sellPrice);
		
		Util.log("openSellOffer(): Clicking item to sell");
		// Click on the item in the inventory
		if(!itemToSell.click()) {
			Util.log("openSellOffer(): Clicking item failed");
			JrProcessor.setStatus(JrProcessor.STATUS.NO_INVENTORY_ITEM);
			return false;
		}
		
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
			if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
		    	break;
		    }
		    Util.randomSleep();
		}
		
		// Make sure we're still in the ge
		if(!isInGE() || GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		Network.updateSubTask("Typing out sell price");
		Util.log("openSellOffer(): Setting the sell price");
		// set the sell price
		if(sellPrice != 0) {
			// If the sell price of the item is less than 50gp, insta sell it
			if(GrandExchange.getPrice() < 50) {
				Util.log("openSellOffer(): Item price too low, selling for 1gp");
				sellPrice = 1;
			}
			
			Mouse.moveBox(376,202,406,222);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleep();
			
			Util.waitForInterface(162, 44);
			for(int i = 0; i < (sellPrice+"").length(); i++) {
				Keyboard.sendType((sellPrice+"").charAt(i));
				Util.randomSleepRange(120,200);
			}
			if(Interfaces.isInterfaceSubstantiated(162, 44)) {
				Keyboard.pressEnter();
			}
			
		}
		Util.randomSleep();
		
		// Reset if the GE closed
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		
		Network.updateSubTask("Typing out quantity");
		// Set the amount to sell
		if(quantity == 0) {
			Util.log("openSellOffer(): Selling All items");
			Mouse.moveBox(177,202,207,222);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleepRange(300, 600);
		}else if(quantity > 0) {
			Util.log("openSellOffer(): Selling "+quantity+" items");
			Mouse.moveBox(219,202,249,222);
			Util.randomSleep();
			Mouse.click(1);

			Util.waitForInterface(162, 44);
			for(int i = 0; i < (quantity+"").length(); i++) {
				Keyboard.sendType((quantity+"").charAt(i));
				Util.randomSleepRange(120,200);
			}
			Keyboard.pressEnter();
		}
		Util.randomSleep();
		
		// Reset if the GE closed
		if(!isInGE()) {
			Util.log("openBuyOffer(): No longer in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		Network.updateSubTask("Confirming");
		// Click confirm
		Mouse.moveBox(185,269,334,306);
		Util.randomSleep();
		Mouse.click(1);
		
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    // Check to see if we're at the "select offer" screen
			if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				Network.updateSubTask("Sell offer created");
				JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
				return true;
			}
		}
		
		JrProcessor.setStatus(JrProcessor.STATUS.NEW_OFFER_ERROR);
		return false;
	}

	
	/**
	 * Checks for the best price you can sell an item for
	 * @param itemToCheck The item to check
	 * @return int with the exact best price to sell for<br>
	 * -1 - fail <br>
	 * -2 - offer took too long <br>
	 * -3 - failed at clicking finished offer<br>
	 * -4 - Could not return to main selection
	 */
	public static int checkHighPrice(RSItem itemToCheck) {
		return checkHighPrice(itemToCheck.name);
	}
	
	/**
	 * Checks for the best price you can sell an item for
	 * @param itemToCheck The item to check
	 * @return int with the exact best price to sell for <br>
	 * -1 - fail <br>
	 * -2 - offer took too long <br>
	 * -3 - failed at clicking finished offer<br>
	 * -4 - Could not return to main selection
	 */
	private static int checkHighPrice(String itemToCheck) {
		Network.updateSubTask("Checking price: " + itemToCheck);
		// Util.log("checkHighPrice(): ");
		Util.log("checkHighPrice(): Creating buy offer for: "+itemToCheck);
		if(!openBuyOffer(itemToCheck,0,0)){
			Util.log("checkHighPrice(): Could not create buy offer");
			// Offer did not work
			return -1;
		}
		
		Network.updateSubTask("Looking for offer holding item");
		// Wait till we're at the main offer screen
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				break;
		    }
		}
		if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			Util.log("checkHighPrice(): Not at selection window");
	    	return -1;
	    }
		
		// find the offer that holds this item bought
		Util.log("checkHighPrice(): Looking for buy offer");
		RSGEOffer boughtItem = null;
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
				if(offer.getItemName().equalsIgnoreCase(itemToCheck)) {
					boughtItem = offer;
				}
			}
		}
		
		if(boughtItem == null) {
			Util.log("checkHighPrice(): Unable to find buy offer");
			return -1;
		}
		
		Network.updateSubTask("Waiting for offer to finish");
		Util.log("checkHighPrice(): Waiting for buy offer to finish");
		// Wait till this offer is done
		waitTill = Util.secondsLater(LONGEST_WAIT_TIME_SECONDS);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(boughtItem.getStatus() == RSGEOffer.STATUS.COMPLETED) {
		    	break;
		    }
		}
		
		if(boughtItem.getStatus() != RSGEOffer.STATUS.COMPLETED) {
			Util.log("checkHighPrice(): Offer did not complete after 3 minutes");
			return -2;
		}else {
			Network.updateSubTask("Checking HIGH price");
			Util.log("checkHighPrice(): Opening offer");
			// Open the offer
			if(!boughtItem.click()) {
				Util.log("checkHighPrice(): Failed to click");
				return -3;
			}


			waitTill = Util.secondsLater(5);
			while(Util.time() < waitTill) {
			    Util.randomSleep();
			    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
			    	break;
			    }
			}
			
			if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
				Util.log("checkHighPrice(): Failed to open");
		    	return -3;
		    }

			// Get the price of the item
			int cost = boughtItem.getTransferredGP();
			
			if(!GrandExchange.goToSelectionWindow()) {
				return -4;
			}
			
			GE.collectCompleted();
			
			Util.log("checkHighPrice(): Done");
			Network.updateSubTask("High price:"+cost);
			return cost;
		}
		
	}

	
	/**
	 * Checks the cheapest price you can buy an item for
	 * @param itemToCheck
	 * @return int - amount of gp to pay<br>
	 * -1 - fail <br>
	 * -2 - offer took too long <br>
	 * -3 - failed at clicking finished offer<br>
	 * -4 - Could not return to main selection
	 */
	public static int checkLowPrice(RSItem itemToCheck) {
		return GE.checkLowPrice(itemToCheck,-1);
	}
	
	/**
	 * Checks the cheapest price you can buy an item for
	 * @param itemToCheck Item you wish to check
	 * @param quantity 
	 * @return int - amount of gp to pay<br>
	 * -1 - fail <br>
	 * -2 - offer took too long <br>
	 * -3 - failed at clicking finished offer<br>
	 * -4 - Could not return to main selection
	 */
	public static int checkLowPrice(RSItem itemToCheck, int quantity) {
		// Util.log("checkLowPrice(): ");

		Network.updateSubTask("Low price of: " + itemToCheck.name);
		Util.log("checkLowPrice(): Creating sell offer");
		if(!openSellOffer(itemToCheck,1,quantity)){
			Util.log("checkLowPrice(): Creating offer failed");
			// Offer did not work
			return -1;
		}
		
		Network.updateSubTask("waiting for sell offer");
		// Wait till we're at the main offer screen
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				break;
		    }
		}
		if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			Util.log("checkLowPrice(): Not at selection window");
	    	return -1;
	    }
			
		RSGEOffer boughtItem = null;
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
				if(offer.getItemName().equalsIgnoreCase(itemToCheck.name)) {
					boughtItem = offer;
				}
			}
		}
		
		if(boughtItem == null) {
			Util.log("checkLowPrice(): Unable to find buy offer");
			return -1;
		}
		

		Util.log("checkLowPrice(): Waiting for buy offer to finish");
		// Wait till this offer is done
		waitTill = Util.secondsLater(LONGEST_WAIT_TIME_SECONDS);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(boughtItem.getStatus() == RSGEOffer.STATUS.COMPLETED) {
		    	break;
		    }
		    
		    for(RSGEOffer offer : GrandExchange.getOffers()) {
				if(offer.getStatus() != RSGEOffer.STATUS.EMPTY) {
					if(offer.getItemName().equalsIgnoreCase(itemToCheck.name)) {
						boughtItem = offer;
					}
				}
			}
		}
		Network.updateSubTask("Opening offer");
		// Wait till this offer is done
		if(boughtItem.getStatus() != RSGEOffer.STATUS.COMPLETED) {
			Util.log("checkLowPrice(): Offer took too long");
			return -2;
		}else {
			// Open the offer
			if(!boughtItem.click()){
				return -3;
			}
			
			waitTill = Util.secondsLater(5);
			while(Util.time() < waitTill) {
			    Util.randomSleep();
			    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
			    	break;
			    }
			}
			
			if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.OFFER_WINDOW) {
				Util.log("checkHighPrice(): Failed to open");
		    	return -3;
		    }

			
			// Get the price of the item
			int cost = boughtItem.getTransferredGP();
			
			if(!GrandExchange.goToSelectionWindow()) {
				return -4;
			}
			
			GE.collectCompleted();
			
			Network.updateSubTask("LOW price: "+cost);
			return cost;
		}
	}
	
	/**
	 * Collects all the items in the GE
	 * @return true if success
	 */
	public static boolean collectAndCancel() {

		if(!GE.isInGE()) {
			Util.log("collectAndCancel(): not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		Network.updateSubTask("Canceling offers");
		// Wait till we're at the main offer screen
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				break;
		    }
		}
		if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			Util.log("collectAndCancel(): Not in the GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
	    }
		
		Util.log("collectAndCancel(): Canceling all offers");
		
		boolean anythingInGE = false;
		RSGEOffer lastCanceled = null;
		
		// loop until no offers are found
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() != RSGEOffer.STATUS.EMPTY && offer.getStatus() != RSGEOffer.STATUS.COMPLETED) {
				anythingInGE = true;
				offer.click("Abort offer");
				Util.randomSleep();
				lastCanceled = offer;
			}else if(offer.getStatus() == RSGEOffer.STATUS.COMPLETED) {
				anythingInGE = true;
			}
		}
		
		// wait until all offers canceled
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(lastCanceled == null || lastCanceled.getStatus() == RSGEOffer.STATUS.CANCELLED) {
				break;
		    }
		}
		Network.updateSubTask("All offers canceled");
		
		Util.log("collectAndCancel(): Collecting all offers");
		if(anythingInGE) {
			// Click the collect all button
			Mouse.moveBox(414,61,492,76);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleep();
		}
		
		
		
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
			anythingInGE = false;
		    Util.randomSleep();
		    
		    // loop until no offers are found
			for(RSGEOffer offer : GrandExchange.getOffers()) {
				if(offer.getStatus() != RSGEOffer.STATUS.EMPTY && offer.getStatus() != RSGEOffer.STATUS.COMPLETED) {
					anythingInGE = true;
				}else if(offer.getStatus() == RSGEOffer.STATUS.COMPLETED) {
					anythingInGE = true;
				}
			}
			
			if(!anythingInGE) {
				break;
			}else {
				Mouse.click(1);
			}
		}

		
		
		if(!anythingInGE) {
			JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
			return true;
		}else {
			JrProcessor.setStatus(JrProcessor.STATUS.ITEM_IN_GE);
			return false;
		}
		
		
		
	}
	
	public static boolean collectCompleted() {
		if(!GE.isInGE()) {
			Util.log("collectCompleted(): not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		// Wait till we're at the main offer screen
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				break;
		    }
		}
		
		if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
			Util.log("collectCompleted(): Not in the GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
	    }
		
		
		boolean anythingInGE = false;
		
		// loop until no offers are found
		for(RSGEOffer offer : GrandExchange.getOffers()) {
			if(offer.getStatus() == RSGEOffer.STATUS.COMPLETED) {
				anythingInGE = true;
			}
		}
		
		
		Util.log("collectCompleted(): Collecting all offers");
		if(anythingInGE) {
			// Click the collect all button
			Mouse.moveBox(414,61,492,76);
			Util.randomSleep();
			Mouse.click(1);
			Util.randomSleep();
		}
		
		JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
		return true;
	}
	
	/**
	 * Counts the number of free spots on the GE
	 * @return Number of free spots in the GE
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
	 * @return true if success
	 */
	public static boolean sellInventory() {
		// Util.log("sellInventory(): ");
		
		if(!GE.isInGE()) {
			Util.log("sellInventory(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		if(Inventory.getAll().length == 0) {
			Util.log("sellInventory(): No item in inventory");
			JrProcessor.setStatus(JrProcessor.STATUS.NO_INVENTORY_ITEM);
			return false;
		}
		
		Util.log("sellInventory(): Selling inventory");
		Network.updateSubTask("Items: " + Inventory.getAll().length);
		int sellPriceOffset = 2;
		
		int minInventoryLength = 1;
		while(Inventory.getAll().length > minInventoryLength) {
			minInventoryLength = 1;
			Util.log("sellInventory(): inventory loop started");
			// Loop through the items and sell them all
			for(RSItem item : Inventory.getAll()) {
				Util.log("sellInventory(): item: "+item.name+" count: "+item.getStack());
				if(item.name.equalsIgnoreCase("coins")) {
					Util.log("sellInventory(): ignoring coins");
					continue;
				}
				
				
				int sellPrice = 0;
				
				// If the stack is only 1 item
				if(item.getStack() <= 10) {
					Util.log("sellInventory(): fastSelling "+item.name+" stack size under 10");
					sellPrice = 1;
				}else {
					sellPrice = checkHighPrice(item);
				}
				
				// If there was an error getting the sell price
				if(sellPrice < 0) {
					JrProcessor.setStatus(JrProcessor.STATUS.SELL_INVENTORY_ERROR);
					return false;
				}
				
				if(sellPrice <= 50) {
					GE.openSellOffer(item, sellPrice, 0);
				}else {
					GE.openSellOffer(item, sellPrice-8-sellPriceOffset, 0);
				}
				
				GE.collectCompleted();
				
				// If there are no free spots
				if(numFreeSpots() == 0) {
					GE.waitThenCancel();
				}
			}
			sellPriceOffset *= 5;
			// If there are no more items to sell, check to see if any offers are taking too long
			GE.waitThenCancel();
			Util.randomSleepRange(1000, 2000);
		}
		
		Bank.setMaxGPInBank(Inven.countCoins());
		Util.log("sellInventory(): coins "+Inven.countCoins());
		
		Network.updateSubTask("Inventory Sold");
		
		Bank.setMaxGPInBank(Inventory.find("Coins").length != 0 ? Inventory.find("Coins")[0].getStack() : 0);
		
		JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
		
		
		
		return true;
	}
	
	
	/**
	 * Checks if the bot is in the GE
	 * @return True if in GE
	 */
	public static boolean isInGE() {
		if(GrandExchange.getWindowState() == null) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Waits 3 minutes then cancels/collects all remaining offers
	 * @return true if success
	 */
	public static boolean waitThenCancel() {
		//Util.log("waitThenCancel(): ");
		// Calculate 3 minutes in the future
		long endTime = Util.secondsLater(LONGEST_WAIT_TIME_SECONDS);
		
		Util.log("waitThenCancel(): Waiting for offers to finish or time to run out");
		
		Network.updateSubTask("Waiting for offers to finish");
		while(true) {
			if(!GE.isInGE()) {
				Util.log("waitThenCancel(): Not in GE");
				JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
				return false;
			}
			
			// If the current time is larger than the end time
			if(Util.time() > endTime) {
				Util.log("waitThenCancel(): Time ran out");
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
				Util.log("waitThenCancel(): All offers finished");
				break;
			}
				
			Util.randomSleepRange(2000, 4000);
			
			if(GrandExchange.getWindowState() != GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
				GrandExchange.goToSelectionWindow();
			}
		}
		
		return GE.collectAndCancel();
	}
	
	/**
	 * @return SUCCESS, FAILED_CLOSING
	 */
	public static boolean closeGE() {
		
		if(!isInGE()) {
			Util.log("closeGE(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
			return true;
		}
		
		//Util.log("closeGE(): ");
		Network.updateSubTask("Closing GE");
		Util.log("closeGE(): Closing GE");
		if(!GrandExchange.close(true)) {
			JrProcessor.setStatus(JrProcessor.STATUS.FAILED_CLOSING);
			return false;
		}
		
		long waitTill = Util.secondsLater(5);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Interfaces.get(GrandExchange.INTERFACE_EXCHANGE_ID) == null) {
		    	JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
				return true;
			}
		}
		
		JrProcessor.setStatus(JrProcessor.STATUS.FAILED_CLOSING);
		return false;
	}
	
	public static boolean buyVialsOfWater(int amount) {
		if(!GE.isInGE()) {
			Util.log("buyVialsOfWater(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return false;
		}
		
		if(!GE.openBuyOffer("Vial of water", 15, amount)) {
			Util.log("buyVialsOfWater(): Unable to buy vials of water");
			return false;
		}
		
		if(!GE.waitThenCancel()) {
			Util.log("buyVialsOfWater(): Unable to collect");
			return false;
		}

		JrProcessor.setStatus(JrProcessor.STATUS.SUCCESS);
		return true;
	}


	public static int[] findLowHigh(String itemName) {
		
		Network.updateSubTask("Finding HIGH/LOW of: "+itemName);
		Util.log("Finding HIGH/LOW of: "+itemName);
		// Util.log("buyBestPrice(): xxxxxx");
		if(!isInGE()) {
			Util.log("findLowHigh(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return null;
		}
		
		Util.log("findLowHigh(): Checking HIGH price");
		// Get the highest price of the item
		int highPrice = GE.checkHighPrice(itemName);
		Util.log("findLowHigh(): checkHighPrice() returned: " + highPrice);
		
		
		// If any error was passed
		if(highPrice < 0) {
			Util.log("findLowHigh(): returned error");
			return null;
		}		
		
		if(!isInGE()) {
			Util.log("findLowHigh(): Not in GE");
			JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
			return null;
		}
		
		if(!GE.collectAndCancel()) {
			Util.log("findLowHigh(): Error collecting");
			return null;
		}
		
		// Wait and check if the item appears in the inventory
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Inventory.find(itemName).length != 0) {
		    	break;
		    }
		}
		
		int lowPrice = 0;
		// If the high price is low, dont bother getting the low price
		if(highPrice <= 100) {
			lowPrice = highPrice;
			Util.log("findLowHigh(): High price is low, using it to buy.");
		}else {
			// Find the item in the inventory
			RSItem[] items = Inventory.find(itemName);
			
			// If no item is in the inventory
			if(items.length == 0) {
				Util.log("findLowHigh(): Unable to find item in inventory");
				JrProcessor.setStatus(JrProcessor.STATUS.NO_INVENTORY_ITEM);
				return null;
			}
			
			Util.log("findLowHigh(): Checking LOW price");
			// Get the low price of the item
			lowPrice = GE.checkLowPrice(items[0]);
			Util.log("findLowHigh(): checkLowPrice() returned: " + highPrice);
			
			
			if(!isInGE()) {
				Util.log("findLowHigh(): Not in GE");
				JrProcessor.setStatus(JrProcessor.STATUS.GE_NOT_OPEN);
				return null;
			}
			
			// If any error was passed
			if(lowPrice < 0) {
				return null;
			}	
		}
		
		if(!GE.collectAndCancel()) {
			Util.log("findLowHigh(): Error collecting");
			return null;
		}
		
		return new int[] {lowPrice,highPrice};
	}
}

