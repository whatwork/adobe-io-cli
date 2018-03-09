package io.adobe.weshopkins.target;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;


public class TargetApi {
	
	private final static String CONTENT_TYPE_TARGET_JSON = "application/vnd.adobe.target.v1+json";
	private static Log log = LogFactory.getLog(TargetApi.class);

	public static JSONObject getActivities(String endpoint, String apiKey, String bearerToken) throws Exception {

		HttpsURLConnection conn = (HttpsURLConnection) new URL(endpoint).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("authorization", "Bearer " + bearerToken );
		conn.setRequestProperty("content-type", CONTENT_TYPE_TARGET_JSON);
		conn.setRequestProperty("x-api-key", apiKey);
		conn.setRequestProperty("cache-control", "no-cache");
				
		conn.setUseCaches(false);
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		
		log.debug("getActivities header");
		for (String key : conn.getRequestProperties().keySet())
		{
			log.debug(key + ": " + conn.getRequestProperty(key));
		}

		int responseCode = conn.getResponseCode();
		
		log.debug("getActivities Sending 'GET' request to URL : " + endpoint);
		log.debug("getActivities Response Code : " + responseCode);		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		log.debug("getActivities Response: " + response.toString());
 
		return new JSONObject(response.toString());
	}

}
