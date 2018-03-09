package io.adobe.weshopkins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import io.adobe.weshopkins.JWT;
import io.adobe.weshopkins.target.TargetApi;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 

/**
 * Hello world!
 *
 */
public class CLI {
	
	private final static String PROPERTIES_FILE_NAME = "adobeio.properties";

	private static Log log = LogFactory.getLog(JWT.class);

	public static void main(String[] args) throws Exception {

		
		Properties prop = new Properties();
		
		InputStream inputStream = new CLI().getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

		if (inputStream != null) {
			prop.load(inputStream);
			log.debug("Loaded props file " + PROPERTIES_FILE_NAME);
		} else {
			throw new FileNotFoundException("property file '" + PROPERTIES_FILE_NAME + "' not found in the classpath");
		}
		
		// API key information from properties file
		String orgId = prop.getProperty("enterprise.organizationId");
		String technicalAccountId = prop.getProperty("enterprise.technicalAccountId");
		String apiKey = prop.getProperty("enterprise.apiKey"); 
		String tenant = prop.getProperty("enterprise.tenant"); 
		String pathToSecretKey = prop.getProperty("enterprise.privateKeyFilename"); 
		String imsHost = prop.getProperty("server.imsHost"); 
		String clientSecret = prop.getProperty("enterprise.clientSecret");
		String apiHost = prop.getProperty("server.apiHost");

		// Get a JWT token 
		String jwtToken = JWT.getJWT(imsHost, orgId, technicalAccountId, apiKey, pathToSecretKey);
		log.debug("JWT:" + jwtToken);
		
		// Convert the JWT token to a Bearer token
		String bearerToken = JWT.getBearerTokenFromJWT(imsHost, apiKey, clientSecret, jwtToken);
		log.debug("Bearer: " + bearerToken);
		
		// Do stuff with Target
		JSONObject activities = TargetApi.getActivities("https://" + apiHost + "/" + tenant + "/target/activities/", apiKey, bearerToken);
		
		System.out.println(activities.toString(1));

	}

	

}
