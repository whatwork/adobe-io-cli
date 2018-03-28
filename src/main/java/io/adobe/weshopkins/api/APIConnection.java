package io.adobe.weshopkins.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

/**
 * APIConnection
 * 
 * Connection class.  Does the actual GET, DELETE, POST, etc... operations
 * 
 * @author wehopkin
 *
 */
public abstract class APIConnection {
		
	private String apiHost;
	private String apiKey;
	private String bearerToken;
	private String baseURL;
	
	public APIConnection(String apiHost, String apiKey, String bearerToken) {
		this.apiHost=apiHost;
		this.apiKey=apiKey;
		this.bearerToken=bearerToken;
		this.setBaseURL("https://" + apiHost);
	}
	
	public JSONObject doGetRequestJSON(String endpoint, String contentType) throws Exception {
		return doRequestJSON(endpoint, "GET", contentType);
	}

	public JSONObject doDeleteRequestJSON(String endpoint, String contentType) throws Exception {
		return doRequestJSON(endpoint,"DELETE", contentType);
	}
	
	private JSONObject doRequestJSON(String endpoint, String method, String contentType) throws Exception {

		/* set up a connection to the Adobe.io API Server */
		HttpsURLConnection conn = (HttpsURLConnection) new URL(endpoint).openConnection();
		conn.setRequestMethod(method);
		
		/* Set the request headers */
		conn.setRequestProperty("authorization", "Bearer " + bearerToken );
		conn.setRequestProperty("content-type", contentType);
		conn.setRequestProperty("x-api-key", apiKey);
		conn.setRequestProperty("cache-control", "no-cache");
		conn.setUseCaches(false);
		
		/* set timeouts */
		// TODO: put values in config file
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		
//		log.debug("getActivities header");
//		for (String key : conn.getRequestProperties().keySet())
//		{
//			log.debug(key + ": " + conn.getRequestProperty(key));
//		}

		int responseCode = conn.getResponseCode();
		
		if (responseCode == 200) /* 200 = OK  */ 
		{
						
			/* read the response from the API call into a string */
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			/* all API responses are JSON, so we might as well make a JSON object and return it */
			return new JSONObject(response.toString());

		} else {
			return new JSONObject("{response: " + responseCode + ", url: \"" + endpoint + "\"}");
		
		}
		
	}
	

	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public String getBaseURL() {
		return baseURL;
	}

	protected void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}


}
