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
}
