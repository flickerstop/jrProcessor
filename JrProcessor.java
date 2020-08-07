package scripts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Pausing;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;
import scripts.util.Bank;
import scripts.util.GE;
import scripts.util.Inven;
import scripts.util.Network;
import scripts.util.Trade;
import scripts.util.Util;


@ScriptManifest(authors = { "JR" }, category = "Tools", name = "jrProcessor")
public class JrProcessor extends Script implements Starting, Breaking, PreBreaking, Ending, MessageListening07 {
	
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
	
	private static STATUS status = STATUS.NONE;
	private static int currentState = 0;
	
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
	    BUYING_1GP_ERROR
	  }
	
	@Override
	public void onStart() {
		this.setLoginBotState(false);
	}
 
	
	@Override
	public void run() {
		//Util.log("run(): ");
		Util.clearConsole();
		
		Util.log("run(): suspending antiban");
		suspendAntiban();
		
		if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Util.log("run(): logging in");
			Login.login();
		}
		Util.log("run(): network init");
		Network.init("jrProcessor");
		
		
		// Starting order
		stateOrder.addAll(Arrays.asList(11,2,1,4,2,10,14));
		
		
		
					
		while(isRunning) {
			//Network.updateSubTask("aaaaaaaaaaaaaa");
			Network.updateSubTask("Getting new state");
			
			// Output the next states
//			String allStates = "";
//			for(int state : stateOrder) {
//				allStates += state+"->";
//			}
//			Util.log(allStates);
			
			// Check if we need a new state path
			if(stateOrder.size() == 0) {
				stateOrder.addAll(Arrays.asList(2,10,14,15));
			}

			
			// Get the next state
			currentState = stateOrder.removeFirst();
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
					// If the issue was there was no items in the inventory
					if(status == STATUS.NO_INVENTORY_ITEM) {
						Network.updateSubTask("No items in inventory");
						// But there are more than 500k
						if(Inven.countCoins() > 500000) {
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
			////////////////////////////////////////////////////////////////
			case 10: // open bank
				if(!Bank.openBank()) {
					Util.log("run(): Unable to open bank");
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
				if(!Bank.emptyBank()) {
					Util.log("run(): Unable to empty bank");
					stateOrder.addAll(Arrays.asList(11,10,12));
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
				for(int i = 0; i < 3; i++) {
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
							
			////////////////////////////////////////////////////////////////
			case 20: // Check bank for next process
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
				stateOrder.addAll(Arrays.asList(2,10,14,12,13,11,1,3));
				break;
			
			case BUYING_1GP_ERROR:
				Util.log("STATUS: Trying to buy item for 1gp, retrying!");
				stateOrder.addAll(Arrays.asList(2,10,14,12,13,11,1,3));
				break;
				
			case FAILED_CLOSING:
			case NEW_OFFER_ERROR:
			case NO_FREE_OFFER:
			case NO_INVENTORY_ITEM:
			case ITEM_IN_GE:
			case GENERAL_FAIL:
			case TOOK_TOO_LONG:
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
			
			
		case 20:
			return "DEAD STATE";
		case 21:
			return "Processing in Bank";
		case 22:
			return "Processing in Inventory";
			
			
		case 30:
			return "Breaking...";
			
		default:
			return "INVALID STATE";
		}
	}


}










