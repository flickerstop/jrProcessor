package scripts;
import java.util.ArrayList;
import java.util.Date;

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
	
	private boolean isReadyToBreak = false;
	private boolean isBreaking = false;
	
	private boolean isTradeTime = false;
	
	private int totalCashStack = 0;
	private int totalBought = 0;
	
	@Override
	public void onStart() {
//		Banking.openBank();
//		Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_X);
//		Banking.depositAll();
		
		//TODO THIS IS NEW, if anything fucks it blame this
		this.setLoginBotState(false);
	}
 
	
	@Override
	public void run() {
		Util.clearConsole();
		suspendAntiban();
		
		if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Login.login();
		}
		
		Network.init();
		
		// Check the ge 
		GE.openGE();
		GE.collectAndCancel();
		
		
		while(true) {
			while(true) {
				// we can break now
				isReadyToBreak = true;
				Util.randomSleepRange(1000,2000);
				while(isBreaking) {
					Util.randomSleepRange(10000,20000);
				}
				isReadyToBreak = false;
				
				
				// If the mule is nearby
				while(Trade.isMuleNearby()) {
					Util.log("Mule is Nearby!");
					try {
						Network.announceWaitingForMule();
					} catch (Exception e) {
						Util.log("Could not announce waiting for mule");
						e.printStackTrace();
					}
					GE.closeGE();
					
					// Take out the plat from the bank
					Network.updateBotSubTask("Taking out plat");
					Trade.getReadyForTrade();
					Util.randomSleepRange(2000, 3000);
					Banking.close();
					
					// Walk to the trade spot
					Util.randomSleepRange(2000,4000);
					Network.updateBotSubTask("Walking to spot");
					Util.walkBotToTrade();
					
					long waitStartTime = new Date().getTime();
					// Wait until mule trades the bot
					while(!isTradeTime && Trade.isMuleNearby()) {
						Network.updateBotSubTask("Waiting for mule to trade");
						Util.randomSleepRange(4000,7000);
						Util.walkBotToTrade();
						
						if((new Date().getTime()) >= (waitStartTime  + 20000L)) {
							waitStartTime = new Date().getTime();
							Trade.tradeMule();
							Util.randomSleepRange(4000,7000);
							if(Trade.isTradeOpen()) {
								isTradeTime = true;
							}
						}
					}
					
					// Trade the plat over
					Network.updateBotSubTask("Trading Mule");
					Trade.tradeItems();
					
					// Done trading
					Util.randomSleepRange(2000, 3000);
					isTradeTime = false;
					Network.updateBotSubTask("Waiting 30 seconds after trade");
					Util.randomSleepRange(20000, 45000);
				}
				
				
				// Open the bank
				Bank.openBank();
	
				Banking.depositAll();
				
				// Deposit everything in the bank
				Util.randomSleepRange(2000, 3000);
				
				// Search the bank for what items to process
				ProcessingObject process = ItemProcessManager.searchBank();
				
				
				
				
				// Check if no items to make
				if(process == null) {
					Util.randomSleepRange(1000,2000);
					break;
				}
				
				// Grab the data for the API
				try {
					Network.updateAPI(process,totalCashStack,totalBought);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				Util.log("Creating:"+process.result);
				
				
				// Grab the items from the bank
				process.inBank();
				
				
				Banking.close();
				Util.randomSleep();
				
				if(Banking.isBankScreenOpen()) {
					Banking.close();
				}
				
				process.inInventory();
				
			}
			
			// we can break now
			isReadyToBreak = true;
			Util.randomSleepRange(1000,2000);
			while(isBreaking) {
				Util.randomSleepRange(10000,20000);
			}
			isReadyToBreak = false;
			
			
			Bank.openBank();
			Util.randomSleepRange(1000,2000);
			
			try {
				Network.announceGE();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			Util.log("Counting left over herbs");
			int extraHerbs = Bank.countHerbs();
			Util.log("Amount left over: "+extraHerbs);
			
			Util.log("Counting left over vials");
			int extraVials = Bank.countVials();
			Util.log("Amount left over: "+extraVials);
			
			Network.updateBotSubTask("Emptying Bank");
			
			// Time to Empty bank and sell
			Util.log("Emptying Bank");
			Bank.emptyBank();
			Util.randomSleep();
			
			Util.log("Closing Bank");
			Banking.close();
			Util.randomSleep();
			
			Util.log("Opening GE");
			GE.openGE();
			
			Network.updateBotSubTask("Selling Inventory");
			Util.log("Selling Inventory");
			GE.sellInventory();
			
			// Buy the items needed
			String itemsToBuy[] = Network.getNextItem();
			ArrayList<Integer> buyPrice = new ArrayList<Integer>();
			
			int totalCoins = Inventory.find("Coins")[0].getStack();
			totalCashStack = totalCoins;
			
			
			
			// Check if we need to deposit some money
			if(totalCoins > Bank.MAX_GP_ALLOWED+500000) {
				Network.updateBotSubTask("Converting GP to Plat");
				Util.log("Converting some coins over to plat");
				Bank.convertToPlatTokens();
				Util.randomSleepRange(2000, 4000);
				GE.openGE();
			}
			
			
			// if we have no coins
			if(!Inven.hasCoins() || Trade.isMuleNearby()) {
				continue;
			}
			
			
			int totalPrice = 0;
			Util.log("\nChecking Prices of items to buy...");
			try {
				for(int i = 0; i <= itemsToBuy.length-1; i++) {
					Util.log("Item # "+i+": "+itemsToBuy[i]);
				}
				
				
				// Get the price to buy the items at
				for(int i = 0; i <= itemsToBuy.length-1; i++) {
					
					// Skip null items
					if(itemsToBuy[i].equalsIgnoreCase("null")) {
						continue;
					}
					
					// Manually set prices for items
					if(itemsToBuy[i].equalsIgnoreCase("Vial of water")) {
						buyPrice.add(15);
						totalPrice += 15;
						continue;
					}else if(itemsToBuy[i].equalsIgnoreCase("Maple logs")) {
						buyPrice.add(25);
						totalPrice += 25;
						continue;
					}
					
					// if we have no coins
					if(!Inven.hasCoins() || Trade.isMuleNearby()) {
						continue;
					}
					
					
					Network.updateBotSubTask("PC: "+itemsToBuy[i]);
					Util.log("Checking High price of: " + itemsToBuy[i]);
					int itemSellPrice = GE.checkSellPrice(itemsToBuy[i]);
					Util.log(itemsToBuy[i] + " High price is: " + itemSellPrice);
					// If under 100gp, dont bother trying to min/max the price
					if(itemSellPrice > 100) {
						Util.randomSleepRange(2000, 4000);
						Util.log("Checking Low price of: " + itemsToBuy[i]);
						int itemBuyPrice = GE.checkBuyPrice(Inventory.find(itemsToBuy[i])[0]);
						Util.log(itemsToBuy[i] + " Low price is: " + itemSellPrice);
						
						int itemPrice = (itemSellPrice + itemBuyPrice)/2;
						
						// if the difference between buy/sell is only like 15 coins, insta buy
						if(Math.abs(itemSellPrice-itemBuyPrice) <= 15) {
							itemPrice = itemSellPrice+10;
							totalPrice += itemPrice;
							buyPrice.add(itemPrice);
						}else {
							totalPrice += itemPrice;
							buyPrice.add(itemPrice);
						}
						
						
					}else {
						Util.log(itemsToBuy[i] + " is very cheap, we'll insta buy it.");
						int itemPrice = itemSellPrice+10;
						totalPrice += itemPrice;
						buyPrice.add(itemPrice);
					}
					
					
					Util.randomSleep();
				}
				
				// Calculate how many to buy
				int amountToBuy = (totalCoins - 30000)/totalPrice;
				
				// cap at 10k
				if(amountToBuy > 1000) {
					amountToBuy = 1000;
				}
				
				totalBought = amountToBuy;
				Util.log("\nBuying Items...");
				// Buy the items
				for(int i = 0; i <= itemsToBuy.length-1; i++) {
					// Skip null items
					if(itemsToBuy[i].equalsIgnoreCase("null")) {
						continue;
					}
					
					// if we have no coins
					if(!Inven.hasCoins() || Trade.isMuleNearby()) {
						continue;
					}
					
					int tempAmountToBuy = amountToBuy;
					Util.log("\n\nBuying: " + itemsToBuy[i]);
					Util.log("Price: " + buyPrice.get(i));
					
					Network.updateBotSubTask("Buying: " + itemsToBuy[i]);
					// If we're buying vials of water, add the amount of left over herbs
					if(itemsToBuy[i].equalsIgnoreCase("Vial of water")) {
						tempAmountToBuy += extraHerbs;
						
						// If we already have this many vials in the bank
						if((tempAmountToBuy-extraVials) <= 0) {
							Util.log("Skipped buying vials, enough in bank");
							continue;
						}else {
							// If we only need a bit more vials, buy that amount
							tempAmountToBuy -= extraVials;
						}
					}
					
					Util.log("Amount: " + tempAmountToBuy);
					
					GE.openBuyOffer(itemsToBuy[i], buyPrice.get(i), tempAmountToBuy);
				}
			}catch(Exception e) {
				Util.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Util.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Util.log("Something happened when buying");
				e.printStackTrace();
				Util.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Util.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			
			Network.updateBotSubTask("");
			
			Util.log("All GE interaction done.");
			
			GE.waitThenCancel();
			
			GE.closeGE();
				
			Bank.openBank();
			Banking.depositAll();
			Util.randomSleepRange(2000, 3000);
		}
	}


	@Override
	public void onBreakEnd() {
		Util.log("Break End");
		isBreaking = false;
	}


	@Override
	public void onBreakStart(long arg0) {
		Util.log("Break Start");
	}


	@Override
	public void onPreBreakStart(long arg0) {
		// Wait for when we can break
		while(!isReadyToBreak) {
			Util.randomSleepRange(100, 200);
		}
		
		try {
			Network.announceBreak();
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


}










