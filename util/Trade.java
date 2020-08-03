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
	
	public static boolean botTradeMule() {
		//Util.log("botTradeMule(): ");
		
		// Loop till we trade
		
		Util.log("botTradeMule(): Looking for mule to trade");
		
		// Try to trade the mule for 60 seconds
		long waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
			// If the trade window is open
			if(isTradeOpen()) {
				break;
			}
			
		    Util.randomSleep();
		    if(Trade.isTradeOpen()) {
				break;
			}
		    
		    // Find the mule
 			if(Players.find(MULE_NAME).length == 0) {
 				Util.log("botTradeMule(): Mule not found!");
 				return false;
 			}
 			
 			RSPlayer mule = Players.find(MULE_NAME)[0];
			// Trade the mule
			mule.click("Trade with "+MULE_NAME);
			Util.randomSleepRange(2000, 4000);
		}
		
		// wait another 30 seconds or until the trade window opens
		waitTill = Util.secondsLater(30);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(isTradeOpen()) {
				break;
			}
		}
		// If no trade window open
		if(!isTradeOpen()) {
			Util.log("botTradeMule(): No trade opened after 1.5 minutes");
			return false;
		}
		
		//Make sure we're trading the correct person
		if(!Trading.getOpponentName().equalsIgnoreCase(MULE_NAME)) {
			Util.log("botTradeMule(): NOT TRADING MULE!");
			Trading.close();
			return false;
		}
		
		
		// Offer the plat
		Util.log("botTradeMule(): Adding plat to trade");
		while(Inventory.find("Platinum token").length != 0){
			Trading.offer(0, "Platinum token");
			Util.randomSleepRange(4000, 7000);
		}
		
		Util.log("botTradeMule(): Accepting trade");
		// Loop till accept
		while(true) {
			// Make sure we're still trading
			if(!isTradeOpen()) {
				break;
			}
			
			
			if(Trading.hasAccepted(false) != true) {
				Trading.accept();
			}
			Util.randomSleep();
		}
		
		Util.log("botTradeMule(): Trading done!");
		return true;
		
	}
	
	public static boolean muleTradeBot(String[] muleTarget){
		//Util.log("muleTradeBot(): ");
		
		Util.log("muleTradeBot(): Hopping worlds...");
		// Hop to that world
		if(!WorldHopper.changeWorld(Integer.parseInt(muleTarget[1]))) {
			Util.log("Failed to hop worlds");
			return false;
		}
		Util.randomSleepRange(6000, 10000);
		
		Util.log("muleTradeBot(): Logging in...");
		// log in
		if(!Login.login()) {
			Util.log("muleTradeBot(): Failed to login");
			return false;
		}
		Util.randomSleep();
		Camera.setCamera(0, 100);
		Util.randomSleep();
		
		// look for the player
		Util.log("muleTradeBot(): Trying to find player: "+muleTarget[0]);
		for(int i = 0; i< 15; i++) {
			if(Players.find(muleTarget[0]).length == 0) {
				Util.log("muleTradeBot(): Player not found! Attempt # "+i);
				Util.randomSleepRange(2000, 6000);
			}else {
				break;
			}
		}
		
		// No player found
		if(Players.find(muleTarget[0]).length == 0) {
			Util.log("muleTradeBot(): Player not found! Quitting...");
			return false;
		}
		
		// Walk to the spot to trade
		if(!Util.walkMuleToTrade()) {
			Util.log("muleTradeBot(): Unable to walk to bot");
			return false;
		}
		Util.randomSleepRange(2000, 4000);
		
		// Target the player
		RSPlayer target = Players.find(muleTarget[0])[0];
		boolean isTrading = false;
		
		long waitTill = Util.secondsLater(60*2);
		// Keep trading the player till we get a trade window
		while(!isTrading && Util.time() < waitTill) {
			Util.log("muleTradeBot(): Attempting to trade: "+muleTarget[0]);
			Interfaces.closeAll();

			// If the player isn't in the correct spot, wait
			if(target.getPosition().getY() != 3489) {
				Util.log("muleTradeBot(): Player not standing in correct spot");
				Util.randomSleepRange(3000, 5000);
				continue;
			}
			
			// Trade the player
			target.click("Trade with "+muleTarget[0]);
			
			
			for(int i = 0; i < 15; i++) {
				Util.log("muleTradeBot(): waiting for trade window...");
				Util.randomSleepRange(1000, 3000);
				if(isTradeOpen()) {
					if(!Trading.getOpponentName().equalsIgnoreCase(muleTarget[0])) {
						Util.log("muleTradeBot(): Trading wrong person!");
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
		
		if(!isTradeOpen()) {
			Util.log("muleTradeBot(): Not trading anyone");
			return false;
		}
		
		Util.log("muleTradeBot(): Accepting Trade");
		// Loop till accept
		while(true) {
			// Make sure we're still trading
			if(!isTradeOpen()) {
				break;
			}
			
			
			if(Trading.hasAccepted(true) == true) {
				Trading.accept();
			}
			Util.randomSleep();
		}
		
		Util.log("muleTradeBot(): Trade done");
		return true;
	}

	
	public static boolean isTradeOpen() {
		if(Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW || Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
			return true;
		}
		return false;
	}
}
