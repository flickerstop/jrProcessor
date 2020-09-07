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
	

	
	public static boolean isTradeOpen() {
		if(Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW || Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
			return true;
		}
		return false;
	}
	
	public static boolean tradeMule() {
		Util.log("tradeMule(): Looking for mule");
		// Find the mule
		RSPlayer mule = Players.find(ServerInfo.getMuleName()).length > 0?Players.find(ServerInfo.getMuleName())[0]:null;
		
		// Check if we found the mule
		if(mule == null) {
			Util.log("tradeMule(): Unable to find mule");
			return false;
		}
		Util.log("tradeMule(): Trading with mule");
		
		// Wait 120 total seconds
		long totalWaitTime = Util.secondsLater(120);
		while(Util.time() < totalWaitTime) {
			mule.click("Trade with "+ServerInfo.getMuleName());
			// Wait 20 seconds then trade again
			long waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleep();
			    if(isTradeOpen()) {
			    	break;
			    }
			}
			if(isTradeOpen()) {
		    	break;
		    }
		}
		
		// Check if the trade window is open
		if(!isTradeOpen()) {
			Util.log("tradeMule(): Unable to trade with the mule");
	    	return false;
	    }
		
		// Check if we're trading the correct person
		if(!Trading.getOpponentName().equalsIgnoreCase(ServerInfo.getMuleName())) {
			Util.log("tradeMule(): NOT TRADING MULE!");
			Trading.close();
			return false;
		}
		
		Util.log("tradeMule(): Trading over plat tokens");
		// Trade over the plat tokens
		if(!Trading.offer(0, "Platinum token")) {
			// If failed to trade the tokens over, try every 4 seconds for 5 seconds
			long waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(2000, 4000);
			    if(Trading.offer(0, "Platinum token")) {
			    	break;
			    }
			}
		}
		
		
		Util.log("tradeMule(): Accepting the trade");
		// Accept the first trade window
		if(!Trading.accept()) {
			// If failed to accept, keep trying
			long waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(2000, 4000);
			    // If somehow we're on the 2nd window
			    if(Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
			    	break;
			    }
			    
			    if(Trading.accept()) {
			    	break;
			    }
			}
		}
		
		Util.log("tradeMule(): Waiting for 2nd trade window");
		
		long waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
		    	break;
		    }
		}
		
		// Make sure we're at the second window
		if(Trading.getWindowState() != Trading.WINDOW_STATE.SECOND_WINDOW) {
			Util.log("tradeMule(): 2nd trade window never hit");
			Trading.close();
			return false;
	    }
		
		
		Util.log("tradeMule(): Accepting 2nd trade window");
		// Accept the second trade window
		if(!Trading.accept()) {
			// If failed to accept, keep trying
			waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(2000, 4000);
			    if(Trading.accept()) {
			    	break;
			    }
			    
			    if(!isTradeOpen()) {
			    	break;
			    }
			}
		}
		Util.log("tradeMule(): Waiting for trade to be finished");
		waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(!isTradeOpen()) {
		    	break;
		    }
		}
		
		// Check for plat tokens in the inventory
		if(Inventory.find("Platinum token").length != 0) {
			Util.log("tradeMule(): Plat tokens still in inventory!");
			return false;
		}
		
		Util.log("tradeMule(): Trade successful!");
		return true;
	}
	
	public static boolean tradeBot(String botName) {
		
		// Look for the bot
		Util.log("tradeBot(): Looking for bot");
		RSPlayer mule = Players.find(botName).length > 0?Players.find(botName)[0]:null;
		
		// Check if we found the mule
		if(mule == null) {
			Util.log("tradeBot(): Unable to find bot");
			return false;
		}
		Util.log("tradeBot(): Trading with bot");
		
		// Wait 120 total seconds
		long totalWaitTime = Util.secondsLater(240);
		while(Util.time() < totalWaitTime) {
			mule.click("Trade with "+botName);
			// Wait 20 seconds then trade again
			long waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleep();
			    if(isTradeOpen()) {
			    	break;
			    }
			}
			if(isTradeOpen()) {
		    	break;
		    }
		}
		
		// Check if the trade window is open
		if(!isTradeOpen()) {
			Util.log("tradeBot(): Unable to trade with the bot");
	    	return false;
	    }
		
		// Check if we're trading the correct person
		if(!Trading.getOpponentName().equalsIgnoreCase(botName)) {
			Util.log("tradeBot(): NOT TRADING BOT!");
			Trading.close();
			return false;
		}
		
		
		Util.log("tradeBot(): Accepting the trade");
		// Accept the first trade window
		if(!Trading.accept()) {
			// If failed to accept, keep trying
			long waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(2000, 4000);
			    if(Trading.accept()) {
			    	break;
			    }
			}
		}
		
		
		Util.log("tradeBot(): Waiting for 2nd trade window");
		long waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW) {
		    	break;
		    }
		    if(!Trading.hasAccepted(false)) {
		    	Trading.accept();
		    }
		}
		
		// Make sure we're at the second window
		if(Trading.getWindowState() != Trading.WINDOW_STATE.SECOND_WINDOW) {
			Util.log("tradeBot(): 2nd trade window never hit");
			Trading.close();
			return false;
	    }
		
		
		Util.log("tradeBot(): Accepting 2nd trade window");
		// Accept the second trade window
		if(!Trading.accept()) {
			// If failed to accept, keep trying
			waitTill = Util.secondsLater(20);
			while(Util.time() < waitTill) {
			    Util.randomSleepRange(2000, 4000);
			    if(Trading.accept()) {
			    	break;
			    }
			}
		}
		
		Util.log("tradeBot(): Waiting for trade to be finished");
		waitTill = Util.secondsLater(60);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(!isTradeOpen()) {
		    	break;
		    }
		}
		
		if(isTradeOpen()) {
			Util.log("tradeBot(): Trade never ended");
			Trading.close();
	    	return false;
	    }
		
		
		Util.log("tradeBot(): Trade successful!");
		return true;
	}
}
