package scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api2007.Game;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.util.Bank;
import scripts.util.Network;
import scripts.util.Util;

// Test

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting, MessageListening07, Arguments {


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		Util.log("______________________________");
		
		Util.log("NPC name: "+ NPCChat.getName());
		
		Util.log("NPC Message: " + NPCChat.getMessage());
		
		Util.log("NPC Options:");
		
		int i = 0;
		for(String option : NPCChat.getOptions()) {
			Util.log("#"+i+": "+option);
			i++;
		}
	}
	

	@Override    
	public void tradeRequestReceived(String s) {
        Util.log(s);
    }

	@Override
	public void passArguments(HashMap<String, String> arguments) {
		
		System.out.println((String)arguments.get("test"));
		
		
		System.out.println(arguments.values());
		
		
	}


	

}
	
	

