package scripts;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Starting;

import scripts.util.BreakManager;
import scripts.util.Items;
import scripts.util.Network;
import scripts.util.Util;
// Test
import scripts.util.Walk;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting{


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		Walk.checkForDoor();
	}

}
	
	

