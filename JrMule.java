package scripts;

import org.tribot.api2007.Login;
import org.tribot.api2007.WorldHopper;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.util.Network;
import scripts.util.Trade;
import scripts.util.Util;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "jrMule")
public class JrMule extends Script implements Starting, Ending {

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Util.clearConsole();
	}

	@Override
	public void run() {
		
		this.setLoginBotState(false);
		while(true) {
			
			Util.log("making sure we're logged out");
			// Check if we're all the login screen
			if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
				Util.log("Logging out");
				// if not, log out
				Login.logout();
				Util.randomSleepRange(3000,4000,false);
			}
			
			
			
			// check if there's any plat tokens to collect
			try {
				Util.log("Getting next target");
				String[] muleTarget = Network.getNextMuleTarget();
				
				// There's a target
				if(muleTarget != null) {
					Util.log("Target found!");
					Util.log("Name: "+muleTarget[0]);
					Util.log("World: "+muleTarget[1]);
					if(!Trade.tradeTarget(muleTarget)) {
						// Trade failed
						Util.log("Trading failed!");
						Util.randomSleepRange(3000,4000);
						Login.logout();
						Util.randomSleepRange(45000, 60000,false);
					}else {
						Util.log("Trading Success!");
						Util.randomSleepRange(3000,4000);
						Login.logout();
						Util.randomSleepRange(60000, 120000,false);
					}
				}else {
					Util.log("No target to trade");
					Util.randomSleepRange(145000, 180000,false);
				}
			}catch(Exception e) {
				e.printStackTrace();
				Login.logout();
				Util.randomSleepRange(45000, 80000,false);
			}
			
			

		}
	}
	

}