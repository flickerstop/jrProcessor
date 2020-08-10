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

import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.WorldHopper;

import scripts.JrProcessor;
import scripts.objects.ItemProcessManager;

public class Network {

	private static String urlStart = "http://192.168.2.32"; // LAPTOP
	//private static String urlStart = "http://192.168.2.63"; // DESKTOP
	//private static String urlStart = "http://flickerstop.com"; // Release
	
	
	private static String playerName = "";
	private static String scriptName = "";
	
	public static boolean isInit = false;
	
	private static long startTime = 0L;
	
	public static String version = "v1.07";
	
	
	public static String[] getNextItem() {
		
		int fletchingLevel = Skills.getCurrentLevel(Skills.SKILLS.FLETCHING);
		
		String url = urlStart+"/get/bot/jrProcessor/bestTasks";
		
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
		String data = getHTML(urlStart+"/post/bot/muleData");
		
		if(data.equalsIgnoreCase("none")) {
			return null;
		}
		
		Util.log("Target data: " + data);
		
		String name = data.split(",")[0].replaceAll("[^\\sa-zA-Z0-9 ]", "");
		String world = data.split(",")[1];
		
		String output[] = {name,world};
		
		return output;
		
	}
	
	public static String getServerStatus(){
		String data = "null";
		try {
			data = getHTML(urlStart+"/get/bot/serverCheck");
		} catch (Exception e) {
			Util.log("getServerStatus(): ERROR");
			e.printStackTrace();
			return "null";
		}
		
		if(data.equalsIgnoreCase("none")) {
			return "null";
		}
		
		Util.log("getServerStatus(): "+data);
		
		
		return data;
		
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
	
	public static void updateJrProcessor() throws Exception {
		int herbloreLevel = Skills.getCurrentLevel(Skills.SKILLS.HERBLORE);
		int fletchingLevel = Skills.getCurrentLevel(Skills.SKILLS.FLETCHING);
		int magicLevel = Skills.getCurrentLevel(Skills.SKILLS.MAGIC);
		
		String position = "x:"+Player.getRSPlayer().getPosition().getX()+" y:"+Player.getRSPlayer().getPosition().getY();
				
		
		
		
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("coins", Bank.gpInBank);
        params.put("maxCoins", Bank.getMaxGPInBank());
        params.put("plat", Bank.platInBank);
        params.put("herblore", herbloreLevel);
        params.put("fletching", fletchingLevel);
        params.put("magic", magicLevel);
        params.put("position", position);
        params.put("startTime", startTime);
        params.put("world", WorldHopper.getWorld());
        params.put("mainTask", JrProcessor.getStateString());
        
        
    	params.put("item1Name", JrProcessor.currentProcess == null ? null : JrProcessor.currentProcess.item1);
        params.put("item1Count", JrProcessor.currentProcess == null ? null : ItemProcessManager.getItem1Total());
        params.put("item2Name", JrProcessor.currentProcess == null ? null : JrProcessor.currentProcess.item2);
        params.put("item2Count", JrProcessor.currentProcess == null ? null : ItemProcessManager.getItem2Total());
        params.put("resultName", JrProcessor.currentProcess == null ? null : JrProcessor.currentProcess.result);
        params.put("resultCount", JrProcessor.currentProcess == null ? null : ItemProcessManager.getResultTotal());

        post(params,"/jrProcessorUpdate");
	}
	
	public static void updateMainTask(String task){
		Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("task", task);

        try {
			post(params,"/mainTask");
		} catch (Exception e) {
			Util.log("Error updating main task");
			e.printStackTrace();
		}
	}
	
	public static void updateSubTask(String task){
		Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("task", task);

        try {
			post(params,"/subTask");
		} catch (Exception e) {
			Util.log("Error updating sub task");
			e.printStackTrace();
		}
	}
	
	public static void updatePosition() throws Exception {
		String position = "x:"+Player.getRSPlayer().getPosition().getX()+" y:"+Player.getRSPlayer().getPosition().getY();
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("position", position);

        post(params,"/position");
	}
	
	
	public static void announceBreakStart() throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);

        post(params,"/breakStart");
	}
	
	public static void sendLog(String logData) throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("logString", logData);

        post(params,"/log");
	}
	
	public static void announceBreakEnd() throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);

        post(params,"/breakEnd");
	}
	
	public static void announceCrash() throws Exception {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);

        post(params,"/crash");
	}
	
	private static String post(Map<String,Object> params,String postPath) throws Exception {
		
		URL url = new URL (urlStart+"/post/bot"+postPath);
		
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
	
	public static void init(String newScriptName) {
		
		startTime = new Date().getTime();
		
		playerName = Player.getRSPlayer().getName();
		scriptName = newScriptName;
		
		String membershipLeft = Util.getMembershipLeft();

		Util.log("Server set to remote");
		
		Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", playerName);
        params.put("script", scriptName);
        params.put("membership",membershipLeft);
        params.put("startTime",startTime);
        params.put("version",version);

        try {
			post(params,"/init");
			isInit = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
