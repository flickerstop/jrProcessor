package scripts.util;

import java.util.Calendar;
import java.util.LinkedList;

import org.tribot.api.General;

public class BreakManager {
	private static LinkedList<LinkedList<Integer>> breakSchedule = new LinkedList<LinkedList<Integer>>();
	
	
	public static boolean buildBreakSchedule() {
		Util.log("buildBreakSchedule(): Getting schedule");
		
		String networkSchedule = Network.getBreakSchedule();
		
		if(networkSchedule.equalsIgnoreCase("null")) {
			Util.log("buildBreakSchedule(): Failed to get schedule");
			return false;
		}
		
		Util.log("buildBreakSchedule(): Converting schedule");
		
		
		for(String day : networkSchedule.split("\\],\\[")) {
			LinkedList<Integer> dayArray = new LinkedList<Integer>();
			String dayString = day.replaceAll("[\\[\\]]", "");
			
			
			for(String hour : dayString.split(",")) {
				int hourInt = Integer.parseInt(hour);
				dayArray.add(hourInt);
			}
			
			
			breakSchedule.push(dayArray);
		}
		
		Util.log("buildBreakSchedule(): Schedule Built");
		
		int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
		int hourOfTheDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Util.log("Current Day:" + dayOfTheWeek);
		Util.log("Current Hour:" + hourOfTheDay);
		
		return true;
	}
	
	
	public static void outputSchedule() {
		//Calendar calendar = Calendar.getInstance();
		int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
		int hourOfTheDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		
		Util.log("Current Day:" + dayOfTheWeek);
		Util.log("Current Hour:" + hourOfTheDay);
		
		int dayNum = 0;
		for(LinkedList<Integer> day : breakSchedule) {
			String output = "[";
			int hourNum = 0;
			for(int hour : day) {
				if(dayNum == dayOfTheWeek && hourNum == hourOfTheDay) {
					output += "**"+hour + "**, ";
				}else {
					output += hour + ", ";
				}
				
				hourNum++;
			}
			Util.log(output + "]"); 
			dayNum++;
		}
	}
	
	public static int getCurrentTask() {
		// Monday 0, Sunday 6
		int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
		int hourOfTheDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		return breakSchedule.get(dayOfTheWeek).get(hourOfTheDay);
	}
	
	public static int getDay() {
		return  Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
	}
	
	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
}
