package scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

import scripts.util.Network;

public class test {
	public static void main (String args[]) {
		
		System.out.println("test");
		
		try {
			Network.updateAPI(null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
}
