package scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test {
	public static void main (String args[]) {
		System.out.println("Running");
		String url = "http://192.168.2.63/get/jrprocessor/";
		try {
			System.out.println(getHTML(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	
	public static String[] getNextItem() {
		
		
		
		
		
		return null;
	}
	public static String getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line);
      }
      rd.close();
      return result.toString();
   }
}
