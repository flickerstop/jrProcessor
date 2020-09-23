package scripts.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

import scripts.JrProcessor;

public class NPCTalk {
	
	public static boolean kaqemeex1() {

		Util.log("kaqemeex1(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Kaqemeex").length != 0 ? NPCs.find("Kaqemeex")[0] : null;
		
		Network.updateSubTask("Looking for kaqemeex");
		// Check for null
		if(npc == null) {
			Util.log("kaqemeex1(): Unable to find NPC");
			return false;
		}
		
		long waitTill = Util.secondsLater(60);
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
		
		
		Network.updateSubTask("Talking to kaqemeex");
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {

			// Get the chat options
			List<String> chatOptions = NPCChat.getOptions() != null ? Arrays.asList(NPCChat.getOptions()) : Arrays.asList(new String[0]);
			
			// If the chat options contains the first
			if(chatOptions.contains("I'm in search of a quest.")) {
				Util.log("kaqemeex1(): Option 1");
				NPCChat.selectOption("I'm in search of a quest.", true);
			}else if(chatOptions.contains("I'm in search of a quest")) {
				Util.log("kaqemeex1(): Option 1");
				NPCChat.selectOption("I'm in search of a quest", true);
			}
			// Second Options
			else if(chatOptions.contains("Okay, I will try and help.")) {
				Util.log("kaqemeex1(): Option 2");
				NPCChat.selectOption("Okay, I will try and help.", true);
			}
			else {
				Util.log("kaqemeex1(): Clicking continue...");
				NPCChat.clickContinue(true);
			}
			
			
			
			
			Util.randomSleepRange(200,1000);
			if(NPCChat.getOptions() == null && NPCChat.getName() == null && NPCChat.getMessage() == null) {
				Util.log("kaqemeex1(): Chat done");
				return true;
			}
		}
		
		return false;
		
		
	}
	
	public static boolean kaqemeex2() {

		Util.log("kaqemeex2(): Looking for NPC");
		// Find the npc
		Network.updateSubTask("Looking for kaqemeex");
		RSNPC npc =  NPCs.find("Kaqemeex").length != 0 ? NPCs.find("Kaqemeex")[0] : null;
		
		
		// Check for null
		if(npc == null) {
			Util.log("kaqemeex2(): Unable to find NPC");
			return false;
		}
		
		Network.updateSubTask("Talking to kaqemeex");
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
		Network.updateSubTask("Looking for sanfew");
		
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
		Network.updateSubTask("Talking to Sanfew");
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
							Network.updateSubTask("First option done");
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
							Network.updateSubTask("Second option done");
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
		Network.updateSubTask("Looking for sanfew");
		
		// Check for null
		if(npc == null) {
			Util.log("sanfew2(): Unable to find NPC");
			return false;
		}
		
		Network.updateSubTask("Talking to sanfew");
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
				Network.updateSubTask("Using "+items.get(i)+" on cauldron");
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
	
	public static boolean mizgog() {

		Util.log("mizgog(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Wizard Mizgog").length != 0 ? NPCs.find("Wizard Mizgog")[0] : null;
		
		Network.updateSubTask("Looking for Wizard Mizgog");
		// Check for null
		if(npc == null) {
			Util.log("mizgog(): Unable to find NPC");
			return false;
		}
		
		long waitTill = Util.secondsLater(15);
		while(Util.time() < waitTill) {
			npc.click("Talk-to Wizard Mizgog");
			Util.randomSleepRange(4000,7000);
		    
		    if(NPCChat.getName() != null && (NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName()) || NPCChat.getName().equalsIgnoreCase("Wizard Mizgog"))) {
		    	break;
		    }
		}
		
		if(!NPCChat.getName().equalsIgnoreCase(Player.getRSPlayer().getName()) && !NPCChat.getName().equalsIgnoreCase("Wizard Mizgog")) {
			Util.log("mizgog(): Unable to talk to NPC");
			return false;
	    }
		
		
		boolean firstDone = false;
		Network.updateSubTask("Talking to Wizard Mizgog");
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {
			// If this chat has options and the first isn't done
			if(NPCChat.getOptions() != null) {
				Util.log("mizgog(): Chat has options (first selection menu)");
				// Loop through the chat options
				for(String option : NPCChat.getOptions()) {
					// If this option matches what we need
					if(option.equalsIgnoreCase("Give me a quest please.")) {
						Util.log("mizgog(): Correct option found");
						if(NPCChat.selectOption("Give me a quest please.", true)) {
							Util.log("mizgog(): Clicked correct option");
							firstDone = true;
							Network.updateSubTask("First option done");
							continue;
						}else {
							Util.log("mizgog(): Unable to click option");
							return false;
						}
					}
				}
			}
			
			Util.log("mizgog(): Clicking continue...");
			NPCChat.clickContinue(true);

			
			Util.randomSleep();
			if(NPCChat.getOptions() == null && NPCChat.getName() == null) {
				Util.log("mizgog(): Chat done");
				break;
			}
		}
		
		if(firstDone) {
			Util.log("mizgog(): Both options were selected");
			return true;
		}else {
			Util.log("mizgog(): Error selecting option");
			return false;
		}
	}

	
	public static boolean veos() {
		Util.log("veos(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Veos").length != 0 ? NPCs.find("Veos")[0] : null;
		boolean fastTravel = false;
		
		Network.updateSubTask("Looking for Veos");
		// Check for null
		if(npc == null) {
			Util.log("veos(): Unable to find NPC");
			return false;
		}
		
		// Check if Veos has the option for land's end
		for(String option : npc.getActions()) {
			if(option.equalsIgnoreCase("Port Piscarilius")) {
				fastTravel = true;
			}
		}
		
		if(fastTravel) {
			boolean clickedTravel = false;
			long waitTill = Util.secondsLater(10);
			while(Util.time() < waitTill) {
			    Util.randomSleep();
			    if(npc.click("Port Piscarilius Veos")) {
			    	clickedTravel = true;
			    	break;
			    }
			}
			if(!clickedTravel) {
				Util.log("veos(): Failed fast Travel");
				return false;
			}else {
				// Wait till we're on the ship
				waitTill = Util.secondsLater(15);
				while(Util.time() < waitTill) {
				    Util.randomSleep();
				    if(Player.getPosition().distanceTo(new RSTile(1824, 3695, 1)) < 5) {
				    	Util.randomSleepRange(2000, 4000);
				    	// We're on the ship, look for the Gangplank
			    		return Walk.crossGangplank();
				    }
				}
				return false;
			}
		}
		
		return false;
	}

	public static boolean hosidiusClerk() {
		Util.log("hosidiusClerk(): Looking for NPC");
		// Find the npc
		RSNPC npc =  NPCs.find("Clerk").length != 0 ? NPCs.find("Clerk")[0] : null;
		
		Network.updateSubTask("Looking for Clerk");
		// Check for null
		if(npc == null) {
			Util.log("hosidiusClerk(): Unable to find NPC");
			return false;
		}
		
		long waitTill = Util.secondsLater(10);
		while(Util.time() < waitTill) {
		    Util.randomSleep();
		    if(npc.click("Talk-to Clerk")) {
		    	break;
		    }
		}

		// Continue
		
		// Continue
		
		// Yes
		
		// Continue
		
		waitTill = Util.secondsLater(60*3);
		while(Util.time() < waitTill) {

			// Get the chat options
			List<String> chatOptions = NPCChat.getOptions() != null ? Arrays.asList(NPCChat.getOptions()) : Arrays.asList(new String[0]);
			
			// If the chat options contains the first
			if(chatOptions.contains("Yes")) {
				Util.log("hosidiusClerk(): Option 1");
				NPCChat.selectOption("I'm in search of a quest.", true);
			}
			else {
				Util.log("hosidiusClerk(): Clicking continue...");
				NPCChat.clickContinue(true);
			}
			
			
			
			
			Util.randomSleepRange(200,1000);
			if(NPCChat.getOptions() == null && NPCChat.getName() == null && NPCChat.getMessage() == null) {
				Util.log("hosidiusClerk(): Chat done");
				return true;
			}
		}
		
		
		return false;
	}




}
