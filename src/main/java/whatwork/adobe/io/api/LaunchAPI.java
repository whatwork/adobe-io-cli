package whatwork.adobe.io.api;

import org.json.JSONObject;

/**
 * 
 * LaunchAPI
 * 
 * Implements calls to the Launch services on Adobe IO.
 * 
 * @author wehopkin
 *
 */
public class LaunchAPI extends APIConnection{
	
	private final static String CONTENT_TYPE_LAUNCH_JSON = "application/vnd.api+json";
	
	/* constructor */
	public LaunchAPI(String launchHost, String apiKey, String bearerToken) {
		super(launchHost, apiKey, bearerToken);
		setBaseURL("https://" + launchHost );		
	}

	/*
	 * Returns the profile by a given email address
	 */
	public void demoKitchenSink() throws Exception {

		JSONObject companies = getCompanies();
		System.out.print(companies.toString(4));
		
//		JSONArray data = companies.getJSONArray("data");
//		data.forEach(item -> {
//		    JSONObject obj = (JSONObject) item;
//		    System.out.println(obj.getString("id"));
//		});
	
	}

	public JSONObject getCompanies() throws Exception{
		return doGetRequestJSON(getBaseURL() + "/companies?page[size]=999", CONTENT_TYPE_LAUNCH_JSON);
		
		

	}

}
