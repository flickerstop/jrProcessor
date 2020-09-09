package scripts;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Starting;

import scripts.util.BreakManager;
import scripts.util.Network;
import scripts.util.Util;
// Test

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting{


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		Network.init("Jr Test");
		BreakManager.buildBreakSchedule();
		
		BreakManager.outputSchedule();
		
		Util.log(BreakManager.getCurrentTask());
	}

}
	
	

