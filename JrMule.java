package scripts;

import org.tribot.api2007.Camera;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.util.Network;
import scripts.util.Trade;
import scripts.util.Util;
import scripts.util.Walk;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "jrMule")
public class JrMule extends Script implements Starting, Ending {

	private boolean isSleep = false;
	
	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() {
		Util.clearConsole();
		this.setLoginBotState(false);
	}

	@Override
	public void run() {
		Util.setMule();
		this.setLoginBotState(false);
		
		while(true) {
			// Check if we're all the login screen
			Util.log("making sure we're logged out");
			
			if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
				Util.log("Logging out");
				// if not, log out
				Login.logout();
				long waitTill = Util.secondsLater(30);
				while(Util.time() < waitTill) {
				    Util.randomSleep();
				    // Wait till we're at the login screen
				    if(Login.getLoginState() == Login.STATE.LOGINSCREEN) {
				    	break;
				    }
				}
			}
			
			if(isSleep) {
				Util.log("Sleeping for 1-3 minutes...");
				Util.randomSleepRange(1000*60*1, 1000*60*3,true);
				isSleep = false;
			}
			
			Camera.setCamera(0,100);
			// Check if there's any new bot to mule
			String[] muleTarget = Network.getNextMuleTarget();
			
			// If there's no target, wait then repeat
			if(muleTarget == null) {
				Util.log("No target found");
				isSleep = true;
				continue;
			}
			
			// Hop to the world the mule is on
			Util.log("Target found!");
			Util.log("Name: "+muleTarget[0]);
			Util.log("World: "+muleTarget[1]);
			
			// Hop to the world
			if(!WorldHopper.changeWorld(Integer.parseInt(muleTarget[1]))){
				Util.log("Failed to switch worlds");
				isSleep = true;
				continue;
			}
			
			// Login
			if(!Login.login()) {
				Util.log("Failed to login");
				isSleep = true;
				continue;
			}
			
			// Check if we're far within the GE
			if(Player.getPosition().distanceTo(new RSTile(3167,3488,0)) > 10) {
				Walk.walkToPosition(3167,3488,0);
				Util.waitTillMovingStops();
			}
			
			
			if(!Trade.tradeBot(muleTarget[0])) {
				Util.log("Trading failed.");
				isSleep = true;
				continue;
			}
			
			// Find the next target
			isSleep = true;
		}
	}
	

}