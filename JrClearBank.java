package scripts;

import org.tribot.api2007.Banking;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

import scripts.util.Bank;
import scripts.util.GE;
import scripts.util.Network;
import scripts.util.Util;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "jrClearBank")
public class JrClearBank extends Script{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Network.updateBotSubTask("Emptying Bank");
		
		Util.randomSleep();
		GE.closeGE();
		Util.randomSleep();
		// Time to Empty bank and sell
		Util.log("Emptying Bank");
		Bank.emptyBank(true);
		Util.randomSleep();
		
		Util.log("Closing Bank");
		Banking.close();
		Util.randomSleep();
		
		Util.log("Opening GE");
		GE.openGE();
		
		Network.updateBotSubTask("Selling Inventory");
		Util.log("Selling Inventory");
		GE.sellInventory();
		

		Network.updateBotSubTask("Converting GP to Plat");
		Util.log("Converting some coins over to plat");
		Bank.convertToPlatTokens();
		Util.randomSleepRange(2000, 4000);
		
		Banking.withdraw(0, "Platinum token");
		Banking.close();
	}

}
