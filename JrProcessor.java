package scripts;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;
import scripts.util.Util;


@ScriptManifest(authors = { "JR" }, category = "Tools", name = "Jr Test")
public class JrProcessor extends Script implements Starting, Breaking, PreBreaking {
	
	private boolean isReadyToBreak = false;
	private boolean isBreaking = false;
	
	@Override
	public void onStart() {
//		Banking.openBank();
//		Banking.setWithdrawQuantity(Banking.WITHDRAW_QUANTITY.WITHDRAW_X);
//		Banking.depositAll();
		Util.clearConsole();
		
	}
 
	
	@Override
	public void run() {
		if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Login.login();
		}
		
		while(true) {
			
			// we can break now
			isReadyToBreak = true;
			Util.randomSleepRange(1000,2000);
			while(isBreaking) {
				Util.randomSleepRange(10000,20000);
			}
			isReadyToBreak = false;
			
			
			// Open the bank
			Bank.openBank();

			Banking.depositAll();
			
			// Deposit everything in the bank
			Util.randomSleep();
			
			// Search the bank for what items to process
			ProcessingObject process = ItemProcessManager.searchBank();
			
			// Check if no items to make
			if(process == null) {
				Util.randomSleepRange(1000,2000);
				break;
			}
			
			Util.log("Creating:"+process.result);
			
			// Grab the items from the bank
			process.inBank();
			
			
			Banking.close();
			Util.randomSleep();
			// Loop till bank is closed
			while(true) {
				if(Banking.isBankScreenOpen()) {
					Util.randomSleep();
				}else {
					break;
				}
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
		
		// Time to Empty bank and sell
		Util.log("Emptying Bank");
		Bank.emptyBank();
		Util.randomSleep();
		
		Util.log("Grabbing coins");
		Bank.grabCoins();
		Util.randomSleep();
		
		Util.log("Closing Bank");
		Banking.close();
		Util.randomSleep();
		
		Util.log("Opening GE");
		GE.openGE();
		
		Util.log("Selling Inventory");
		GE.sellInventory();
		
		// Buy the items needed
		String itemsToBuy[] = {"Grimy ranarr weed","Vial of water"};
		ArrayList<Integer> buyPrice = new ArrayList<Integer>();
		
		int totalCoins = Inventory.find("Coins")[0].getStack();
		int totalPrice = 0;
		
		Util.log("\nChecking Prices of items to buy...");
		// Get the price to buy the items at
		for(int i = 0; i <= itemsToBuy.length-1; i++) {
			Util.log("Checking price of: " + itemsToBuy[i]);
			int itemSellPrice = GE.checkSellPrice(itemsToBuy[i]);
			
			// If under 100gp, dont bother trying to min/max the price
			if(itemSellPrice > 100) {
				int itemBuyPrice = GE.checkBuyPrice(Inventory.find(itemsToBuy[i])[0]);
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
				int itemPrice = itemSellPrice+10;
				totalPrice += itemPrice;
				buyPrice.add(itemPrice);
			}
			
			
			Util.randomSleep();
		}
		
		// Calculate how many to buy
		int amountToBuy = (totalCoins - 30000)/totalPrice;
		Util.log("\nBuying Items...");
		// Buy the items
		for(int i = 0; i <= itemsToBuy.length-1; i++) {
			Util.log("\n\nBuying: " + itemsToBuy[0]);
			Util.log("Price: " + buyPrice.get(i));
			Util.log("Amount: " + amountToBuy);
			GE.openBuyOffer(itemsToBuy[i], buyPrice.get(i), amountToBuy);
		}
		
		GE.waitThenCancel();
		
		GE.closeGE();
			
		Bank.openBank();
		Banking.depositAll();
		Util.randomSleepRange(2000, 3000);
		
		run();
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
		// We are now breaking
		isBreaking = true;
		
	}
	
}










