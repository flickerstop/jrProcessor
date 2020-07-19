package scripts.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSItem;

import scripts.objects.ProcessingObject;

public class Network {

	//private String urlStart = "http://192.168.2.32"; // LAPTOP
	//private static String urlStart = "http://192.168.2.63"; // DESKTOP
	private static String urlStart = "http://flickerstop.com"; // BANK
	
	public static String[] getNextItem() {
		
		String url = urlStart+"/get/jrprocessor/";
		
		String data;
		try {
			data = getHTML(url);
		} catch (Exception e) {
			System.out.println("Failed to grab new task!");
			return new String[]{"Grimy ranarr weed","Vial of water"};
		}
		
		String methods[] = data.split("\\|");
		
		
		// Loop till we find a method we can do
		while(true) {
			// Get a random method from the list
			int randomNumber = ThreadLocalRandom.current().nextInt(0, methods.length);
			String[] method = methods[randomNumber].split(",");
			
			Util.log("Trying to make: " + method[0]);
			
			if(method[0].equalsIgnoreCase("Grimy toadflax") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 34) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy irit leaf") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 45) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy avantoe") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 50) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy kwuarm") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 55) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy snapdragon") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 63) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy cadantine") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 66) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy lantadyme") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 69) {
				continue;
			}else if(method[0].equalsIgnoreCase("Grimy dwarf weed") && Skills.getCurrentLevel(Skills.SKILLS.HERBLORE) < 72) {
				continue;
			}else {
				return methods[randomNumber].split(",");
			}
		}
	}
	
	public static String[] getNextMuleTarget() throws Exception {
		//String data = getHTML(urlStart+"/post/jrprocessorMuleData");
		
		String data = getHTML("http://192.168.2.63"+"/post/jrprocessorMuleData");
		
		if(data.equalsIgnoreCase("none")) {
			return null;
		}
		
		String name = data.split(",")[0].replaceAll("[^\\sa-zA-Z0-9 ]", "");
		String world = data.split(",")[1];
		
		String output[] = {name,world};
		
		return output;
		
	}
	
	private static String getHTML(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		con.disconnect();
		return result.toString();
	}
	
	public static void updateAPI(ProcessingObject process, int totalCashStack, int totalBought) throws Exception {
		int item1 = Banking.find(process.item1).length != 0 ? Banking.find(process.item1)[0].getStack() : 0;
		int item2 = Banking.find(process.item2).length != 0 ? Banking.find(process.item2)[0].getStack() : 0;
		int result = Banking.find(process.result).length != 0 ? Banking.find(process.result)[0].getStack() : 0;
		int coins = Banking.find("Coins").length != 0 ? Banking.find("Coins")[0].getStack() : 0;
		int plat = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		
		
		
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("coins", coins);
        params.put("plat", plat);
        params.put("item1Name", process.item1);
        params.put("item1Count", item1);
        params.put("item2Name", process.item2);
        params.put("item2Count", item2);
        params.put("resultName", process.result);
        params.put("resultCount", result);
        params.put("totalCoins",totalCashStack);
        params.put("totalBought",totalBought);
        params.put("type","update");
        params.put("world", WorldHopper.getWorld());

        post(params);
	}
	
	public static void announceGE() throws Exception {
		int plat = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","ge");
        params.put("plat", plat);

        post(params);
	}
	
	public static void announceBreak() throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","break");

        post(params);
	}
	
	public static void announceCrash() throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","crash");

        post(params);
	}
	
	private static String post(Map<String,Object> params) throws Exception {
		URL url = new URL (urlStart+"/post/jrprocessor");
		
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String,Object> param : params.entrySet()) {
		    if (postData.length() != 0) postData.append('&');
		    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		    postData.append('=');
		    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.setConnectTimeout(2000);
	    conn.getOutputStream().write(postDataBytes);
	    
	    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

	    StringBuilder returnString = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) {
        	returnString.append((char)c);
        }
		
		return returnString.toString();
	}
	
	public static void init() {
		Util.log(Player.getRSPlayer().getName());
		if(Player.getRSPlayer().getName().equalsIgnoreCase("Mathew Kenne") || Player.getRSPlayer().getName().equalsIgnoreCase("Zander Caius")) {
			Util.log("SET SERVER TO LOCAL");
			urlStart = "http://192.168.2.63"; // DESKTOP
			//urlStart = "http://192.168.2.32"; // LAPTOP
		}else {
			Util.log("Server set to remote");
		}
	}

}
