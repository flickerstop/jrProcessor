package scripts.util;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Players;
import org.tribot.api2007.Trading;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSPlayer;

public class Trade {
	//final static String MULE_NAME = "Zander Caius";
	final static String MULE_NAME = "MyBonk";
	
	
	public static String getMuleName() {
		return MULE_NAME;
	}
	
	public static boolean isMuleNearby() {
		if(Players.find(MULE_NAME).length == 0) {
			return false;
		}
		return true;
	}
	
	public static void getReadyForTrade() {
		Util.log("Opening Bank for trade");
		Bank.openBank();
		Util.randomSleep();
		
		
		
		
		
		while(Banking.find("Platinum token").length != 0) {
			Banking.depositAll();
			Util.randomSleep();
			// Take out plat tokens
			Util.log("Taking out plat tokens");
			Banking.withdraw(0, "Platinum token");
			Util.randomSleep();
		}
		
		
		Banking.close();
		Util.randomSleep();
		Util.log("Closing bank for trade");
		
	}
	
	public static void tradeItems() {
		// Loop till we trade
		while(true) {
			
			if(Trade.isTradeOpen()) {
				break;
			}
			
			// Find the mule
			if(Players.find(MULE_NAME).length == 0) {
				Util.log("Mule not found!");
				return;
			}
			
			RSPlayer mule = Players.find(MULE_NAME)[0];
			// Trade the mule
			mule.click("Trade with "+MULE_NAME);
			Util.randomSleepRange(2000, 4000);
			
		}
		
		
		
		//Make sure we're trading the correct person
		if(!Trading.getOpponentName().equalsIgnoreCase(MULE_NAME)) {
			Util.log("NOT TRADING MULE!");
			Trading.close();
			return;
		}
		
		
		while(Inventory.find("Platinum token").length != 0){
			Trading.offer(0, "Platinum token");
			Util.randomSleepRange(4000, 7000);
		}
		
		
		// Loop till accept
		while(true) {
			// Make sure we're still trading
			if(Trading.getWindowState() != Trading.WINDOW_STATE.FIRST_WINDOW && Trading.getWindowState() != Trading.WINDOW_STATE.SECOND_WINDOW) {
				break;
			}
			
			
			if(Trading.hasAccepted(false) != true) {
				Trading.accept();
			}
			Util.randomSleep();
		}
		
		Util.log("Trading done!");
		
	}
	
	public static boolean tradeTarget(String[] muleTarget){
		Util.log("Hopping worlds...");
		// Hop to that world
		if(!WorldHopper.changeWorld(Integer.parseInt(muleTarget[1]))) {
			Util.log("Failed to hop worlds");
			return false;
		}
		Util.randomSleepRange(2000, 4000);
		
		Util.log("Logging in...");
		// log in
		if(!Login.login()) {
			Util.log("Failed to login");
			return false;
		}
		Util.randomSleep();
		Camera.setCamera(0, 100);
		Util.randomSleep();
		
		// look for the player
		Util.log("Trying to find player: "+muleTarget[0]);
		for(int i = 0; i< 15; i++) {
			if(Players.find(muleTarget[0]).length == 0) {
				Util.log("Player not found! Attempt # "+i);
				Util.randomSleepRange(2000, 6000);
			}else {
				break;
			}
		}
		// No player found
		if(Players.find(muleTarget[0]).length == 0) {
			Util.log("Player not found! Quitting...");
			return false;
		}
		
		// Walk to the spot to trade
		Util.walkMuleToTrade();
		Util.randomSleepRange(2000, 4000);
		
		// Target the palyer
		RSPlayer target = Players.find(muleTarget[0])[0];
		boolean isTrading = false;
		// Keep trading the player till we get a trade window
		while(!isTrading) {
			Util.log("Attempting to trade: "+muleTarget[0]);
			Interfaces.closeAll();

			// If the player isn't in the correct spot, wait
			if(target.getPosition().getY() != 3489) {
				Util.log("Player not standing in correct spot");
				Util.randomSleepRange(3000, 5000);
				continue;
			}
			
			// Trade the player
			target.click("Trade with "+muleTarget[0]);
			
			
			for(int i = 0; i < 15; i++) {
				Util.log("waiting for trade window...");
				Util.randomSleepRange(1000, 3000);
				if(Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW || Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
					if(!Trading.getOpponentName().equalsIgnoreCase(muleTarget[0])) {
						Util.log("Trading wrong person!");
						Trading.close();
						Util.randomSleep();
						continue;
					}
					isTrading = true;
					break;
				}
			}
			
			
			
			
			
		}
		Util.randomSleep();
		
		// Loop till accept
		while(true) {
			// Make sure we're still trading
			if(Trading.getWindowState() != Trading.WINDOW_STATE.FIRST_WINDOW && Trading.getWindowState() != Trading.WINDOW_STATE.SECOND_WINDOW) {
				break;
			}
			
			
			if(Trading.hasAccepted(true) == true) {
				Trading.accept();
			}
			Util.randomSleep();
		}
		
		
		return true;
	}
	
	public static void tradeMule() {
		// look for the player
		Util.log("Trying to find mule: "+MULE_NAME);
		Network.updateBotSubTask("Trying to find mule: "+MULE_NAME);
		for(int i = 0; i< 15; i++) {
			if(Players.find(MULE_NAME).length == 0) {
				Util.log("Player not found! Attempt # "+i);
				Util.randomSleepRange(2000, 6000);
			}else {
				break;
			}
		}
		// No player found
		if(Players.find(MULE_NAME).length == 0) {
			Util.log("Player not found! Quitting...");
			return;
		}
		
		Util.randomSleepRange(2000, 4000);
		
		// Target the player
		RSPlayer target = Players.find(MULE_NAME)[0];
		
		Util.log("Attempting to trade: "+MULE_NAME);
		Interfaces.closeAll();

		Util.randomSleepRange(2000, 4000);
		// Trade the player
		Network.updateBotSubTask("Trading: "+MULE_NAME);
		target.click("Trade with "+MULE_NAME);
			
			
		Util.randomSleep();
	}
	
	public static boolean isTradeOpen() {
		if(Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW || Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
			return true;
		}
		return false;
	}
}
