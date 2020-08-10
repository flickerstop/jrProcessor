package scripts.objects;

import java.util.ArrayList;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Skills;

import scripts.JrProcessor;
import scripts.util.Util;

public class ItemProcessManager {
	
	private static int item1Total = 0;
	private static int item2Total = 0;
	private static int resultTotal = 0;
	
	
	public static ProcessingObject searchBank() {
		
		Util.log("searchBank(): Looking for next process");
		
		int vialsOfWaterNeeded = 0;
		
		for(ProcessingObject process : getListOfProcesses()) {
			
			int item1Count = 0;
			int item2Count = 0;
			
			// If we're using a 2nd item
			if(process.item2Count != 0) {
				item1Count = Banking.find(process.item1).length != 0 ? Banking.find(process.item1)[0].getStack() : 0;
				item2Count = Banking.find(process.item2).length != 0 ? Banking.find(process.item2)[0].getStack() : 0;
			}else { // If only using 1 item
				item1Count = Banking.find(process.item1).length != 0 ? Banking.find(process.item1)[0].getStack() : 0;
			}
			
			// If we can do this process
			if(item1Count >= process.item1Count && item2Count >= process.item2Count) {
				Util.log("searchBank(): Found "+ process.result);
				return process;
			}else {
				// If we can't do this process since we're missing vials of water
				if(item1Count > 0 && item2Count == 0 && process.item2.equalsIgnoreCase("Vial of water")) {
					Util.log("searchBank(): need to buy vials of water for this task");
					Util.log("searchBank(): amount: " + item1Count);
					vialsOfWaterNeeded += item1Count;
				}
			}
		}
		if(vialsOfWaterNeeded > 0) {
			JrProcessor.setStatus(JrProcessor.STATUS.MISSING_VIALS_OF_WATER, vialsOfWaterNeeded);
		}
		
		return null;
	}
	
	private static boolean checkItem(ProcessingObject itemToProcess) {
		try {
			int item1Count = 0;
			int item2Count = 0;
			
			// If we're using a 2nd item
			if(itemToProcess.item2Count != 0) {
				item1Count = Banking.find(itemToProcess.item1)[0].getStack();
				item2Count = Banking.find(itemToProcess.item2)[0].getStack();
			}else { // If only using 1 item
				item1Count = Banking.find(itemToProcess.item1)[0].getStack();
			}
			
			if(item1Count >= itemToProcess.item1Count && item2Count >= itemToProcess.item2Count) {
				return true;
			}
			
		
		}catch(Exception e) {
			// do something
		}
		
		
		
		return false;
	}



	public static ArrayList<ProcessingObject> getListOfProcesses() {
		ArrayList<ProcessingObject> listOfProcessingItems = new ArrayList<ProcessingObject>();
		
		// Leveling up herblore
		listOfProcessingItems.add(new ProcessingObject("Eye of newt", "Guam potion (unf)", "Attack potion(3)", 1, 1, 1,0,false));
		
		int herbloreLevel = Skills.getCurrentLevel(Skills.SKILLS.HERBLORE);
		int fletchingLevel = Skills.getCurrentLevel(Skills.SKILLS.FLETCHING);
		
		// Herblore tasks
		//25
		if(herbloreLevel >= 25) {
			listOfProcessingItems.add(new ProcessingObject("Grimy ranarr weed", "", "Ranarr weed", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Ranarr weed", "Vial of water", "Ranarr potion (unf)", 1, 1, 1,0,true));
		}
		
		// 34
		if(herbloreLevel >= 34) {
			listOfProcessingItems.add(new ProcessingObject("Grimy toadflax", "", "Toadflax", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Toadflax", "Vial of water", "Toadflax potion (unf)", 1, 1, 1,0,true));
		}

		// 45
		if(herbloreLevel >= 45) {
			listOfProcessingItems.add(new ProcessingObject("Grimy irit leaf", "", "Irit leaf", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Irit leaf", "Vial of water", "Irit potion (unf)", 1, 1, 1,0,true));
		}

		// 50
		if(herbloreLevel >= 50) {
			listOfProcessingItems.add(new ProcessingObject("Grimy avantoe", "", "Avantoe", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Avantoe", "Vial of water", "Avantoe potion (unf)", 1, 1, 1,0,true));
		}

		// 55
		if(herbloreLevel >= 55) {
			listOfProcessingItems.add(new ProcessingObject("Grimy kwuarm", "", "Kwuarm", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Kwuarm", "Vial of water", "Kwuarm potion (unf)", 1, 1, 1,0,true));
		}

		// 63
		if(herbloreLevel >= 63) {
			listOfProcessingItems.add(new ProcessingObject("Grimy snapdragon", "", "Snapdragon", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Snapdragon", "Vial of water", "Snapdragon potion (unf)", 1, 1, 1,0,true));
		}

		// 66
		if(herbloreLevel >= 66) {
			listOfProcessingItems.add(new ProcessingObject("Grimy cadantine", "", "Cadantine", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Cadantine", "Vial of water", "Cadantine potion (unf)", 1, 1, 1,0,true));
		}

		// 69
		if(herbloreLevel >= 69) {
			listOfProcessingItems.add(new ProcessingObject("Grimy lantadyme", "", "Lantadyme", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Lantadyme", "Vial of water", "Lantadyme potion (unf)", 1, 1, 1,0,true));
		}

		// 72
		if(herbloreLevel >= 72) {
			listOfProcessingItems.add(new ProcessingObject("Grimy dwarf weed", "", "Dwarf weed", 1, 0, 1,2,false));
			listOfProcessingItems.add(new ProcessingObject("Dwarf weed", "Vial of water", "Dwarf weed potion (unf)", 1, 1, 1,0,true));
		}
		
		
		
		// Fletching Tasks
		listOfProcessingItems.add(new ProcessingObject("Arrow shaft", "Feather", "Headless arrow", 1, 1, 1,4,true));
		if(fletchingLevel >= 10) {
			listOfProcessingItems.add(new ProcessingObject("Bronze dart tip", "Feather", "Bronze dart", 1, 1, 1,5,true));
		}
		if(fletchingLevel >= 22) {
			listOfProcessingItems.add(new ProcessingObject("Iron dart tip", "Feather", "Iron dart", 1, 1, 1,5,true));
		}
		if(fletchingLevel >= 37) {
			listOfProcessingItems.add(new ProcessingObject("Steel dart tip", "Feather", "Steel dart", 1, 1, 1,5,true));	
		}
		if(fletchingLevel >= 52) {
			listOfProcessingItems.add(new ProcessingObject("Mithril dart tip", "Feather", "Mithril dart", 1, 1, 1,5,true));
		}
		if(fletchingLevel >= 55) {
			listOfProcessingItems.add(new ProcessingObject("Maple logs", "Knife", "Maple longbow (u)", 1, 1, 1,6,true));
		}
		if(fletchingLevel >= 70) {
			listOfProcessingItems.add(new ProcessingObject("Yew logs", "Knife", "Yew longbow (u)", 1, 1, 1,6,true));
		}
		if(fletchingLevel >= 85) {
			listOfProcessingItems.add(new ProcessingObject("Magic logs", "Knife", "Magic longbow (u)", 1, 1, 1,6,true));
		}
		
		
		// Other tasks
		listOfProcessingItems.add(new ProcessingObject("Clay", "Bucket of water", "Soft clay", 1, 1, 1,0,true));
		listOfProcessingItems.add(new ProcessingObject("Bird nest", "Pestle and mortar", "Crushed nest", 1, 1, 1,3,true).setItem2Stack(true));
		listOfProcessingItems.add(new ProcessingObject("Volcanic ash", "Supercompost", "Ultracompost", 2, 1, 1,0,true).setItem1Stack(true));
		
		// Things to force items to sell
		listOfProcessingItems.add(new ProcessingObject("Twisted Bow", "Elysian spirit shield", "Bucket", 1, 1, 1,0,true));
		
		return listOfProcessingItems;
	}
	
	public static boolean canDoMethod(String items) {
		String item1 = items.split(",")[0];
		//String item2 = items.split(",")[0];
		for(ProcessingObject method : ItemProcessManager.getListOfProcesses()) {
			if(method.item1.equalsIgnoreCase(item1)) {
				return true;
			}
		}
		
		return false;
	}

	public static int getItem1Total() {
		return item1Total;
	}

	public static void setItem1Total(int item1Total) {
		ItemProcessManager.item1Total = item1Total;
	}

	public static int getItem2Total() {
		return item2Total;
	}

	public static void setItem2Total(int item2Total) {
		ItemProcessManager.item2Total = item2Total;
	}

	public static int getResultTotal() {
		return resultTotal;
	}

	public static void setResultTotal(int resultTotal) {
		ItemProcessManager.resultTotal = resultTotal;
	}
}
