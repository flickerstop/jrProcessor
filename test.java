package scripts;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Starting;

import scripts.util.Bank;
import scripts.util.Cooking;
import scripts.util.Util;
// Test
import scripts.util.Walk;
import scripts.util.Zeah;

@ScriptManifest(authors = { "JR" }, category = "Tools", name = "JR TESTER")
public class test extends Script implements Starting{


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		suspendAntiban();

//		Zeah.teleportToDraynor();
//		Util.randomSleep();
//		Zeah.walkToVeos();
//		Util.randomSleep();
//		Zeah.takeTheBoat();
//		Util.randomSleep();
//		Zeah.walkDocksToHosidius();
//		Util.randomSleep();
//		Zeah.hosidiusToClerk();
//		Util.randomSleep();
//		Zeah.clerkToKitchen();
//		Util.randomSleep();
		
		
		while(true) {
			Zeah.bankInKitcken();
			Util.randomSleep();
			Bank.depositAll();
			Bank.takeOutCookingTrainingFish();
			Bank.closeBank();
			Cooking.cookFishOnOven();
			
		}
		
		
		
		
		
		
		
//		RSTile walkPath[] = {
//				new RSTile(1774, 3632, 0),
//				new RSTile(1768, 3605, 0)
//		};
//		
//		
//		Walk.walkHumanPath(walkPath);
//		
		//return;
		
	}

	
	@SuppressWarnings("deprecation")
    public static void suspendAntiban() {
        for (Thread thread : Thread.getAllStackTraces().keySet())
            if (thread.getName().contains("Antiban") || thread.getName().contains("Fatigue"))
                thread.suspend();
        
        Util.log("Anti-ban Suspended");
    }
	

}
	
	

