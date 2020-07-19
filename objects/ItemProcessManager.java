package scripts.objects;

import java.util.ArrayList;

import org.tribot.api2007.Banking;

public class ItemProcessManager {
	public static ProcessingObject searchBank() {
			
		for(ProcessingObject process : getListOfProcesses()) {
			if(checkItem(process)) {
				return process;
			}
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
		
		// Herblore tasks
		
		//25
		listOfProcessingItems.add(new ProcessingObject("Grimy ranarr weed", "", "Ranarr weed", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Ranarr weed", "Vial of water", "Ranarr potion (unf)", 1, 1, 1,0,true));
		// 34
		listOfProcessingItems.add(new ProcessingObject("Grimy toadflax", "", "Toadflax", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Toadflax", "Vial of water", "Toadflax potion (unf)", 1, 1, 1,0,true));
		// 45
		listOfProcessingItems.add(new ProcessingObject("Grimy irit leaf", "", "Irit leaf", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Irit leaf", "Vial of water", "Irit potion (unf)", 1, 1, 1,0,true));
		// 50
		listOfProcessingItems.add(new ProcessingObject("Grimy avantoe", "", "Avantoe", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Avantoe", "Vial of water", "Avantoe potion (unf)", 1, 1, 1,0,true));
		// 55
		listOfProcessingItems.add(new ProcessingObject("Grimy kwuarm", "", "Kwuarm", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Kwuarm", "Vial of water", "Kwuarm potion (unf)", 1, 1, 1,0,true));
		// 63
		listOfProcessingItems.add(new ProcessingObject("Grimy snapdragon", "", "Snapdragon", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Snapdragon", "Vial of water", "Snapdragon potion (unf)", 1, 1, 1,0,true));
		// 66
		listOfProcessingItems.add(new ProcessingObject("Grimy cadantine", "", "Cadantine", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Cadantine", "Vial of water", "Cadantine potion (unf)", 1, 1, 1,0,true));
		// 69
		listOfProcessingItems.add(new ProcessingObject("Grimy lantadyme", "", "Lantadyme", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Lantadyme", "Vial of water", "Lantadyme potion (unf)", 1, 1, 1,0,true));
		// 72
		listOfProcessingItems.add(new ProcessingObject("Grimy dwarf weed", "", "Dwarf weed", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Dwarf weed", "Vial of water", "Dwarf weed potion (unf)", 1, 1, 1,0,true));
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Leveling up Fletching
		listOfProcessingItems.add(new ProcessingObject("Arrow shaft", "Feather", "Headless arrow", 1, 1, 1,4,true));
		listOfProcessingItems.add(new ProcessingObject("Bronze dart tip", "Feather", "Bronze dart", 1, 1, 1,5,true));
		listOfProcessingItems.add(new ProcessingObject("Iron dart tip", "Feather", "Iron dart", 1, 1, 1,5,true));
		listOfProcessingItems.add(new ProcessingObject("Steel dart tip", "Feather", "Steel dart", 1, 1, 1,5,true));
		listOfProcessingItems.add(new ProcessingObject("Mithril dart tip", "Feather", "Mithril dart", 1, 1, 1,5,true));
		
		// Fletching Tasks
		listOfProcessingItems.add(new ProcessingObject("Maple logs", "Knife", "Maple longbow (u)", 1, 1, 1,6,true));
		listOfProcessingItems.add(new ProcessingObject("Yew logs", "Knife", "Yew longbow (u)", 1, 1, 1,6,true));
		
		// Other tasks
		listOfProcessingItems.add(new ProcessingObject("Clay", "Bucket of water", "Soft clay", 1, 1, 1,0,true));
		listOfProcessingItems.add(new ProcessingObject("Bird nest", "Pestle and mortar", "Crushed nest", 1, 1, 1,3,true).setItem2Stack(true));
		listOfProcessingItems.add(new ProcessingObject("Volcanic ash", "Supercompost", "Ultracompost", 2, 1, 1,0,true).setItem1Stack(true));
		
		// Things to force items to sell
		listOfProcessingItems.add(new ProcessingObject("Twisted Bow", "Elysian spirit shield", "Bucket", 1, 1, 1,0,true));
		
		return listOfProcessingItems;
	}
}
