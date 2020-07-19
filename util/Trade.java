package scripts.util;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
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
		
		Banking.depositAll();
		Util.randomSleep();
		
		Util.log("Taking out plat tokens");
		// Take out plat tokens
		Banking.withdraw(0, "Platinum token");
		Util.randomSleep();
		
		Banking.close();
		Util.randomSleep();
		Util.log("Closing bank for trade");
		
	}
	
	public static void tradeItems() {
		// Loop till we trade
		while(true) {
			
			// Find the mule
			if(Players.find(MULE_NAME).length == 0) {
				Util.log("Mule not found!");
				return;
			}
			
			RSPlayer mule = Players.find(MULE_NAME)[0];
			// Trade the mule
			mule.click("Trade with "+MULE_NAME);
			Util.randomSleepRange(2000, 4000);
			
			if(Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW || Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
				break;
			}
		}
		
		
		
		//Make sure we're trading the correct person
		if(!Trading.getOpponentName().equalsIgnoreCase(MULE_NAME)) {
			Util.log("NOT TRADING MULE!");
			Trading.close();
			return;
		}
		
		Trading.offer(0, "Platinum token");
		Util.randomSleep();
		
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
		Util.randomSleepRange(2000, 4000);;
		
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
		if(Players.find(muleTarget[0]).length == 0) {
			Util.log("Player not found! Quitting...");
			return false;
		}
		
		RSPlayer target = Players.find(muleTarget[0])[0];
		
		boolean isTrading = false;
		// Keep trading the player till we get a trade window
		while(!isTrading) {
			Util.log("Attempting to trade: "+muleTarget[0]);
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
}
