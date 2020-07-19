package scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.PreBreaking;
import org.tribot.script.interfaces.Starting;

import scripts.util.Bank;
import scripts.util.Network;
import scripts.util.Util;


@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting, MessageListening07 {


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		while(true) {
			Util.randomSleep();
		}
		
	}
	

	@Override    
	public void tradeRequestReceived(String s) {
        Util.log(s);
    }

}
	
	

