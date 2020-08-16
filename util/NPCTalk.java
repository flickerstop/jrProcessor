package scripts.util;

import java.util.ArrayList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;

import scripts.JrProcessor;

public class NPCTalk {
	
	public static boolean kaqemeex1() {

		Util.log("kaqemeex1(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Kaqemeex").length != 0 ? NPCs.find("Kaqemeex")[0] : null;
		
		
		// Check for null
		if(npc == null) {
			Util.log("kaqemeex1(): Unable to find NPC");
			return false;
		}
		
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			npc.click("Talk-to Kaqemeex");
			Util.randomSleepRange(4000,7000);
		    
		    if(NPCChat.getName() != null && NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
		    	break;
		    }
		}
		
		if(!NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
			Util.log("kaqemeex1(): Unable to talk to NPC");
			return false;
	    }
		
		
		boolean firstDone = false;
		boolean secondDone = false;
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {
		    Util.log("kaqemeex1(): Clicking continue...");
			NPCChat.clickContinue(true);
			
			// If this chat has options and the first isn't done
			if(NPCChat.getOptions() != null) {
				Util.log("kaqemeex1(): Chat has options (first selection menu)");
				// Loop through the chat options
				for(String option : NPCChat.getOptions()) {
					// If this option matches what we need
					if(option.equalsIgnoreCase("I'm in search of a quest.")) {
						Util.log("kaqemeex1(): Correct option found");
						if(NPCChat.selectOption("I'm in search of a quest.", true)) {
							Util.log("kaqemeex1(): Clicked correct option");
							firstDone = true;
						}else {
							Util.log("kaqemeex1(): Unable to click option");
							return false;
						}
					}
				}
			}
			
			// If this chat has options and the first isn't done
			if(NPCChat.getOptions() != null) {
				Util.log("kaqemeex1(): Chat has options (second selection menu)");
				// Loop through the chat options
				for(String option : NPCChat.getOptions()) {
					// If this option matches what we need
					if(option.equalsIgnoreCase("Okay, I will try and help.")) {
						Util.log("kaqemeex1(): Correct option found");
						if(NPCChat.selectOption("Okay, I will try and help.", true)) {
							Util.log("kaqemeex1(): Clicked correct option");
							secondDone = true;
						}else {
							Util.log("kaqemeex1(): Unable to click option");
							return false;
						}
					}
				}
			}

			
			Util.randomSleep();
			if(NPCChat.getOptions() == null && NPCChat.getName() == null) {
				Util.log("kaqemeex1(): Chat done");
				break;
			}
		}
		
		if(firstDone && secondDone) {
			Util.log("kaqemeex1(): Both options were selected");
			return true;
		}else {
			Util.log("kaqemeex1(): Error selecting option");
			return false;
		}
	}
	
	public static boolean kaqemeex2() {

		Util.log("kaqemeex2(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Kaqemeex").length != 0 ? NPCs.find("Kaqemeex")[0] : null;
		
		
		// Check for null
		if(npc == null) {
			Util.log("kaqemeex2(): Unable to find NPC");
			return false;
		}
		
		
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			npc.click("Talk-to Kaqemeex");
		    Util.randomSleepRange(2000,4000);
		    
		    if(NPCChat.getName() != null && NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
		    	break;
		    }
		}
		
		if(!NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
			Util.log("kaqemeex2(): Unable to talk to NPC");
			return false;
	    }
		
		
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {
		    Util.log("kaqemeex2(): Clicking continue...");
			NPCChat.clickContinue(true);

			
			Util.randomSleep();
			
			if(NPCChat.getName() == null) {
				Util.log("kaqemeex2(): Chat done");
				break;
			}
		}
		
		
		Util.log("kaqemeex2(): Done Chatting");
		return true;
	}

	public static boolean sanfew1() {

		Util.log("sanfew1(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Sanfew").length != 0 ? NPCs.find("Sanfew")[0] : null;
		
		
		// Check for null
		if(npc == null) {
			Util.log("sanfew1(): Unable to find NPC");
			return false;
		}
		
		
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			npc.click("Talk-to Sanfew");
		    Util.randomSleepRange(2000,4000);
		    
		    if(NPCChat.getName() != null && NPCChat.getName().equalsIgnoreCase("Sanfew")) {
		    	break;
		    }
		}
		
		if(!NPCChat.getName().equalsIgnoreCase("Sanfew")) {
			Util.log("sanfew1(): Unable to talk to NPC");
			return false;
	    }
		
		
		boolean firstDone = false;
		boolean secondDone = false;
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {
		    Util.log("sanfew1(): Clicking continue...");
			NPCChat.clickContinue(true);
			
			// If this chat has options and the first isn't done
			if(NPCChat.getOptions() != null) {
				Util.log("sanfew1(): Chat has options (first selection menu)");
				// Loop through the chat options
				for(String option : NPCChat.getOptions()) {
					// If this option matches what we need
					if(option.equalsIgnoreCase("I've been sent to help purify the Varrock stone circle.")) {
						Util.log("sanfew1(): Correct option found");
						if(NPCChat.selectOption("I've been sent to help purify the Varrock stone circle.", true)) {
							Util.log("sanfew1(): Clicked correct option");
							firstDone = true;
						}else {
							Util.log("sanfew1(): Unable to click option");
							return false;
						}
					}
				}
			}
			
			// If this chat has options and the first isn't done
			if(NPCChat.getOptions() != null) {
				Util.log("sanfew1(): Chat has options (second selection menu)");
				// Loop through the chat options
				for(String option : NPCChat.getOptions()) {
					// If this option matches what we need
					if(option.equalsIgnoreCase("Ok, I'll do that then.")) {
						Util.log("sanfew1(): Correct option found");
						if(NPCChat.selectOption("Ok, I'll do that then.", true)) {
							Util.log("sanfew1(): Clicked correct option");
							secondDone = true;
						}else {
							Util.log("sanfew1(): Unable to click option");
							return false;
						}
					}
				}
			}

			
			Util.randomSleep();
			if(NPCChat.getOptions() == null && NPCChat.getName() == null) {
				Util.log("sanfew1(): Chat done");
				break;
			}
		}
		
		if(firstDone && secondDone) {
			Util.log("sanfew1(): Both options were selected");
			return true;
		}else {
			Util.log("sanfew1(): Error selecting option");
			return false;
		}
	}
	
	public static boolean sanfew2() {

		Util.log("sanfew2(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Sanfew").length != 0 ? NPCs.find("Sanfew")[0] : null;
		
		
		// Check for null
		if(npc == null) {
			Util.log("sanfew2(): Unable to find NPC");
			return false;
		}
		
		
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			npc.click("Talk-to Sanfew");
		    Util.randomSleepRange(2000,4000);
		    
		    if(NPCChat.getName() != null && NPCChat.getName().equalsIgnoreCase("Sanfew")) {
		    	break;
		    }
		}
		
		if(!NPCChat.getName().equalsIgnoreCase("Sanfew")) {
			Util.log("sanfew2(): Unable to talk to NPC");
			return false;
	    }
		
		
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {
		    Util.log("sanfew2(): Clicking continue...");
			NPCChat.clickContinue(true);

			
			Util.randomSleep();
			
			if(NPCChat.getName() == null) {
				Util.log("sanfew2(): Chat done");
				break;
			}
		}
		
		
		Util.log("sanfew2(): Done Chatting");
		return true;
	}
	
	
	public static boolean useItemsOnCauldron() {
		// Items to take out
		ArrayList<String> items = new ArrayList<String>();
		items.add("Raw beef");
		items.add("Raw rat meat");
		items.add("Raw bear meat");
		items.add("Raw chicken");
		
		// Loop until we have no more items to move out or 3 minutes have passed
		long maxWait = Util.secondsLater(60*3);
		while(Util.time() < maxWait && items.size() > 0) {
			for(int i = 0; i < items.size(); i++) {
				
				Util.log("useItemsOnCauldron(): Clicking "+items.get(i));
				// click use on the item
				if(Inventory.find(items.get(i))[i].click("Use "+items.get(i))) {
					Util.randomSleep();
					Util.log("useItemsOnCauldron(): Clicking on Cauldron of Thunder");
					if(!Objects.findNearest(5, "Cauldron of Thunder")[0].click("Use "+items.get(i)+" -> Cauldron of Thunder")) {
						// Failed to use items together
						Util.log("useItemsOnCauldron(): Failed to use together");
						// If item is selected
						if(Game.getItemSelectionState() == 1) {
							Util.log("useItemsOnCauldron(): un-selecting item");
							// click the first item in the inventory
							Inventory.getAll()[0].click();
							Util.randomSleep();
						}
					}
				}
				
				long waitTill = Util.secondsLater(5);
				while(Util.time() < waitTill) {
				    Util.randomSleep();
				    if(Inventory.find(items.get(i)).length == 0) {
				    	Util.log("useItemsOnCauldron(): Used "+ items.get(i));
				    	items.remove(items.get(i));
				    	i--;
				    	break;
				    }
				}
			}
		}
		
		if(items.size() > 0) {
			Util.log("useItemsOnCauldron(): Items still left in bank!");
			return false;
		}
		Util.log("useItemsOnCauldron(): Items taken out of the bank");
		return true;

	}
}
