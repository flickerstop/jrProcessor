package scripts.util;

import java.util.Arrays;
import java.util.LinkedList;

public class PredefinedStateOrders {

	public static LinkedList<Integer> setQuestingStart() {
		LinkedList<Integer> newList = new LinkedList<Integer>();
		newList.addAll(Arrays.asList(
				1001,	// Set Objective as questing
				900,	// Turn off roofs
				51,		// Mark as doing work
				100,	// Open bank in lumby castle
				14,		// Deposit all items
				301,	// Deposit equipment
				104,	// Look for imp catcher items
				101,	// Take out herblore quest items
				11,		// Close the bank
				102,	// Wear herb quest equipment
				131,	// Teleport to burthrope
				120,	// Walk to kaqemeex
				110,	// Talk to Kaqemeex (Start Quest)
				121,	// Walk to Sanfew
				111,	// Talk to Sanfew (1st time)
				125,	// Leave Sanfew house
				122,	// Walk to blue dragon dungeon
				123,	// Walk through gate
				103,	// Use items on cauldron
				131,	// Teleport to burthrope
				121,	// Walk to Sanfew
				112,	// Talk to Sanfew (2nd time)
				125,	// Leave Sanfew house
				120,	// Walk to Kaqemeex
				113,	// Talk to Kaqemeex (End Quest)
				130,	// Teleport to GE
				1000,	// Set objective as making pots
				52		// Mark as not doing work
			));
		
		return newList;
	}
	
	public static LinkedList<Integer> setQuestingWithImpCatcher() {
		LinkedList<Integer> newList = new LinkedList<Integer>();
		newList.addAll(Arrays.asList(
				105,	// Take out imp catcher items
				101,	// Take out herblore quest items
				11,		// Close the bank
				106,	// Wear imp catcher equipment
				132,	// Teleport to wizard tower
				126,	// Walk to wizard mizgog
				114,	// Talk to wizard mizgog
				102,	// Wear herb quest equipment
				131,	// Teleport to burthrope
				120,	// Walk to kaqemeex
				110,	// Talk to Kaqemeex (Start Quest)
				121,	// Walk to Sanfew
				111,	// Talk to Sanfew (1st time)
				125,	// Leave Sanfew house
				122,	// Walk to blue dragon dungeon
				123,	// Walk through gate
				103,	// Use items on cauldron
				131,	// Teleport to burthrope
				121,	// Walk to Sanfew
				112,	// Talk to Sanfew (2nd time)
				125,	// Leave Sanfew house
				120,	// Walk to Kaqemeex
				113,	// Talk to Kaqemeex (End Quest)
				130,	// Teleport to GE
				1000,	// Set objective as making pots
				52		// Mark as not doing work
			));
		
		return newList;
	}
	
	public static LinkedList<Integer> startLevelingCooking() {
		LinkedList<Integer> newList = buyFishForCooking();
		newList.addAll(Arrays.asList(
				11,		// Close the bank
				131,	// Teleport to burthrope
				900,	// Make sure rooftops are off
				200		// Walk to rogues den
			));
		
		return newList;
	}
	
	public static LinkedList<Integer> buyFishForCooking() {
		LinkedList<Integer> newList = new LinkedList<Integer>();
		newList.addAll(Arrays.asList(
				1002,	// Set objective as leveling cooking
				51,		// Mark as doing work
				10,		// Open the bank
				14,		// Deposit items
				273,	// Take out fish
				18,		// Take out coins
				11,		// Close bank
				1,		// Open GE
				4,		// Cancel all offers
				6,		// Collect all offers
				270,	// Buy fish for leveling up
				6,		// Wait and collect fish
				2,		// Close GE
				10,		// Open bank
				14,		// Deposit all items
				272		// Make sure we have enough fish in the bank
			));
		
		return newList;
	}
	
	
	
}
