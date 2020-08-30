package scripts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Login;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;
import scripts.util.Bank;
import scripts.util.Cooking;
import scripts.util.GE;
import scripts.util.Inven;
import scripts.util.NPCTalk;
import scripts.util.Network;
import scripts.util.Teleport;
import scripts.util.Trade;
import scripts.util.Util;
import scripts.util.Walk;


@ScriptManifest(authors = { "JR" }, category = "Tools", name = "jrProcessor")
public class JrProcessor extends Script implements Starting, Breaking, PreBreaking, Ending, MessageListening07{
	
	private static boolean isReadyToBreak = false;
	private static boolean isBreaking = false;
	private static boolean isTradeTime = false;
	
	
	private static final int MIN_OFFSET = 10;
	private static int offset = MIN_OFFSET;
	
	private static final int MAX_ITEMS = 1000;
	
	public static final int MIN_COINS = 400000;
	
	public static boolean isRunning = true;
	public static ProcessingObject currentProcess = null;
	public static LinkedList<Integer> stateOrder = new LinkedList<Integer>();
	
	private static int statusData = 0;
	private static STATUS status = STATUS.NONE;
	private static int currentState = 0;
	
	private static int currentObjective = 0;
	
	
	private static final long TIME_BETWEEN_SERVER_CHECK = 60000L;
	private static long lastServerCheck = Util.time() + TIME_BETWEEN_SERVER_CHECK;
	
	public static enum STATUS {
		NONE,
		SUCCESS,
	    FAILED_CLOSING,
	    SQUARE_NOT_FOUND,
	    NPC_NOT_FOUND,
	    GE_NOT_OPEN,
	    NEW_OFFER_ERROR,
	    NO_FREE_OFFER,
	    NO_INVENTORY_ITEM,
	    ITEM_IN_GE,
	    GENERAL_FAIL,
	    TOOK_TOO_LONG,
	    SELL_INVENTORY_ERROR,
	    BUYING_1GP_ERROR, 
	    BUYING_OVER_PRICE_ERROR, 
	    LEAVE_1M_ERROR,
	    MISSING_VIALS_OF_WATER,
	    BANK_NOT_OPEN,
	    NO_TRAINING_FISH
	}
	
	@Override
	public void onStart() {
		this.setLoginBotState(false);
	}
 
	
	@Override
	public void run() {
		//Util.log("run(): ");
		Util.clearConsole();
		this.setLoginBotState(false);
		
		Util.log("run(): suspending antiban");
		suspendAntiban();
		
		if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Util.log("run(): logging in");
			if(!Login.login()) {
				if(Login.getLoginMessage() == Login.LOGIN_MESSAGE.BANNED) {
					isRunning = false;
					return;
				}
				if(Login.getLoginMessage() == Login.LOGIN_MESSAGE.MEM_WORLD) {
					isRunning = false;
					return;
				}
			}
		}else {
			Util.log("run(): Already logged in");
		}
		
		long waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Login.getLoginState() == Login.STATE.INGAME) {
		    	break;
		    }else if(Login.getLoginState() == Login.STATE.WELCOMESCREEN) {
		    	Mouse.moveBox(276,295,488,373);
		    	Util.randomSleep();
		    	Mouse.click(1);
		    	Util.randomSleepRange(5000, 10000);
		    }
		}

		
		Util.log("run(): network init");
		
		Util.log("run(): "+Network.version);
		
		Util.log("run(): Name: "+Player.getRSPlayer().getName());
		
		Network.init("jrProcessor");
		
		
		// Starting order
		//stateOrder.addAll(Arrays.asList(11,2,1,4,2,10,14));
		
		// testing mule
		//stateOrder.addAll(Arrays.asList(2,10,14,17,11,1,3,6,4,2,10,14,16,11,32));
		
		// Find where to start
		
		
		if(Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) == 1) {
			Util.log("run(): Selected QUESTING state order");
			// 100,14,101,11,102,131,120,110,121,111,125,122,123,103,131,121,112,125,120,113,130
			Camera.setCamera(0,100);
			stateOrder.addAll(Arrays.asList(1001,100,14,101,11,102,131,120,110,121,111,125,122,123,103,131,121,112,125,120,113,130));
//		}else if(Skills.getCurrentLevel(Skills.SKILLS.COOKING) < 68) {
//			Util.log("run(): Selected LEVEL COOKING state order");
//			// TODO here
//			// 1002,10,17,11,1,3,6,270,6,2,10,14,11,131,900,200
//			stateOrder.addAll(Arrays.asList(201,14,202,11,203));
//			Util.log("run(): Selected PROCESSING state order");
		}else if(Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) >= 3) {
			stateOrder.addAll(Arrays.asList(1000,11,2,1,4,2,10,14));
			Util.log("run(): Selected PROCESSING state order");
		}
		
					
		while(isRunning) {
			Util.log("========================================");
			// Check if we are logged out
			if(Login.getLoginState() != Login.STATE.INGAME) {
				Util.log("run(): Account not logged in!");
				stateOrder.addFirst(31);
			}
			
			// Check if any membership is left
			if(Util.getMembershipLeft().equalsIgnoreCase("none")) {
				Util.log("No membership left");
				Login.logout();
				isRunning = false;
				return;
			}
			
			// Check if we should check in with the server
			if((lastServerCheck + TIME_BETWEEN_SERVER_CHECK) <= Util.time() && !isTradeTime) {
				lastServerCheck = Util.time();
				// Check in with the server
				switch(Network.getServerStatus()) {
				case "null":
					
					break;
					
				case "nominal":
					
					break;
				
				case "sellAndWait":
					stateOrder.clear();
					stateOrder.addAll(Arrays.asList(2,10,14,17,11,1,3,6,4,2,10,14,16,11,32));
					isTradeTime = true;
					break;
					
				default:
					break;
				
				}
			}
			
			
			
			//Network.updateSubTask("aaaaaaaaaaaaaa");
			Network.updateSubTask("Getting new state");
			
			// Output the next states
			String allStates = "";
			for(int state : stateOrder) {
				allStates += state+"->";
			}
			Util.log(allStates);
			
			// Check if we need a new state path
			if(stateOrder.size() == 0) {
				stateOrder.addAll(Arrays.asList(2,10,14,15));
			}

			
			// Get the next state
			currentState = stateOrder.removeFirst();
			Util.log("run(): State: "+getStateString());
			try {
				Network.updateJrProcessor();
			} catch (Exception e) {
				Util.log("run(): Error updateJrProcessor()");
				e.printStackTrace();
			}
			
			switch(currentState) {
			////////////////////////////////////////////////////////////////
			case 1: // Open GE
				if(!GE.openGE()) {
					Network.updateSubTask("Failed opening GE");
					Util.log("run(): Failed opening GE");
				}
				break;
			////////////////////////////////////////////////////////////////
			case 2: // Close GE
				if(!GE.closeGE()) {
					Network.updateSubTask("Failed closing GE");
					Util.log("run(): Failed closing GE");
				}
				break;
			////////////////////////////////////////////////////////////////
			case 3: // sell inventory
				if(!GE.sellInventory()) {
					Util.log("run(): Sell inventory error");
					// If the issue was there was no items in the inventory
					if(status == STATUS.NO_INVENTORY_ITEM) {
						Util.log("run(): No items in inventory");
						Network.updateSubTask("No items in inventory");
						// But there are more than 500k
						if(Inven.countCoins() > 500000) {
							Util.log("run(): >500k Coins in inventory, ignoring error");
							Network.updateSubTask("Coins in inventory, success");
							// Call it a success
							status = STATUS.SUCCESS;
						}
					}
				}
				break;
			////////////////////////////////////////////////////////////////
			case 4: // cancel and collect items in GE
				if(!GE.collectAndCancel()) {
					
				}
				break;
			////////////////////////////////////////////////////////////////
            case 5: // Buy all the new items needed
            	Util.log("run(): Buying next items for processing");
                String[] nextItems = Network.getNextItem();
                ArrayList<Integer> buyPrice = new ArrayList<Integer>();
                
                int totalPrice = 0;
                int amountToBuy = 0;
                
                boolean isError = false;
                
                int totalCoins = 0;
                
                // Check if there are coins in the inventory
                if(Inven.hasCoins()) {
                	totalCoins = Inven.countCoins();
                	
                	if(totalCoins < MIN_COINS) {
                		Util.log("run(): Not enough coins in inventory");
                		
                		// Not enough money but there is herbs in the bank
                		if(Bank.herbsInBank >= 1 || Bank.vialsInBank == 0) {
                			Util.log("run(): Buying only vials of water");
                			
                			int vialsToBuy = (Bank.herbsInBank * 15) > totalCoins ? totalCoins/15:Bank.herbsInBank;
                			
                			if(!GE.openBuyOffer("Vial of water", 15, vialsToBuy)){
                				break;
                			}
                		}
                		
                		break;
                	}
                }
            	
                // Get the prices to buy the items at
                for(String item : nextItems) {
                	Util.log("run(): Checking price of: "+item);
                	// check if it is any predefined prices
                	if(item.equalsIgnoreCase("Vial of water")) {
						buyPrice.add(15);
						totalPrice += 15;
						continue;
					}else if(item.equalsIgnoreCase("Maple logs")) {
						buyPrice.add(25);
						totalPrice += 25;
						continue;
					}
                	
                	
                	
                	int lowHigh[] = GE.findLowHigh(item);
                	
                	// check for error
                	if(lowHigh == null) {
                		Util.log("run(): Failed getting low/high for: "+ item);
                		isError = true;
                		break;
                	}
                	
                	// get the buy/sell price
                	int low = lowHigh[0];
                	int high = lowHigh[1];
                	
                	// Check if either are below 100
                	if(low < 100 || high < 100) {
                		buyPrice.add(500);
                		totalPrice += 500;
                	}else {
                		buyPrice.add(high+offset);
                		totalPrice += high+offset;
                	}
                }
                
                // if there is no error
                if(!isError) {
                	
                	amountToBuy = (int)Math.floor((totalCoins-30000)/totalPrice);
                	
                	if(amountToBuy > MAX_ITEMS) {
    					amountToBuy = MAX_ITEMS;
    				}
                	
                	Util.log("run(): Buying "+amountToBuy+" sets");
                	
                	// buy the items
                	for(int i = 0; i <= nextItems.length-1; i++) {
                		
                		int tempAmountToBuy = amountToBuy;
                		
                		// Check if we need more/less vials of water
                		if(nextItems[i].equalsIgnoreCase("Vial of water")) {
    						tempAmountToBuy += Bank.herbsInBank;
    						
    						// If we already have this many vials in the bank
    						if((tempAmountToBuy-Bank.vialsInBank) <= 0) {
    							Util.log("run(): Skipped buying vials, enough in bank");
    							continue;
    						}else {
    							// If we only need a bit more vials, buy that amount
    							tempAmountToBuy -= Bank.vialsInBank;
    						}
    					}
                		
                		if(!GE.openBuyOffer(nextItems[i], buyPrice.get(i), tempAmountToBuy)) {
                			Util.log("run(): error buying: "+nextItems[i]);
                		}
                		
                	}
                	// If everything went good, wait and collect
                	if(status == STATUS.SUCCESS) {
                		stateOrder.addFirst(6);
                	}else {
                		stateOrder.addFirst(4);
                	}
                	
                }
            	
				break;


            ////////////////////////////////////////////////////////////////
            case 6:
            	if(!GE.waitThenCancel()) {
            		if(status == STATUS.GE_NOT_OPEN) {
            			stateOrder.addFirst(1);
            		}
            		stateOrder.addFirst(4);
            	}
            	break;
            	
			////////////////////////////////////////////////////////////////
			case 7:
				if(!GE.buyVialsOfWater(statusData)) {
					Util.log("run(): Unable to buy vials of water");
				}else {
					statusData = 0;
				}
			break;
            ////////////////////////////////////////////////////////////////	
			////////////////////////////////////////////////////////////////
			case 10: // open bank
				if(!Bank.openBank()) {
					Util.log("run(): Unable to open bank");
					status = STATUS.BANK_NOT_OPEN;
				}
				break;
			////////////////////////////////////////////////////////////////
			case 11: // close bank
				if(!Bank.closeBank()) {
					Util.log("run(): Unable to close bank");
				}
				break;
			////////////////////////////////////////////////////////////////
			case 12: // empty bank
				if(!Bank.emptyBank(false)) {
					Util.log("run(): Unable to empty bank");
					stateOrder = Util.addToStartOfArray(stateOrder,Arrays.asList(11,10,14,12));
				}
				break;
			////////////////////////////////////////////////////////////////
			case 13: // Count herbs
				Util.log("run(): Counting herbs/vials");
				Bank.countHerbs();
				Bank.countVials();
				break;
			////////////////////////////////////////////////////////////////
			case 14: // Deposit all
				Util.log("run(): Depositing all items");
				Bank.depositAll();
				break;
			////////////////////////////////////////////////////////////////
				
			case 15:
				boolean hasNoTask = true;
				// Loop 5 times to make sure no items are currently being transfered
				for(int i = 0; i < 2; i++) {
					// Search the bank for what items to process
					currentProcess = ItemProcessManager.searchBank();
					
					// Check if no items to make
					if(currentProcess != null) {
						hasNoTask = false;
						int amountOfActions = Bank.maxNumberOfInvens(currentProcess);
						
						if(amountOfActions < 5) {
							stateOrder.addAll(Arrays.asList(14,21,11,22,10));
							break;
						}
						
						for(int i2 = 0; i2 < amountOfActions; i2++) {
							stateOrder.addAll(Arrays.asList(14,21,11,22,10));
						}
						break;
					}else {
						Util.randomSleepRange(1000,2000);
					}
				}
				if(hasNoTask) {
					// Only if there are no processes to do after 5 checks
					stateOrder.addAll(Arrays.asList(10,14,12,13,11,1,3,4,5));
				}
				
				
				break;
				
			////////////////////////////////////////////////////////////////
			case 16:
				if(!Bank.leave1mInBank()) {
					stateOrder.clear();
					stateOrder.addAll(Arrays.asList(2,10,14,12,11,1,3,6,4,2,10,16,11,32));
				}
				break;
			////////////////////////////////////////////////////////////////
			case 17: // empty bank
				if(!Bank.emptyBank(false)) {
					Util.log("run(): Unable to empty bank");
					stateOrder = Util.addToStartOfArray(stateOrder,Arrays.asList(11,10,14,12));
				}
				break;
			////////////////////////////////////////////////////////////////
			case 18: // empty bank
				if(!Bank.grabCoins()) {
					Util.log("run(): Unable to withdraw coins");
					stateOrder.clear();
				}
			break;
			////////////////////////////////////////////////////////////////
							
			////////////////////////////////////////////////////////////////
			case 20: // Check bank for next process
				Util.log("run(): Looking for new process");
				currentProcess = ItemProcessManager.searchBank();
				if(currentProcess == null) {
					Util.log("run(): No item to process");
				}
				break;
			////////////////////////////////////////////////////////////////
			case 21: // Process item in bank
				if(currentProcess != null) {
					if(!currentProcess.inBank()) {
						Util.log("run(): Unable to process in bank");
					}else {
						Util.log("run(): process in bank done");
					}
				}else {
					Util.log("run(): NULL process");
				}
				break;
			////////////////////////////////////////////////////////////////
			case 22: // Process item in inventory
				
				if(Banking.isBankScreenOpen()) {
					stateOrder.addFirst(20);
					stateOrder.addFirst(11);
					break;
				}
				
				if(currentProcess != null) {
					if(!currentProcess.inInventory()) {
						Util.log("run(): Unable to process in inventory");
					}else {
						Util.log("run(): process in inventory done");
					}
				}else {
					Util.log("run(): NULL process");
				}
				break;
				
				
			////////////////////////////////////////////////////////////////
							
			////////////////////////////////////////////////////////////////
			case 30:
				Util.log("run(): Break Starting");
				Login.logout();
				isReadyToBreak = true;
				Util.randomSleepRange(10000, 20000);
				while(isBreaking) {
					Util.randomSleepRange(10000,20000);
				}
				isReadyToBreak = false;
				Util.log("run(): Break Ending");
				
				Util.log("run(): logging in");
				Login.login();
				
				break;
			
			
			
			////////////////////////////////////////////////////////////////
			case 31:
				
				if(!Login.login()) {
					if(Login.getLoginMessage() == Login.LOGIN_MESSAGE.BANNED) {
						isRunning = false;
						Util.log("ACCOUNT BANNED!");
						try {
							Network.announceCrash();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Network.updateMainTask("ACCOUNT BANNED!");
						Network.updateSubTask("Weath: +10xp");
					}
				}
				
				break;
			
			////////////////////////////////////////////////////////////////
			case 32:
				long waitUntil = Util.secondsLater(60*45);
				while(true) {
					Util.randomSleepRange(60000, 180000);
					Mouse.randomRightClick();
					Mouse.leaveGame(true);
					if(Util.time() > waitUntil) {
						break;
					}
				}
				
				Login.logout();
				isRunning = false;
				
				break;
			////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////
				
				
				
			case 100: // Open bank booth in lumby castle
				Util.log("run() state 100: Open bank booth in lumby castle");
				if(!Bank.openLumbyBank()) {
					stateOrder.clear();
					Util.log("run() state 100: Problem opening bank");
				}
				break;
				
			case 101: // Take out quest items
				Util.log("run() state 101: Take out quest items");
				Util.randomSleepRange(4000, 6000);
				if(!Bank.takeOutQuestItems()) {
					stateOrder.clear();
					Util.log("run() state 101: Problem taking out items");
				}
				break;
				
			case 102: // Wear equipment
				Util.log("run() state 102: Wear equipment");
				if(!Inven.wearQuestItems()) {
					stateOrder.clear();
					Util.log("run() state 102: Problem wearing items");
				}
				break;
				
			case 103: // Use items on cauldron
				Util.log("run() state 103: Use items on cauldron");
				if(!NPCTalk.useItemsOnCauldron()) {
					stateOrder.clear();
					Util.log("run() state 103: Problem using items on cauldron");
				}
				break;
				
				
				
				
			case 110: // Talk to Kaqemeex (Start quest)
				Util.log("run() state 110: Talk to Kaqemeex (Start quest)");
				if(!NPCTalk.kaqemeex1()) {
					stateOrder.clear();
					Util.log("run() state 110: Problem talking to kaqemeex");
				}
				break;
				
			case 111: // Talk to Sanfew (1st time)
				Util.log("run() state 111: Talk to Sanfew (1st time)");
				if(!NPCTalk.sanfew1()) {
					stateOrder.clear();
					Util.log("run() state 111: Problem talking to Sanfew");
				}
				break;
				
			case 112: // Talk to Sanfew (2nd time)
				Util.log("run() state 112: Talk to Sanfew (2nd time)");
				if(!NPCTalk.sanfew2()) {
					stateOrder.clear();
					Util.log("run() state 112: Problem talking to Sanfew");
				}
				break;
				
			case 113: // Talk to Kaqemeex (End Quest)
				Util.log("run() state 113: Talk to Kaqemeex (End Quest)");
				if(!NPCTalk.kaqemeex2()) {
					stateOrder.clear();
					Util.log("run() state 110: Problem talking to kaqemeex");
				}
				break;
				
				
				
				
			case 120: // Walk to Kaqemeex
				Util.log("run() state 120: Walk to Kaqemeex");
				if(!Walk.walkToRandom(2923, 3487, 2928, 3485, 0)) {
					stateOrder.clear();
					Util.log("run() state 120: Failed to walk to Kaqemeex");
				}
				break;
				
			case 121: // Walk to Sanfew
				Util.log("run() state 121: Walk to Sanfew");
				if(!Walk.walkToPosition(2897, 3428, 0)) {
					stateOrder.clear();
					Util.log("run() state 121: unable to walk to Sanfew");
				}else {
					if(!Walk.climbUpStairs()) {
						stateOrder.clear();
						Util.log("run() state 121: unable to climb up stairs");
					}
					Util.randomSleep();
				}
				break;
				
			case 122: // Walk to blue dragon dungeon
				Util.log("run() state 122: Walk to blue dragon dungeon");
				if(!Walk.walkToPosition(2884, 3398, 0)) {
					stateOrder.clear();
					Util.log("run() state 122: unable to walk to blue dragon dungeon");
				}else {
					if(!Walk.climbDownLadder()) {
						stateOrder.clear();
						Util.log("run() state 122: unable to climb down ladder");
					}
					Util.randomSleep();
				}
				break;
				
			case 123: // Walk to gate
				Util.log("run() state 123: Walk to gate");
				if(!Walk.walkToPosition(2888, 9830, 0)) {
					Util.log("run() state 123: Unable to walk to gate");
					stateOrder.clear();
				}else {
					if(!Walk.openPrisonGate()) {
						Util.log("run() state 123: Failed to enter gate");
						stateOrder.clear();
					}
				}
				break;
				
				
			case 125: // Leave Sanfew House
				Util.log("run() state 125: Leave Sanfew House");
				if(!Walk.climbDownStairs()) {
					Util.log("run() state 125: Unable to climb down stairs");
					stateOrder.clear();
				}
				break;
				
				
				
				
			case 130: // Teleport to GE
				Util.log("run() state 130:  Teleport to GE");
				if(!Teleport.grandExchange()) {
					Util.log("run() state 130: Failed teleporting");
					stateOrder.clear();
				}
				break;
				
			case 131: // Teleport to burthorpe
				Util.log("run() state 131: Teleport to burthorpe");
				if(!Teleport.burthorpe()) {
					Util.log("run() state 131: Failed teleporting");
					stateOrder.clear();
				}
				break;
				
				
			////////////////////////////////////////////////////////////////
			case 200:
				Util.log("run() state 200: Walk to Rogues Den");
				if(!Walk.walkToPosition(2906, 3537, 0)) {
					stateOrder.clear();
					Util.log("run() state 200: Failed to walk to Rogues Den");
				}else {
					if(!Walk.enterTrapdoor()) {
						stateOrder.clear();
						Util.log("run() state 200: Unable to enter trapdoor");
					}else {
						if(!Walk.walkToPosition(3043, 4972, 1)) {
							stateOrder.clear();
							Util.log("run() state 200: Failed to walk to Rogues Den fire");
						}
					}
				}
				break;
				
			case 201:
				// open bank in rogues
				if(!Bank.openRoguesDenBank()) {
					stateOrder.clear();
					Util.log("run() state 201: Failed to Open rogues den bank");
				}
				break;
				
			case 202:
				// use bank in rogues
				if(!Bank.takeOutCookingTrainingFish()) {
					stateOrder.clear();
					Util.log("run() state 202: Failed to Open rogues den bank");
				}
				break;
				
			case 203:
				// cook in rogues
				if(!Cooking.cookFishOnFire()) {
					stateOrder.clear();
					Util.log("run() state 202: Failed to Open rogues den bank");
				}else {
					stateOrder.addAll(Arrays.asList(201,14,202,11,203));
				}
				break;
				
			case 210:
				break;
				
			case 211:
				break;
				
			case 212:
				break;
				
			case 213:
				break;
				
			case 270:
				Util.log("run(): Buying fish for cooking");
                
                // Check if there are coins in the inventory
                if(Inven.hasCoins()) {
                	totalCoins = Inven.countCoins();
                	
                	if(totalCoins < MIN_COINS) {
                		Util.log("run(): Not enough coins in inventory");
                		
                		stateOrder = Util.addToStartOfArray(stateOrder, Arrays.asList(2,10,17,11,1,3,6,10,14,18,270));
                		break;
                	}
                }else {
                	stateOrder = Util.addToStartOfArray(stateOrder, Arrays.asList(2,10,14,18,270));
                	break;
                }
                
                Util.log("run(): Attempting to buy raw anchovies");
                if(!GE.openBuyOffer("Raw anchovies", 0, 300)) {
                	Util.log("run(): Unable to buy raw anchovies");
                	stateOrder.clear();
                	break;
                }
                
                Util.log("run(): Attempting to buy raw trout");
                if(!GE.openBuyOffer("Raw trout", 0, 400)) {
                	Util.log("run(): Unable to buy raw trout");
                	stateOrder.clear();
                	break;
                }
                
                Util.log("run(): Attempting to buy raw tuna");
                if(!GE.openBuyOffer("Raw tuna", 0, 8000)) {
                	Util.log("run(): Unable to buy raw tuna");
                	stateOrder.clear();
                	break;
                }
                
				break;
							
							
							
							
							
							
							
			////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////
							
							
							
							
							
							
							
			case 900:
				if(Game.getRoofsEnabledStatus() == Game.RoofStatus.BEING_DRAWN) {
					if(!Options.setRemoveRoofsEnabled(true)) {
						Util.log("run(): Unable to change remove roof settings");
					}else {
						Util.log("run(): Roofs removed");
					}
					
					GameTab.open(GameTab.TABS.INVENTORY);
				}
				break;
							
			////////////////////////////////////////////////////////////////
				
			case 1000:
				currentObjective = 1;
				break;
			case 1001:
				currentObjective = 2;
				break;
			case 1002:
				currentObjective = 3;
				break;
			case 1003:
				currentObjective = 4;
				break;
				
				
			////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////
			}
			// Handle errors
			switch(status) {
			case SUCCESS:
			case NONE:
				break;
				
			case GE_NOT_OPEN:
				Util.log("STATUS: Opening GE then retrying");
				stateOrder.addFirst(currentState);
				stateOrder.addFirst(1);
				break;
				
			case SQUARE_NOT_FOUND:
			case NPC_NOT_FOUND:
				isRunning = false;
				Util.log("STATUS: Placement error! Stopping bot");
				Login.logout();
				break;
				
			case SELL_INVENTORY_ERROR:
				Util.log("STATUS: Couldn't sell inventory, retrying!");
				stateOrder = Util.addToStartOfArray(stateOrder, Arrays.asList(2,10,14,12,13,11,1,3));
				break;
			
			case BUYING_1GP_ERROR:
				Util.log("STATUS: Trying to buy item for 1gp, retrying!");
				stateOrder = Util.addToStartOfArray(stateOrder,Arrays.asList(2,10,14,12,13,11,1,3));
				break;
				
			case BUYING_OVER_PRICE_ERROR:
				Util.log("STATUS: Trying to buy item for over x2 the guide price");
				stateOrder = Util.addToStartOfArray(stateOrder,Arrays.asList(2,10,14,12,13,11,1,4,3,5));
				break;
				
			case MISSING_VIALS_OF_WATER:
				Util.log("STATUS: Has herbs, missing vials of water");
				Util.log("STATUS: Buying # " + statusData);
				stateOrder.clear();
				stateOrder.addAll(Arrays.asList(10,14,18,11,1,7,2,10,14,15));
				break;
				
			case BANK_NOT_OPEN:
				boolean hasOpened = false;
				for(int i = 0; i < 4;i++) {
					if(Bank.openBank()) {
						hasOpened = true;
						break;
					}
				}
				if(!hasOpened) {
					stateOrder.clear();
					Network.updateMainTask("Unable to open bank");
					Login.logout();
				}
				break;
				
			case NO_TRAINING_FISH:
				//TODO;
				//FIXME
				// This status will happen when no fish left to level on
				break;
				
			case FAILED_CLOSING:
			case NEW_OFFER_ERROR:
			case NO_FREE_OFFER:
			case NO_INVENTORY_ITEM:
			case ITEM_IN_GE:
			case GENERAL_FAIL:
			case TOOK_TOO_LONG:
				break;
				
			default:
				break;
			}
			
			resetStatus();
			
		}
	}


	@Override
	public void onBreakEnd() {
		Util.log("Break End");
		isBreaking = false;
		try {
			Network.announceBreakEnd();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onBreakStart(long arg0) {
		Util.log("Break Start");
	}


	@Override
	public void onPreBreakStart(long arg0) {
		stateOrder.addFirst(30);
		// Wait for when we can break
		while(!isReadyToBreak) {
			Util.randomSleepRange(100, 200);
		}
		
		try {
			Network.announceBreakStart();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// We are now breaking
		isBreaking = true;
		
	}


	@Override
	public void onEnd() {
		try {
			Util.forceLog();
			Network.announceCrash();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("deprecation")
    public static void suspendAntiban() {
        for (Thread thread : Thread.getAllStackTraces().keySet())
            if (thread.getName().contains("Antiban") || thread.getName().contains("Fatigue"))
                thread.suspend();
        
        Util.log("Anti-ban Suspended");
    }
	
	@Override    
	public void tradeRequestReceived(String s) {
		Util.log("Trade from: " + s);
        if(s.equalsIgnoreCase(Trade.getMuleName())){
        	isTradeTime = true;
        }
    }
	
	public static void setStatus(STATUS newStatus) {
		status = newStatus;
	}
	
	public static void setStatus(STATUS newStatus, int newStatusData) {
		status = newStatus;
		statusData = newStatusData;
	}
	
	private void resetStatus() {
		status = STATUS.NONE;
	}
	
	public static String getStateString() {
		switch(currentState) {
		case 1:
			return "Opening GE";
		case 2:
			return "Closing GE";
		case 3:
			return "Selling Inventory";
		case 4:
			return "Canceling and Collecting GE";
		case 5:
			return "Buying next items";
		case 6:
			return "Waiting for items to buy/sell";
		case 7:
			return "Buying Vials of water";
		case 10:
			return "Opening Bank";
		case 11:
			return "Closing Bank";
		case 12:
			return "Emptying Bank";
		case 13:
			return "Counting herbs/vials";
		case 14:
			return "Depositing all items";
		case 15:
			return "Checking for next process";
		case 16:
			return "Leaving 1m in the bank";
		case 17:
			return "Emptying bank (everything)";
		case 18:
			return "Taking out only cois";
		case 20:
			return "DEAD STATE";
		case 21:
			return "Processing in Bank";
		case 22:
			return "Processing in Inventory";
		case 30:
			return "Breaking...";
		case 31:
			return "Login...";
		case 32:
			return "Doing nothing while staying logged in";
		case 100: 
			return "Open bank booth in lumby castle";
		case 101:
			return "Take out quest items";
		case 102:
			return "Wear quest equipment";
		case 103:
			return "Use items on cauldron";
		case 110:
			return "Talk to Kaqemeex (Start quest)";
		case 111:
			return "Talk to Sanfew (1st time)";
		case 112:
			return "Talk to Sanfew (2nd time)";
		case 113:
			return "Talk to Kaqemeex (End Quest)";
		case 120:
			return "Walk to Kaqemeex";
		case 121:
			return "Walk to Sanfew";
		case 122:
			return "Walk to blue dragon dungeon";
		case 123:
			return "Walk to gate";
		case 124:
			return "Enter Gate";
		case 125:
			return "Leave Sanfew House";
		case 130:
			return "Teleport to GE";
		case 131:
			return "Teleport to burthorpe";
			
		default:
			return "INVALID STATE";
		}
	}


	public static String getCurrentObjectiveString() {
		switch(currentObjective) {
		case 1:
			return "Making Potions";
		case 2:
			return "Druidic Ritual";
		case 3:
			return "Leveling Cooking";
		case 4:
			return "Cooking for GP";
		default:
			return "unknown";
		}
	}
	
	public static int getCurrentObjectiveInt() {
		return currentObjective;
	}
}










