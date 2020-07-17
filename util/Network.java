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
import org.tribot.api2007.types.RSItem;

import scripts.objects.ProcessingObject;

public class Network {

	//private String urlStart = "http://192.168.2.32"; // LAPTOP
	//private static String urlStart = "http://192.168.2.63"; // DESKTOP
	private static String urlStart = "http://www.flickerstop.com"; // BANK
	
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
		

		int randomNumber = ThreadLocalRandom.current().nextInt(0, methods.length);
		
		return methods[randomNumber].split(",");
		
		
		
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
		
		RSItem item1 = Banking.find(process.item1)[0];
		RSItem item2 = Banking.find(process.item2).length != 0 ? Banking.find(process.item2)[0] : null;
		RSItem result = Banking.find(process.result)[0];
		RSItem coins = Banking.find("Coins")[0];
		
		URL url = new URL (urlStart+"/post/jrprocessor");
		
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("name", Player.getRSPlayer().getName());
        params.put("coins", coins.getStack());
        params.put("item1Name", process.item1);
        params.put("item1Count", item1.getStack());
        params.put("item2Name", process.item2);
        params.put("item2Count", item2 != null ? item2.getStack() : 0);
        params.put("resultName", process.result);
        params.put("resultCount", result.getStack());
        params.put("totalCoins",totalCashStack);
        params.put("totalBought",totalBought);

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

        for (int c; (c = in.read()) >= 0;)
            System.out.print((char)c);
		
		
	}
	
	public static void init() {
		if(Player.getRSPlayer().getName().equalsIgnoreCase("Albert Linco")) {
			Util.log("SET SERVER TO LOCAL");
			urlStart = "http://192.168.2.63"; // DESKTOP
			// urlStart = "http://192.168.2.32"; // LAPTOP
		}else {
			Util.log("Server set to remote");
		}
	}

}
