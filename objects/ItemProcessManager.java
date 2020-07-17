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
		
		listOfProcessingItems.add(new ProcessingObject("Eye of newt", "Guam potion (unf)", "Attack potion(3)", 1, 1, 1,0,false));
		listOfProcessingItems.add(new ProcessingObject("Grimy ranarr weed", "", "Ranarr weed", 1, 0, 1,2,false));
		listOfProcessingItems.add(new ProcessingObject("Ranarr weed", "Vial of water", "Ranarr potion (unf)", 1, 1, 1,0,true));
		listOfProcessingItems.add(new ProcessingObject("Bird nest", "Pestle and mortar", "Crushed nest", 1, 1, 1,3,true).setItem2Stack(true));
		listOfProcessingItems.add(new ProcessingObject("Volcanic ash", "Supercompost", "Ultracompost", 2, 1, 1,0,true).setItem1Stack(true));
		
		return listOfProcessingItems;
	}
}
