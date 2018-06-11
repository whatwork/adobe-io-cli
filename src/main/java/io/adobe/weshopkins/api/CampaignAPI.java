package io.adobe.weshopkins.api;


import org.json.JSONObject;

/**
 * 
 * CampaignAPI
 * 
 * Implements calls to the Campaign services on Adobe IO.
 * 
 * @author wehopkin
 *
 */
public class CampaignAPI extends APIConnection{
	
	private final static String CONTENT_TYPE_CAMPAIGN_JSON = "application/vnd.adobe.campaign.v1+json";
	
	/* constructor */
	public CampaignAPI(String apiHost, String tenant, String apiKey, String bearerToken) {
		super(apiHost, apiKey, bearerToken);
		setBaseURL("https://" + apiHost + "/" + tenant);		
	}

	/*
	 * Returns the profile by a given email address
	 */
	public JSONObject getProfile(String emailAddress) throws Exception {

		return doGetRequestJSON(getBaseURL() + "/campaign/profileAndServices/profile/byEmail?email=" + emailAddress, CONTENT_TYPE_CAMPAIGN_JSON);

	}

	public JSONObject getProfiles() throws Exception {

		return doGetRequestJSON(getBaseURL() + "/campaign/profileAndServices/profile", CONTENT_TYPE_CAMPAIGN_JSON);

	}

}
