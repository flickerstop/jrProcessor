package scripts.util;

import org.tribot.api2007.Inventory;

public class Inven {
	public static boolean hasCoins() {
		if(Inventory.find("Coins").length == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	public static int countCoins() {
		if(!hasCoins()) {
			return -1;
		}
		return Inventory.find("Coins")[0].getStack();
	}
}
