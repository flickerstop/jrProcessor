package scripts.util;

import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

public class Walk {
	public static boolean walkToPosition(int x, int y, int z) {
		int distanceTo = Player.getPosition().distanceTo(new RSTile(x, y, z));
		
		
		Util.log("walkToPosition(): Attempting to walk to position");
		Util.log("walkToPosition(): x:"+x+" y:"+y+" z:"+z);
		Util.log("walkToPosition(): Distance: "+distanceTo);
		
		int MAX_ATTEMPTS = 3;
		Network.updateSubTask("Walking...");
		for(int walkingAttempt = 0;walkingAttempt < MAX_ATTEMPTS; walkingAttempt++) {
			if(!WebWalking.walkTo(new RSTile(x,y,z))) {
				if(Player.getPosition().distanceTo(new RSTile(x, y, z)) > distanceTo) {
					Util.log("walkToPosition(): Greater distance, failed walking!");
					return false;
				}else {
					Util.log("walkToPosition(): Retrying to walk!");
					Util.log("walkToPosition(): x:"+x+" y:"+y+" z:"+z);
					Util.log("walkToPosition(): Distance: "+Player.getPosition().distanceTo(new RSTile(x, y, z)));
				}
			}
			
			if(Player.getPosition().distanceTo(new RSTile(x, y, z)) < 5){
				Util.log("walkToPosition(): Close enough to tile");
				break;
			}
		}
		
		if(Player.getPosition().distanceTo(new RSTile(x, y, z)) > 5) {
			Util.log("walkToPosition(): Failed to walk within 10 tiles");
			return false;
		}
		
		return true;
	}
	
	public static boolean walkToRandom(int x1, int y1, int x2, int y2, int z) {
		
		int x = 0;
		
		if(x1 > x2) {
			x = General.random(x2, x1);
		}else {
			x = General.random(x1, x2);
		}
		
		int y = 0;
		
		if(y1 > y2) {
			y = General.random(y2, y1);
		}else {
			y = General.random(y1, y2);
		}
		
		
		return walkToPosition(x,y,z);
	}
	
	public static boolean miniMapWalkToRandom(int x1, int y1, int x2, int y2, int z) {
		
		int x = 0;
		
		if(x1 > x2) {
			x = General.random(x2, x1);
		}else {
			x = General.random(x1, x2);
		}
		
		int y = 0;
		
		if(y1 > y2) {
			y = General.random(y2, y1);
		}else {
			y = General.random(y1, y2);
		}
		
		
		return Walking.clickTileMM(new RSTile(x,y,z), 1);
	}
	
	public static boolean climbUpStairs() {
		Camera.setCamera(0,100);
		RSTile startingPos = Player.getPosition();
		
		RSObject stairs = Objects.findNearest(5, "Staircase").length > 0 ? Objects.findNearest(5, "Staircase")[0] : null;
		Util.log("climbUpStairs(): Attempting to climb up stairs");
		Network.updateSubTask("Looking for stairs");
		if(stairs == null) {
			Util.log("climbUpStairs(): Unable to find stairs");
			return false;
		}
		
		boolean hasClickedRight = false;
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(2000,3000);
		    if(stairs.click("Climb-up Staircase")) {
		    	hasClickedRight = true;
		    	break;
		    }
		}
		if(!hasClickedRight) {
			Util.log("climbUpStairs(): Unable to click stairs");
			return false;
		}
		Network.updateSubTask("Stairs Clicked");
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(startingPos) > 10 || startingPos.getPlane() != Player.getPosition().getPlane()) {
		    	break;
		    }
		}
		
		Util.log("climbUpStairs(): Stairs climbed up");
		return true;
	}
	
	public static boolean climbDownStairs() {
		Camera.setCamera(0,100);
		RSTile startingPos = Player.getPosition();
		
		RSObject stairs = Objects.findNearest(5, "Staircase").length > 0 ? Objects.findNearest(5, "Staircase")[0] : null;
		Util.log("climbDownStairs(): Attempting to climb down stairs");
		Network.updateSubTask("Looking for stairs");
		if(stairs == null) {
			Util.log("climbDownStairs(): Unable to find stairs");
			return false;
		}
		
		boolean hasClickedRight = false;
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(2000,3000);
		    if(stairs.click("Climb-down Staircase")) {
		    	hasClickedRight = true;
		    	break;
		    }
		}
		if(!hasClickedRight) {
			Util.log("climbDownStairs(): Unable to click stairs");
			return false;
		}
		
		Network.updateSubTask("Stairs Clicked");
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(startingPos) > 10 || startingPos.getPlane() != Player.getPosition().getPlane()) {
		    	break;
		    }
		}
		
		Util.log("climbUpStairs(): Stairs climbed down");
		return true;
	}
	
	
	public static boolean climbUpLadder() {
		Camera.setCamera(0,100);
		RSTile startingPos = Player.getPosition();
		
		
		RSObject stairs = Objects.findNearest(5, "Ladder").length > 0 ? Objects.findNearest(5, "Ladder")[0] : null;
		Util.log("climbUpLadder(): Attempting to climb up ladder");
		Network.updateSubTask("Looking for ladder");
		if(stairs == null) {
			Util.log("climbUpLadder(): Unable to find ladder");
			return false;
		}
		
		boolean hasClickedRight = false;
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(2000,3000);
		    if(stairs.click("Climb-up Ladder")) {
		    	hasClickedRight = true;
		    	break;
		    }
		}
		if(!hasClickedRight) {
			Util.log("climbUpLadder(): Unable to click ladder");
			return false;
		}
		
		Network.updateSubTask("Ladder Clicked");
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(startingPos) > 10 || startingPos.getPlane() != Player.getPosition().getPlane()) {
		    	break;
		    }
		}
		
		Util.log("climbUpLadder(): ladder climbed up");
		return true;
	}
	
	public static boolean climbDownLadder() {
		Camera.setCamera(0,100);
		RSTile startingPos = Player.getPosition();
		
		RSObject stairs = Objects.findNearest(5, "Ladder").length > 0 ? Objects.findNearest(5, "Ladder")[0] : null;
		Util.log("climbDownLadder(): Attempting to climb down Ladder");
		Network.updateSubTask("Looking for ladder");
		if(stairs == null) {
			Util.log("climbDownLadder(): Unable to find Ladder");
			return false;
		}
		
		boolean hasClickedRight = false;
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(2000,3000);
		    if(stairs.click("Climb-down Ladder")) {
		    	hasClickedRight = true;
		    	break;
		    }
		}
		if(!hasClickedRight) {
			Util.log("climbDownLadder(): Unable to click ladder");
			return false;
		}
		
		Network.updateSubTask("Ladder Clicked");
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(startingPos) > 10 || startingPos.getPlane() != Player.getPosition().getPlane()) {
		    	break;
		    }
		}
		
		Util.log("climbDownLadder(): Ladder climbed down");
		return true;
	}
	
	
	public static boolean openPrisonGate() {
		RSObject gate = Objects.findNearest(5, "Prison door").length > 0 ? Objects.findNearest(5, "Prison door")[0] : null;
		Util.log("openPrisonGate(): Find gate");
		
		if(gate == null) {
			Util.log("openPrisonGate(): Unable to find gate");
			return false;
		}
		Network.updateSubTask("Spamming Gate");
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			
			
			if(Player.getPosition().equals(new RSTile(2889,9830,0)) || Player.getPosition().equals(new RSTile(2889,9831))) {
				Util.log("openPrisonGate(): Walked through gate");
		    	return true;
		    }
			
			if(Player.getPosition().getY() >= 2889 && (Player.getPosition().getX() >= 9820 && Player.getPosition().getX() <= 9840)) {
				Util.log("openPrisonGate(): Walked through gate then clicked");
		    	return true;
			}
			
			gate.click("Open Prison door");
		    Util.randomSleepRange(300,500);
		    
		}
		
		Util.log("openPrisonGate(): Failed to walk through gate!");
		return false;
		
		
	}
	
	public static boolean enterTrapdoor() {
		Camera.setCamera(0,100);
		RSTile startingPos = Player.getPosition();
		
		RSObject stairs = Objects.findNearest(5, "Trapdoor").length > 0 ? Objects.findNearest(5, "Trapdoor")[0] : null;
		Util.log("enterTrapdoor(): Attempting to climb Trapdoor");
		Network.updateSubTask("Looking for Trapdoor");
		if(stairs == null) {
			Util.log("enterTrapdoor(): Unable to find Trapdoor");
			return false;
		}
		
		boolean hasClickedRight = false;
		long waitTill = Util.secondsLater(20);
		while(Util.time() < waitTill) {
		    Util.randomSleepRange(2000,3000);
		    if(stairs.click("Enter Trapdoor")) {
		    	hasClickedRight = true;
		    	break;
		    }
		}
		if(!hasClickedRight) {
			Util.log("enterTrapdoor(): Unable to click stairs");
			return false;
		}
		
		Network.updateSubTask("Trapdoor Clicked");
		waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(Player.getPosition().distanceTo(startingPos) > 10 || startingPos.getPlane() != Player.getPosition().getPlane()) {
		    	break;
		    }
		}
		
		Util.log("enterTrapdoor(): Stairs climbed down");
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
