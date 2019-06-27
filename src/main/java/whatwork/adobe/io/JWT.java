package whatwork.adobe.io;

import io.jsonwebtoken.Jwts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;
import static java.lang.Boolean.TRUE;


public class JWT {

	private static Log log = LogFactory.getLog(JWT.class);

	/**
	 * 
	 * getJWT
	 * 
	 * Creates a JWT request and signs it with the secret key
	 * 
	 * @param imsHost
	 * @param orgId
	 * @param technicalAccountId
	 * @param apiKey
	 * @param pathToSecretKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public static String getJWT(String imsHost, String orgId, String technicalAccountId, String apiKey,
			String pathToSecretKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

		// Sample JWT creation. The example uses SHA256withRSA signature
		// algorithm.

		// Expiration time in seconds; now + a day.
		Long expirationTime = new Date().getTime() / 1000 + 86400L;

		// Metascopes associated to key
		String metascopes[] = new String[] { "ent_marketing_sdk" };

		// Secret key as byte array. Secret key file should be in DER encoded
		// format.
		byte[] privateKeyFileContent = Files.readAllBytes(Paths.get(pathToSecretKey));

		// Create the private key
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		KeySpec ks = new PKCS8EncodedKeySpec(privateKeyFileContent);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);

		// Create JWT payload
		Map<String, Object> jwtClaims = new HashMap<>();
		jwtClaims.put("iss", orgId);
		jwtClaims.put("sub", technicalAccountId);
		jwtClaims.put("exp", expirationTime);
		jwtClaims.put("aud", "https://" + imsHost + "/c/" + apiKey);
		for (String metascope : metascopes) {
			jwtClaims.put("https://" + imsHost + "/s/" + metascope, TRUE);
		}

		// Create the final JWT token
		String jwtToken = Jwts.builder().setClaims(jwtClaims).signWith(RS256, privateKey).compact();

		return jwtToken;
	}

	
	/**
	 * 
	 * getBearerTokenFromJWT
	 * 
	 * Calls Adobe IO with the JWT token, gets back a bearer token
	 * for use in later API calls.
	 * 
	 * @param imsHost
	 * @param clientId
	 * @param clientSecret
	 * @param jwtToken
	 * @return
	 * @throws Exception
	 */
	public static String getBearerTokenFromJWT(String imsHost, String clientId, String clientSecret, String jwtToken)
			throws Exception {

		String endpoint = "https://" + imsHost + "/ims/exchange/jwt/";
		HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();

		byte[] data = ("client_id=" + clientId + "&client_secret=" + clientSecret + "&jwt_token=" + jwtToken)
				.getBytes();

		// set up connection
		conn.setRequestMethod("POST");
		conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("cache-control", "no-cache");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));

		// post data
		
		log.debug("getBearerTokenFromJWT Sending 'POST' request to URL : " + endpoint);
		OutputStream os = conn.getOutputStream();
		os.write(data);
		os.flush();
		os.close();

		// check response
		int responseCode = conn.getResponseCode();
		log.debug("getBearerTokenFromJWT Response Code : " + responseCode);

		// read body of response
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		
		// Convert response to JSON object
		log.debug("getBearerTokenFromJWT response: " + response.toString());
		JSONObject myResponse = new JSONObject(response.toString());

		// get access token from response
		return myResponse.getString("access_token");

	}
}