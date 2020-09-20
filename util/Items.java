package scripts.util;

import java.util.Arrays;
import java.util.LinkedList;

public class Items {

	public static LinkedList<String> gamesNecky(){
		LinkedList<String> newList = new LinkedList<String>();
		newList.addAll(Arrays.asList(
				"Games necklace(8)",
				"Games necklace(7)",
				"Games necklace(6)",
				"Games necklace(5)",
				"Games necklace(4)",
				"Games necklace(3)",
				"Games necklace(2)",
				"Games necklace(1)"));
		
		return newList;
	}
	
	public static LinkedList<String> ringOfWealth(){
		LinkedList<String> newList = new LinkedList<String>();
		newList.addAll(Arrays.asList(
				"Ring of wealth (5)",
				"Ring of wealth (4)",
				"Ring of wealth (3)",
				"Ring of wealth (2)",
				"Ring of wealth (1)"));
		
		return newList;
	}
	
	public static LinkedList<String> necklaceOfPassage(){
		LinkedList<String> newList = new LinkedList<String>();
		newList.addAll(Arrays.asList(
				"Necklace of passage(5)",
				"Necklace of passage(4)",
				"Necklace of passage(3)",
				"Necklace of passage(2)",
				"Necklace of passage(1)"));
		
		return newList;
	}
	
	public static LinkedList<String> amuletOfGlory(){
		LinkedList<String> newList = new LinkedList<String>();
		newList.addAll(Arrays.asList(
				"Amulet of glory(6)",
				"Amulet of glory(5)",
				"Amulet of glory(4)",
				"Amulet of glory(3)",
				"Amulet of glory(2)",
				"Amulet of glory(1)"));
		
		return newList;
	}
	
}
