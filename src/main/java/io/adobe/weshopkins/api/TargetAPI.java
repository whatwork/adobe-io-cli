package io.adobe.weshopkins.api;


import org.json.JSONObject;

/**
 * 
 * TargetAPI
 * 
 * Implements calls to the Target services on Adobe IO.
 * 
 * @author wehopkin
 *
 */
public class TargetAPI extends APIConnection{
	
	private final static String CONTENT_TYPE_TARGET_JSON = "application/vnd.adobe.target.v1+json";
	
	/* constructor */
	public TargetAPI(String apiHost, String tenant, String apiKey, String bearerToken) {
		super(apiHost, apiKey, bearerToken);
		setBaseURL("https://" + apiHost + "/" + tenant);		
	}

	/*
	 * Returns the activities on the a
	 */
	public JSONObject getActivities() throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/activities/", CONTENT_TYPE_TARGET_JSON);

	}
	
	public JSONObject getActivityAB(Long activityId) throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/activities/ab/" + activityId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject getAudiences() throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/audiences", CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject getAudience(Long audienceId) throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/audiences/" + audienceId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	
	public JSONObject getActivityXT(Long activityId) throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/activities/xt/" + activityId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject deleteXTActivity(Long activityId) throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/activities/xt/" + activityId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject deleteABActivity(Long activityId) throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/activities/ab/" + activityId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject getOffers() throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/offers/", CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject getOffer(Long offerId) throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/offers/content/" + offerId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject deleteOffer(Long offerId) throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/offers/content/" + offerId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject deleteAudience(Long audienceId) throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/audiences/" + audienceId.toString(), CONTENT_TYPE_TARGET_JSON);

	}

	
}
