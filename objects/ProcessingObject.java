package scripts.objects;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import scripts.util.Util;

public class ProcessingObject {
	public String item1 = "";
	public String item2 = "";
	public String result = "";
	
	public int item1Count = 0;
	public int item2Count = 0;
	public int resultCount = 0;
	
	public int processType = 0;
	
	public boolean isSell = true;
	
	/**
	 * Creates an object for processing
	 * @param i1 Name of Item 1
	 * @param i2 Name of Item 2
	 * @param r  Name of Resulting item
	 * @param c1 Number of item 1 to use
	 * @param c2 Number of item 2 to use
	 * @param rc Number of results made
	 * @param pt Type of process to use (0 is default)
//	 * @param is is result to be sold?
	 */
	public ProcessingObject(String i1,String i2,String r,int c1,int c2,int rc, int pt,boolean is) {
		this.item1 = i1;
		this.item2 = i2;
		this.result = r;
		this.item1Count = c1;
		this.item2Count = c2;
		this.resultCount = rc;
		this.processType = pt;
		this.isSell = is;
	}
	
	public void inInventory() {
		
		////////////////////////////////////
		// Normal AFK item making
		if(processType == 0) {
			// Get the array of all the locations of the items in the inventory
			RSItem item1Array[] = Inventory.find(this.item1);
			RSItem item2Array[] = Inventory.find(this.item2);
			
			// Pick a random near the end of the first item and start of the end
			int randomNumItem1 = 0;
			int randomNumItem2 = 0;
			
			int maxItemsBack = 6;
			
			boolean skipinterface = false;
			
			if(item1Array.length < 6 || item2Array.length < 6) {
				maxItemsBack = 0;
			}
			
			if(item1Array.length == 1 || item2Array.length == 1) {
				skipinterface = true;
			}
			
			try {
				randomNumItem1 = ThreadLocalRandom.current().nextInt(item1Array.length-maxItemsBack, item1Array.length);
				randomNumItem2 = ThreadLocalRandom.current().nextInt(0, (item2Array.length-(item2Array.length-maxItemsBack)));
			}catch(Exception e) {
				// Do something...
			}
			
			// Make sure that item exists
			if(randomNumItem1 > item1Array.length) {
				randomNumItem1 = item1Array.length;
			}
			if(randomNumItem2 > item2Array.length) {
				randomNumItem2 = item2Array.length;
			}
			
			RSItem item1 = item1Array[randomNumItem1];
			RSItem item2 = item2Array[randomNumItem2];
			
			
			item1.click("use");
			Util.randomSleepRange(50,200);
			item2.click("use");
			Util.randomSleep();
			
			// Wait for the interface to open
			while(!skipinterface) {
				System.out.println("Looking for make interface...");
				if(Interfaces.findWhereAction("Make") != null) {
					break;
				}else {
					Util.randomSleep();
				}
			}
			
			Keyboard.sendPress(' ',32);
			Util.randomSleep();
			Mouse.leaveGame(true);
			
			// wait for done or level up
			long endTime = new Date().getTime() + 30000L;
			System.out.println("Looking for level up or finished processing...");
			while(true) {
				int numItem2 = Inventory.getCount(this.item2);
				int numItem1 = Inventory.getCount(this.item1);
				if(Interfaces.get(233) != null) {
					break;
				}else if(numItem1 == 0 || numItem2 == 0){
					break;
				}else {
					Util.randomSleep();
				}
				
				
				// If the current time is larger than the end time
				if(new Date().getTime() > endTime) {
					break;
				}
				
			}
		}else if(processType == 1) { // Using items together over and over
			
			// Loop while item 1 still exists
			while(Inventory.getCount(this.item1) > 0) {
				RSItem item1Array[] = Inventory.find(this.item1);
				RSItem item2Array[] = Inventory.find(this.item2);
				
				
				for(int i = 0; i < item1Array.length; i++) {
					// Check if everything is done
					if(Inventory.getCount(this.item1) == 0) {
						break;
					}
					
					item1Array[item1Array.length-1-i].click("use");
					Util.randomSleepRange(50,200);
					item2Array[i].click("use");
					
					Util.randomSleepRange(50,100);
				}
				Util.randomSleepRange(1000, 2000);;
				
			}
		}else if(processType == 2) { // cleaning herbs
			while(Inventory.getCount(this.item1) > 0) {
				RSItem item1Array[] = Inventory.find(this.item1);
				int amountTotal = Inventory.find(this.item1).length;
				
				int numberClicked = 0;
				
				int i = 0;
				
				while(true) {
					int amountLeft = amountTotal - numberClicked;
					
					// if there are not 4 spots forward
					if(amountLeft < 8) {
						
						for(int i2 = 0; i2 < amountLeft; i2++) {
							item1Array[i+i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
						}
						
						break;
					}else {
						// do 4 forward
						for(int i2 = 0; i2 < 4; i2++) {
							item1Array[i+i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
						}
						i+= 8;
						
						// do 4 backwards
						for(int i2 = 1; i2 <= 4; i2++) {
							item1Array[i-i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
						}
					}
					
					
					
				}
				
				Util.randomSleepRange(1000, 3000);
				

				
			}
		}
	}
	
	public boolean inBank() {
		Util.randomSleep();
		
		// Calculate how much of each item to take out
		int totalItems = this.item1Count + this.item2Count;
		
		int itemsPerType = (int)Math.floor(28/totalItems);
		
		
		// Take out Item 1
		Banking.withdraw(itemsPerType, this.item1);
		Banking.withdraw(itemsPerType, this.item2);

		
		// Make sure we withdrew the proper amount of items
		for(int i = 0; i <= 10; i++) {
			// Check if there's the proper number of items in the inventory
			if(Inventory.getCount(this.item1) == itemsPerType && Inventory.getCount(this.item2) == itemsPerType) {
				break;
			}else {
				Util.randomSleepRange(50,200);
			}
			// After 10 sleeps, withdraw the item again
			if(i == 10) {
				if(Inventory.getCount(this.item1) != itemsPerType) {
					Banking.withdraw(itemsPerType, this.item1);
				}
				if(Inventory.getCount(this.item2) != itemsPerType) {
					Banking.withdraw(itemsPerType, this.item2);
				}
			}
		}
		
		// Check if both items have the proper number of items in the inventory
		if(Inventory.getCount(this.item2) != this.item2Count ||
			Inventory.getCount(this.item1) != this.item1Count) {
			return false;
		}
		
		return true;
	}
	


}
