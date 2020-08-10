package scripts.objects;

import java.util.Date;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import scripts.util.Bank;
import scripts.util.GE;
import scripts.util.Network;
import scripts.util.Util;

public class ProcessingObject {
	public String item1 = "";
	public String item2 = "";
	public String result = "";
	
	public int item1Count = 0;
	public int item2Count = 0;
	public int resultCount = 0;
	
	private boolean isItem1Stack = false;
	private boolean isItem2Stack = false;
	
	public int processType = 0;
	
	public boolean isSell = true;
	
	private int MAKE_INTERFACE_ID = 270;
	
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
	
	public boolean inInventory() {
		
		Network.updateSubTask("Process Type: "+processType);
		
		try {
			RSItem item1 = Inventory.find(this.item1)[0];
			RSItem item2 = Inventory.find(this.item2).length != 0 ? Inventory.find(this.item2)[0] : null;
			
			if(item1.getDefinition().isNoted()) {
				Util.log("Item 1 in inventory are noted!!");
				return false;
			}
			if(item2 != null && item2.getDefinition().isNoted()) {
				Util.log("Item 2 in inventory are noted!!");
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		////////////////////////////////////
		// Normal AFK item making
		if(processType == 0 || processType == 6) {
			Network.updateSubTask("Using item together and waiting");
			// Get the array of all the locations of the items in the inventory
			RSItem item1Array[] = Inventory.find(this.item1);
			RSItem item2Array[] = Inventory.find(this.item2);
			
			// Pick a random near the end of the first item and start of the end
			int randomNumItem1 = 0;
			int randomNumItem2 = 0;
			
			//int maxItemsBack = 6;
			
			boolean skipinterface = false;
			
//			if(item1Array.length < 6 || item2Array.length < 6) {
//				maxItemsBack = 0;
//			}
			
			if(item1Array.length == 1 || item2Array.length == 1) {
				skipinterface = true;
			}
			
			try {
//				randomNumItem1 = ThreadLocalRandom.current().nextInt(item1Array.length-maxItemsBack, item1Array.length);
//				randomNumItem2 = ThreadLocalRandom.current().nextInt(0, (item2Array.length-(item2Array.length-maxItemsBack)));
				randomNumItem1 = item1Array.length-1;
				randomNumItem2 = 0;
			}catch(Exception e) {
				// Do something...
			}
			
			// If doing bows
			if(processType == 6) {
				// Set item 1 to the end
				randomNumItem1 = item1Array.length-1;
			}
			
			// Make sure that item exists
			if(randomNumItem1 > item1Array.length) {
				randomNumItem1 = item1Array.length;
			}
			if(randomNumItem2 > item2Array.length) {
				randomNumItem2 = item2Array.length;
			}
			
			RSItem item1 = null;
			RSItem item2 = null;
			
			try {
				item1 = item1Array[randomNumItem1];
				item2 = item2Array[randomNumItem2];
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			
			
			item1.click("use");
			Util.randomSleep();
			item2.click("use");
			Util.randomSleep();
			
			// Wait for the interface to open
			long endTime = new Date().getTime() + 10000L;
			System.out.println("Looking for make interface...");
			while(!skipinterface) {
				if(Interfaces.get(MAKE_INTERFACE_ID) != null) {
					break;
				}else {
					Util.randomSleep();
				}
				
				// If the current time is larger than the end time
				if(new Date().getTime() > endTime) {
					Util.log("Waited long enough");
					return false;
				}
			}
			
			Util.randomSleepRange(500, 1000);
			
			if(Util.randomNumber(0, 100) > 70) {
				Mouse.leaveGame(true);
			}else {
				Bank.hoverBank();
			}
			
			
			
			
			// Spam space until the make interface is gone
			endTime = new Date().getTime() + 20000L;
			Util.log("Spamming SPACE now");
			while(true) {
				
				if(processType == 0) {
					Keyboard.sendPress(' ',32);
					Util.randomSleep();
					Keyboard.sendRelease(' ',32);
				}else if(processType == 6) {
					Keyboard.sendType('3');
					Util.randomSleep();
				}
				
				
				// If the current time is larger than the end time
				if(new Date().getTime() > endTime) {
					Util.log("Waited long enough");
					return false;
				}
				
				if(Interfaces.get(MAKE_INTERFACE_ID) == null) {
					break;
				}
			}
			
			//Mouse.leaveGame(true);
			
			
			// wait for done or level up
			endTime = new Date().getTime() + 30000L;
			if(processType == 6) {
				endTime = new Date().getTime() + 60000L;
			}
			
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
					Util.log("Waited long enough");
					break;
				}
				
			}
		}else if(processType == 1) { // Using items together over and over
			Network.updateSubTask("Using item together over and over");
			
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
			Network.updateSubTask("Cleaning herbs");
			while(Inventory.getCount(this.item1) > 0) {
				RSItem item1Array[] = Inventory.find(this.item1);
				int amountTotal = Inventory.find(this.item1).length;
				
				int numberClicked = 0;
				
				int i = 0;
				
				while(true) {
					
					if(Banking.isBankLoaded()) {
						return false;
					}else if(GE.isInGE()) {
						return false;
					}
					
					int amountLeft = amountTotal - numberClicked;
					
					// if there are not 4 spots forward
					if(amountLeft < 8) {
						
						for(int i2 = 0; i2 < amountLeft; i2++) {
							item1Array[i+i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
							
							// If item is selected
							if(Game.getItemSelectionState() == 1) {
								// click the first item in the inventory
								Inventory.getAll()[0].click();
								Util.randomSleep();
							}
						}
						
						break;
					}else {
						// do 4 forward
						for(int i2 = 0; i2 < 4; i2++) {
							item1Array[i+i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
							
							// If item is selected
							if(Game.getItemSelectionState() == 1) {
								// click the first item in the inventory
								Inventory.getAll()[0].click();
								Util.randomSleep();
							}
						}
						i+= 8;
						
						// do 4 backwards
						for(int i2 = 1; i2 <= 4; i2++) {
							item1Array[i-i2].click("clean");
							//Util.randomSleepRange(20,40);
							numberClicked++;
							
							// If item is selected
							if(Game.getItemSelectionState() == 1) {
								// click the first item in the inventory
								Inventory.getAll()[0].click();
								Util.randomSleep();
							}
						}
					}
					
					
					
				}
				
				Util.randomSleepRange(1000, 3000);
				

				
			}
		}else if(processType == 3) {
			// Use item 2 on the last item 1 over and over
			
			Mouse.setSpeed(300);
			// Loop till there is no more item 1
			while(Inventory.getCount(this.item1) > 0) {
				// if somehow we don't have item 2
				if(Inventory.getCount(this.item2) == 0) {
					break;
				}
				
				// Get item 2
				RSItem item2 = Inventory.find(this.item2)[0];
				// Get the last item 1
				RSItem item1 = Inventory.find(this.item1)[Inventory.find(this.item1).length-1];
				
				item1.click("use");
				item2.click("use");
			}
			Mouse.setSpeed(100);
		}else if(processType == 4) {
			try {
				while(Inventory.find(this.item1).length > 0 && Inventory.find(this.item2).length > 0) {
					// Get item 1 and item 2
					RSItem item1 = Inventory.find(this.item1)[0];
					RSItem item2 = Inventory.find(this.item2)[0];
					
					item1.click("use");
					Util.randomSleep();
					item2.click("use");
					
					Util.randomSleepRange(2000, 3000);
					
					// Spam space until the make interface is gone
					long endTime = new Date().getTime() + 20000L;
					Util.log("Spamming SPACE now");
					while(true) {
						
						Keyboard.sendPress(' ',32);
						Util.randomSleep();
						Keyboard.sendRelease(' ',32);
						
						// If the current time is larger than the end time
						if(new Date().getTime() > endTime) {
							Util.log("Waited long enough");
							return false;
						}
						
						if(Interfaces.get(MAKE_INTERFACE_ID) == null) {
							break;
						}
					}
					
					// wait for done or level up
					endTime = new Date().getTime() + 15000L;
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
				}
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}else if(processType == 5) {
			Mouse.setSpeed(300);
			try {
				while(Inventory.find(this.item1).length > 0 && Inventory.find(this.item2).length > 0) {
					// Get item 2
					RSItem item2 = Inventory.find(this.item2)[0];
					// Get the last item 1
					RSItem item1 = Inventory.find(this.item1)[0];
					
					item1.click("use");
					Util.randomSleep();
					item2.click("use");
				}
			}catch(Exception e) {
				Mouse.setSpeed(100);
				return false;
			}
			Mouse.setSpeed(100);
		}
		
		return true;
	}
	
	public boolean inBank() {
		Network.updateSubTask("Process Type: "+processType);
		Util.randomSleep();
		
		ItemProcessManager.setItem1Total(Banking.find(this.item1).length != 0 ? Banking.find(this.item1)[0].getStack() : 0);
		ItemProcessManager.setItem2Total(Banking.find(this.item2).length != 0 ? Banking.find(this.item2)[0].getStack() : 0);
		ItemProcessManager.setResultTotal(Banking.find(this.result).length != 0 ? Banking.find(this.result)[0].getStack() : 0);
		
		// Making items that take 27 out then 1
		if(processType == 3 || processType == 6) {
			// Take out Item 1
			Banking.withdraw(27, this.item1);
			Banking.withdraw(27, this.item2);
			
			// Make sure we withdrew the proper amount of items
			for(int i = 0; i <= 10; i++) {
				// Check if there's the proper number of items in the inventory
				if(Inventory.getCount(this.item1) == 27 && Inventory.getCount(this.item2) == 1) {
					break;
				}else {
					Util.randomSleepRange(50,200);
				}
				// After 10 sleeps, withdraw the item again
				if(i == 10) {
					if(Inventory.getCount(this.item1) != 27) {
						Banking.withdraw(27, this.item1);
					}
					Util.randomSleep();
					// Check if there's enough of item 2 ONLY if there's not 27 (item 1 was a stack)
					if(Inventory.getCount(this.item2) != 1) {
						Banking.withdraw(27, this.item2);
					}
				}
			}
			
		}else if(processType == 4 || processType == 5){
			while(Banking.find(this.item1).length != 0 || Banking.find(this.item2).length != 0) {
				// Take out Item 1
				Banking.withdraw(0, this.item1);
				Util.randomSleep();
				Banking.withdraw(0, this.item2);
				
				Util.randomSleepRange(2000, 3000);
			}
		}else { // MAKING EVERYTHING ELSE
		
			// Calculate how much of each item to take out
			int totalItems = this.item1Count + this.item2Count;
			
			int amountOfActions = (int)Math.floor(28/totalItems);
			
			//int itemsPerType = (int)Math.floor(28/totalItems);
			int numItem1 = 0;
			int numItem2 = 0;
			
			// Check if one of the items is a stack
			if(this.isItem1Stack) {
				numItem1 = this.item1Count*27;
				numItem2 = this.item1Count*27;
			}else if(this.isItem2Stack) {
				numItem1 = this.item1Count*27;
				numItem2 = this.item2Count*27;
			}else {
				
				numItem1 = amountOfActions * this.item1Count;
				numItem2 = amountOfActions * this.item2Count;
			}
			
			Util.log(this.isItem1Stack + " | " + this.isItem2Stack);
			Util.log("1: "+numItem1 +"      2: "+numItem2);
			
			// Take out Item 1
			Banking.withdraw(numItem1, this.item1);
			Util.randomSleep();
			Banking.withdraw(numItem2, this.item2);

			
			// Make sure we withdrew the proper amount of items
			for(int i = 0; i <= 10; i++) {
				// Check if there's the proper number of items in the inventory
				if(Inventory.getCount(this.item1) == numItem1 && Inventory.getCount(this.item2) == numItem2) {
					break;
				}else {
					Util.randomSleepRange(50,200);
				}
				// After 10 sleeps, withdraw the item again
				if(i == 10) {
					if(Inventory.getCount(this.item1) != numItem1) {
						Banking.withdraw(numItem1, this.item1);
					}
					Util.randomSleep();
					// Check if there's enough of item 2 ONLY if there's not 27 (item 1 was a stack)
					if(Inventory.getCount(this.item2) != numItem2 && Inventory.getCount(this.item2) != 27) {
						Banking.withdraw(numItem2, this.item2);
					}
				}
			}
		}
		
		
		return true;
	}
	

	public ProcessingObject setItem1Stack(boolean value) {
		this.isItem1Stack = value;
		return this;
	}
	public ProcessingObject setItem2Stack(boolean value) {
		this.isItem2Stack = value;
		return this;
	}

}
