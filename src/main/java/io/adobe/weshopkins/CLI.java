package io.adobe.weshopkins;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import io.adobe.weshopkins.JWT;
import io.adobe.weshopkins.Constants;
import io.adobe.weshopkins.api.CampaignAPI;
import io.adobe.weshopkins.api.TargetAPI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 

import java.io.BufferedReader;

/**
 * CLI
 * 
 * Performs setup, parsing, and execution of the command line arguments
 * 
 * @author wehopkin
 *
 */

public class CLI {
	

	

	
	private static Log log = LogFactory.getLog(JWT.class);

	
	
	public static void main(String[] args) throws Exception {
		
	    /* create command line parser */
		CommandLineParser parser = new DefaultParser();
	    CommandLine line = null;
	    Options options = buildCommandLineOptions();
	    
	    try {
	        line = parser.parse( options, args );
	        
	        if (args.length<1 || line.hasOption(Constants.ARG_HELP))
	        	throw new ParseException("No arguments specified");
	    }
	    catch( ParseException exp ) {
	    	
	    	/* error parsing */
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "CLI", options );
	        System.err.println( "\nError: " + exp.getMessage() );
	        return;
	    }

	    Properties prop = new Properties();
	    boolean verbose = line.hasOption(Constants.ARG_VERBOSE);

	    // check for properties file on command line
	    // default to ~/.adobeio.properties
	    String propFileName = line.getOptionValue(Constants.ARG_PROPERTIES, System.getProperty("user.home") + File.separator + Constants.PROPERTIES_FILE_NAME);
	    File propFile = new File(propFileName);
	    
	    if (propFile.exists()) 
	    {
	    	if (verbose) 
	    	{
	    		System.out.println("Loading properties from " + propFile);
	    	}
	    	prop.load(new FileInputStream(propFile));

	    } else {

	    	System.err.println("Warning: no properties file specified nor found in home directory.  Use argument " + Constants.ARG_PROPERTIES_SAMPLE + " for sample props file");

	    }
	    
		
		// API key information from properties file
		String orgId = line.getOptionValue(Constants.ARG_ORG_ID, prop.getProperty("enterprise.organizationId"));
		String technicalAccountId = line.getOptionValue(Constants.ARG_TECH_ID, prop.getProperty("enterprise.technicalAccountId"));
		String apiKey = line.getOptionValue(Constants.ARG_API_KEY,prop.getProperty("enterprise.apiKey")); 
		String tenant = line.getOptionValue(Constants.ARG_TENANT,prop.getProperty("enterprise.tenant"));
		String campaignTenant = line.getOptionValue(Constants.ARG_CAMPAIGN_TENANT,prop.getProperty("enterprise.campaignTenant"));
		String pathToSecretKey = line.getOptionValue(Constants.ARG_PRIV_KEY,prop.getProperty("enterprise.privateKeyFilename")); 
		String imsHost = line.getOptionValue(Constants.ARG_IMS_HOST,prop.getProperty("server.imsHost")); 
		String clientSecret = line.getOptionValue(Constants.ARG_CLIENT_SECRET,prop.getProperty("enterprise.clientSecret"));
		String apiHost = line.getOptionValue(Constants.ARG_API_HOST,prop.getProperty("server.apiHost"));

		if (verbose) {
			System.out.println("orgId:" + orgId);
			System.out.println("technicalAccountId: " + technicalAccountId);
			System.out.println("apiKey: " + apiKey ); 
			System.out.println("tenant: "+  tenant) ; 
			System.out.println("campaignTenant: "+  campaignTenant) ; 
			System.out.println("secret key: "+ pathToSecretKey ); 
			System.out.println("imsHost: " +  imsHost);
			System.out.println("apiHost: " + apiHost);
			System.out.println("clientSecret: " + clientSecret);
		}
		// Get a JWT token 
		String jwtToken = JWT.getJWT(imsHost, orgId, technicalAccountId, apiKey, pathToSecretKey);
		log.debug("JWT:" + jwtToken);
		
		// Convert the JWT token to a Bearer token
		String bearerToken = "";
		if (line.hasOption(Constants.ARG_BEARER_TOKEN))
		{
			bearerToken = line.getOptionValue(Constants.ARG_BEARER_TOKEN);
			log.debug("Bearer specified: " + bearerToken);
		} else {
			bearerToken = JWT.getBearerTokenFromJWT(imsHost, apiKey, clientSecret, jwtToken);
			log.debug("Bearer fetched: " + bearerToken);
		}
		
		// Read STDIN or FILE for POST body (if we have it)
		String postBody = "";
		if (line.hasOption(Constants.ARG_POST_STDIN)) {
			System.out.println("Reading from standard input. Ctrl+D to finish");
			postBody = readInputUntilEOF(new InputStreamReader(System.in, "UTF-8"));
		} else if (line.hasOption(Constants.ARG_POST_FILENAME)) {
			File f = new File(line.getOptionValue(Constants.ARG_POST_FILENAME));
			if (!f.exists() || !f.isFile()) {
				System.err.println("Cannot open " + f.getAbsolutePath() + " for reading.");
				return;
			}
			if (verbose) {
				System.out.println("Reading from " + f.getAbsolutePath());
			}
			postBody = readInputUntilEOF(new FileReader(f));
		}
		
		if (verbose) {
			if (postBody.length()>0) {
				System.out.print(postBody);
				
				// parse it to JSON
//				try {
//					postJSON = new JSONObject(postBody);
//				} catch (JSONException jex) {
//					System.err.println("Cannot parse post argument: " + jex.toString());
//				}
				
				
			} else {
				System.out.println("No POST input");
			}
		}

		TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
		target.setDebug(verbose);

		if (line.hasOption(Constants.ARG_GET_BEARER_TOKEN) || verbose) {
			System.out.println("bearerToken: " + bearerToken);
		} 
		
		if (line.hasOption(Constants.ARG_TARGET_ACTIVITIES)) {

			JSONObject activities = target.getActivities();
			System.out.println(activities.toString(1));
		} 
		if (line.hasOption(Constants.ARG_TARGET_ACTIVITY_XT)) {

			Long activityId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_ACTIVITY_XT));
			JSONObject activities = target.getActivityXT(activityId);
			System.out.println(activities.toString(1));
		}
		if (line.hasOption(Constants.ARG_TARGET_ACTIVITY_AB)) {

			Long activityId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_ACTIVITY_AB));
			JSONObject activities = target.getActivityAB(activityId);
			System.out.println(activities.toString(1));
		}

		if (line.hasOption(Constants.ARG_TARGET_DELETE_XT)) {
			Long activityId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_DELETE_XT));
			JSONObject activities = target.deleteXTActivity(activityId);
			System.out.println(activities.toString(1));
		}
		
		if (line.hasOption(Constants.ARG_TARGET_DELETE_AB)) {

			Long activityId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_DELETE_AB));
			JSONObject activities = target.deleteABActivity(activityId);
			System.out.println(activities.toString(1));
		}

		if (line.hasOption(Constants.ARG_TARGET_AUDIENCES)) {

			JSONObject audiences = target.getAudiences();
			System.out.println(audiences.toString(1));
		}
		
		if (line.hasOption(Constants.ARG_TARGET_AUDIENCE)) {

			JSONObject audiences = target.getAudience(Long.decode(line.getOptionValue(Constants.ARG_TARGET_AUDIENCE)));
			System.out.println(audiences.toString(1));
		}		
		if (line.hasOption(Constants.ARG_TARGET_AUDIENCE_DELETE)) {

			JSONObject audiences = target.deleteAudience(Long.decode(line.getOptionValue(Constants.ARG_TARGET_AUDIENCE)));
			System.out.println(audiences.toString(1));
		}	
		

		if (line.hasOption(Constants.ARG_TARGET_MBOXES)) {

			JSONObject audiences = target.getMboxes();
			System.out.println(audiences.toString(1));
		}
		
		if (line.hasOption(Constants.ARG_TARGET_MBOX_PARAM)) {

			JSONObject params = target.getMboxParams(line.getOptionValue(Constants.ARG_TARGET_MBOX_PARAM));
			System.out.println(params.toString(1));
		}
		
		
		if (line.hasOption(Constants.ARG_TARGET_PROFILE)) {

			JSONObject profile = target.getProfile(tenant, line.getOptionValue(Constants.ARG_TARGET_PROFILE));
			System.out.println(profile.toString(1));
		}		
		
		if (line.hasOption(Constants.ARG_TARGET_PROFILE_ATTRS)) {

			JSONObject profile = target.getProfileAttributes();
			System.out.println(profile.toString(1));
		}	
		
		if (line.hasOption(Constants.ARG_TARGET_OFFERS)) {

			JSONObject offers = target.getOffers();
			System.out.println(offers.toString(1));
		}			
		if (line.hasOption(Constants.ARG_TARGET_OFFER)) {

			Long offerId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_OFFER));
			JSONObject activities = target.getOffer(offerId);
			System.out.println(activities.toString(1));
		}	
		if (line.hasOption(Constants.ARG_TARGET_DELETE_OFFER)) {

			Long offerId = Long.decode(line.getOptionValue(Constants.ARG_TARGET_DELETE_OFFER));
			JSONObject activities = target.deleteOffer(offerId);
			System.out.println(activities.toString(1));
		}
		
		/* -- campaign options -- */
		
		if (line.hasOption(Constants.ARG_CAMPAIGN_PROFILE)) {
			CampaignAPI acs = new CampaignAPI(apiHost, campaignTenant, apiKey, bearerToken);
			JSONObject profile = acs.getProfile(line.getOptionValue(Constants.ARG_CAMPAIGN_PROFILE));
			System.out.println(profile.toString(1));
		}			
		if (line.hasOption(Constants.ARG_CAMPAIGN_PROFILES)) {
			CampaignAPI acs = new CampaignAPI(apiHost, campaignTenant, apiKey, bearerToken);
			JSONObject profile = acs.getProfiles();
			System.out.println(profile.toString(1));
		}		
		
		/* -- app options  --*/
		if (line.hasOption(Constants.ARG_PROPERTIES_SAMPLE)) {
			
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(new CLI().getClass().getClassLoader().getResourceAsStream("adobeio.sample.properties"), "UTF-8"));
			for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
			System.out.println(sb.toString());  
		}	
	}
	
	private static String readInputUntilEOF(Reader reader) {
		
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader systemIn = new BufferedReader(reader);
			String line;
			while((line = systemIn.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			
		}
		return sb.toString();
	}
	
	/**
	 * buildCommandLineOptions
	 * 
	 * return an Options argument to be used by the command line parser
	 * @return
	 */
	private static Options buildCommandLineOptions()
	{
	    /* add all the acceptable command line arguments */
	    Options options = new Options();
	    options.addOption(new Option( Constants.ARG_HELP, "print this message" ));
	    
	    options.addOption(new Option( Constants.ARG_VERBOSE, "Lots of details on what's happening" ));

	    options.addOption(Option.builder(Constants.ARG_ORG_ID)
	    		.hasArg()
                .longOpt(Constants.ARG_ORG_ID_LONG)
                .desc("Organization ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_TECH_ID)
	    		.hasArg()
                .longOpt(Constants.ARG_TECH_ID_LONG)
                .desc("Technical account ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_API_KEY)
	    		.hasArg()
                .desc("API Key" )
                .argName("key")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_TENANT)
	    		.hasArg()
                .desc("Tenant ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_PRIV_KEY)
	    		.hasArg()
                .longOpt(Constants.ARG_PRIV_KEY_LONG)
                .desc("Filename of private key in DER format" )
                .argName("filename")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_IMS_HOST)
	    		.hasArg()
                .longOpt(Constants.ARG_IMS_HOST_LONG)
                .desc("Hostname of IMS Host (FQDN, no protocol)" )
                .argName("hostname")
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_API_HOST)
	    		.hasArg()
                .desc("API Hostname (FQDN, no protocol)" )
                .argName("hostname")
                .build()
                );

	    
	    options.addOption(Option.builder(Constants.ARG_CLIENT_SECRET)
	    		.hasArg()
                .desc("Client Secret" )
                .argName("secret")
                .build()
                );
	   
	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_ACTIVITIES)
                .longOpt(Constants.ARG_TARGET_ACTIVITIES_LONG)
                .desc("Get all target activities" )
                .build()
                );	    

	    options.addOption(Option.builder(Constants.ARG_TARGET_AUDIENCES)
                .longOpt(Constants.ARG_TARGET_AUDIENCES_LONG)
                .desc("Get all target audiences" )
                .build()
                );	 
	    options.addOption(Option.builder(Constants.ARG_PROPERTIES)
	    		.hasArg()
                .desc("Specify properties file.  Default is [home directory]/adobeio.properties" )
                .argName("filename.properties")
                .type(String.class)
                .build()
                );	 	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_AUDIENCE)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_AUDIENCE_LONG)
                .desc("Get a specific target audience" )
                .argName("id")
                .type(Long.class)
                .build()
                );	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_AUDIENCE_DELETE)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_AUDIENCE_DELETE_LONG)
                .desc("Delete a specific target audience" )
                .argName("id")
                .type(Long.class)
                .build()
                );	 	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_DELETE_XT)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_DELETE_XT_LONG)
                .desc("Delete a specific  XT target activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_TARGET_DELETE_AB)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_DELETE_AB_LONG)
                .desc("Delete a specific A/B target activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_TARGET_OFFER)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_OFFER_LONG)
                .desc("Get a specific offer by ID" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_TARGET_OFFERS)
                .longOpt(Constants.ARG_TARGET_OFFERS_LONG)
                .desc("Get all offers" )
                .build()
                );
	    

	    options.addOption(Option.builder(Constants.ARG_TARGET_DELETE_OFFER)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_DELETE_OFFER_LONG)
                .desc("Delete a specific target offer" )
                .argName("id")
                .type(Long.class)
                .build()
                );
	    
	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_ACTIVITY_AB)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_ACTIVITY_AB_LONG)
                .desc("Get a specific A/B Targeting activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );
	    options.addOption(Option.builder(Constants.ARG_TARGET_ACTIVITY_XT)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_ACTIVITY_XT_LONG)
                .desc("Get a specific XT Targeting activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );
	    
	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_MBOXES)
                .longOpt(Constants.ARG_TARGET_MBOXES_LONG)
                .desc("Get all target mboxes" )
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_TARGET_MBOX_PARAM)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_MBOX_PARAM_LONG)
                .desc("Get list of mbox params" )
                .argName("mboxName")
                .type(String.class)
                .build()
                );
	    

	    options.addOption(Option.builder(Constants.ARG_TARGET_PROFILE)
	    		.hasArg()
                .longOpt(Constants.ARG_TARGET_PROFILE_LONG)
                .desc("Get a profile" )
                .argName("thirdPartyId")
                .type(String.class)
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_TARGET_PROFILE_ATTRS)
                .longOpt(Constants.ARG_TARGET_PROFILE_ATTRS_LONG)
                .desc("Retrieve the list of available profile attributes and mbox parameters of type profile" )
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_BEARER_TOKEN)
                .hasArg()
                .longOpt(Constants.ARG_BEARER_TOKEN_LONG)
                .argName("token")                
                .desc("Specify the bearer token instead of fetching from IMS host" )
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_PROPERTIES_SAMPLE)
                .desc("Display sample properties file" )
                .build()
                );

	    
	    options.addOption(Option.builder(Constants.ARG_GET_BEARER_TOKEN)
                .longOpt(Constants.ARG_GET_BEARER_TOKEN_LONG)                
                .desc("Get and print the bearer token" )
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_POST_STDIN)
                .desc("POST body from stdin" )
                .build()
                );	   
	    
	    options.addOption(Option.builder(Constants.ARG_POST_FILENAME)
	    		.hasArg()
                .desc("POST body from file" )
                .argName("filename")
                .type(String.class)
                .build()
                );

	    
	    /* --- campaign options --- */
	    
	    options.addOption(Option.builder(Constants.ARG_CAMPAIGN_TENANT)
	    		.hasArg()
                .longOpt(Constants.ARG_CAMPAIGN_TENANT_LONG)
                .desc("Campaign tenant (as a FQDN)" )
                .argName("campaignTenantId")
                .type(String.class)
                .build()
                );
	    
	    options.addOption(Option.builder(Constants.ARG_CAMPAIGN_PROFILE)
	    		.hasArg()
                .longOpt(Constants.ARG_CAMPAIGN_PROFILE_LONG)
                .desc("Get a profile by email address" )
                .argName("emailAddress")
                .type(String.class)
                .build()
                );

	    options.addOption(Option.builder(Constants.ARG_CAMPAIGN_PROFILES)
                .longOpt(Constants.ARG_CAMPAIGN_PROFILES_LONG)
                .desc("Get all profiles" )
                .build()
                );

	    
	    return options;
	}
	

}