package scripts.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;

import scripts.objects.ItemProcessManager;
import scripts.objects.ProcessingObject;

public class Network {

	//private String urlStart = "http://192.168.2.32"; // LAPTOP
	//private static String urlStart = "http://192.168.2.63"; // DESKTOP
	private static String urlStart = "http://flickerstop.com"; // BANK
	
	private static long startTime = 0L;
	
	private static String version = "v1.00";
	
	public static String[] getNextItem() {
		
		int fletchingLevel = Skills.getCurrentLevel(Skills.SKILLS.FLETCHING);
		
		String url = urlStart+"/get/jrprocessor/";
		
		String data;
		try {
			data = getHTML(url);
		} catch (Exception e) {
			System.out.println("Failed to grab new task!");
			return new String[]{"Grimy ranarr weed","Vial of water"};
		}
		
		String methods[] = data.split("\\|");
		
		int attempt = 0;
		// Do 5 attempts at getting an item
		while(attempt <= 5) {
			// Loop through all the passed methods
			for(String method : methods) {
				Util.log("Trying method: " + method);
				// 20% chance to skip
				int randomNumber = ThreadLocalRandom.current().nextInt(0, 101);
				if(randomNumber <= 50) {
					Util.log("Method randomly skipped!");
					continue;
				}
				
				if(ItemProcessManager.canDoMethod(method)) {
					String methodItem1 = method.split(",")[0];
					// If we can do yew longs but rolled maple, just do yew
					if(fletchingLevel >= 70 && methodItem1.equalsIgnoreCase("Maple logs")) {
						return new String[]{"Yew logs","null"};
					}
					// If we can do magic longs but rolled maple/yew, just do magic
					if(fletchingLevel >= 85 && (methodItem1.equalsIgnoreCase("Maple logs")||methodItem1.equalsIgnoreCase("Yew logs"))) {
						return new String[]{"Magic logs","null"};
					}
					
					return method.split(",");
				}else {
					Util.log("Unable to do this method.");
				}
			}
			attempt++;
		}
		
		return new String[]{"Bird nest","null"};
	}
	
	public static String[] getNextMuleTarget() throws Exception {
		String data = getHTML(urlStart+"/post/jrprocessorMuleData");
		// FIXME
		//String data = getHTML("http://192.168.2.32"+"/post/jrprocessorMuleData");
		
		if(data.equalsIgnoreCase("none")) {
			return null;
		}
		
		Util.log("Target data: " + data);
		
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
		
		int herbloreLevel = Skills.getCurrentLevel(Skills.SKILLS.HERBLORE);
		int fletchingLevel = Skills.getCurrentLevel(Skills.SKILLS.FLETCHING);
		
		String position = "x:"+Player.getRSPlayer().getPosition().getX()+" y:"+Player.getRSPlayer().getPosition().getY();
				
		
		
		
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
        params.put("version",version);
        params.put("herblore", herbloreLevel);
        params.put("fletching", fletchingLevel);
        params.put("position", position);
        params.put("startTime", startTime);
        params.put("world", WorldHopper.getWorld());

        post(params);
	}
	
	public static void updateMuleData() throws Exception {
		int plat = Inventory.find("Platinum token").length != 0 ? Inventory.find("Platinum token")[0].getStack() : 0;
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("plat", plat);
        params.put("type","mule");

        post(params);
	}
	
	public static void updateMuleTask(String task) throws Exception {
		//int plat = Inventory.find("Platinum token").length != 0 ? Inventory.find("Platinum token")[0].getStack() : 0;
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","muleTask");
        params.put("task",task);

        post(params);
	}
	
	public static void updateBotSubTask(String task){
		Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","taskUpdate");
        params.put("task", task);

        try {
			post(params);
		} catch (Exception e) {
			Util.log("Error updating sub task");
			e.printStackTrace();
		}
	}
	
	public static void updatePosition() throws Exception {
		String position = "x:"+Player.getRSPlayer().getPosition().getX()+" y:"+Player.getRSPlayer().getPosition().getY();
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("position", position);
        params.put("type","position");

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
	
	public static void announceWaitingForMule() throws Exception {
		//int plat = Banking.find("Platinum token").length != 0 ? Banking.find("Platinum token")[0].getStack() : 0;
		
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("type","waitingForMule");
        //params.put("plat", plat);

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
		
		//FIXME
		URL url = new URL (urlStart+"/post/jrprocessor");
		//URL url = new URL ("http://192.168.2.63"+"/post/jrprocessor");
		
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
		
		startTime = new Date().getTime();
		
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
