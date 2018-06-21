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
	
	private String tenant;
	private String clientId;
	
	/* constructor */
	public TargetAPI(String apiHost, String tenant, String clientId, String apiKey, String bearerToken) {
		super(apiHost, apiKey, bearerToken);
		setBaseURL("https://" + apiHost + "/" + tenant);
		this.tenant = tenant;
		this.clientId = clientId;
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
	
	public JSONObject getProfile(String thirdPartyId) throws Exception {

		return doGetRequestJSON("https://" + clientId + ".tt.omtrdc.net/rest/v1/profiles/thirdPartyId/" + thirdPartyId + "?client=" + clientId, CONTENT_TYPE_TARGET_JSON);

	}
	
	
//	public JSONObject setProfile(String thirdPartyId) throws Exception {
//
////		curl -X GET \
////		  'https://adobedemoamericas72.tt.omtrdc.net/m2/adobedemoamericas72/profile/update?mbox3rdPartyId=wehopkin@adobe.com&profile.name=wes&client=adobedemoamericas72'
////
////		return doGetRequestJSON("https://" + clientId + ".tt.omtrdc.net/rest/v1/profiles/thirdPartyId/" + thirdPartyId + "?client=" + clientId, CONTENT_TYPE_TARGET_JSON);
//		
//	}
	
	public JSONObject getServerSideDelivery(String thirdPartyId, String sessionId, JSONObject body) throws Exception {

		return doPostRequestJSON("https://" + clientId + ".tt.omtrdc.net/rest/v1/mbox/" + sessionId + "?client=" + clientId, body);

	}
//	
//	curl -X POST \
//	  'https://<your-tenant-name>.tt.omtrdc.net/rest/v1/mbox/my-session-id?client=<your-tenant-name>' \
//	  -H 'cache-control: no-cache' \
//	  -H 'content-type: application/json' \
//	  -d '{
//	  "mbox" : "l5-mobile-ab"
//	}'
	
	public JSONObject getProfileAttributes() throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/profileattributes/mbox", CONTENT_TYPE_TARGET_JSON);

	}
	
	public JSONObject getMboxes() throws Exception {

		return doGetRequestJSON(getBaseURL() + "/target/mboxes", CONTENT_TYPE_TARGET_JSON);

	}

	public JSONObject getMboxParams(String mboxName) throws Exception {

		return doDeleteRequestJSON(getBaseURL() + "/target/mbox/" + mboxName, CONTENT_TYPE_TARGET_JSON);

	}
	
}
