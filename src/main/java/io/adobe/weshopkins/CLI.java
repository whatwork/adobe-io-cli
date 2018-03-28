package io.adobe.weshopkins;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import io.adobe.weshopkins.JWT;
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

/**
 * CLI
 * 
 * Performs setup, parsing, and execution of the command line arguments
 * 
 * @author wehopkin
 *
 */

public class CLI {
	
	private final static String PROPERTIES_FILE_NAME = "adobeio.properties";
	
	private final static String ARG_TARGET_ACTIVITIES = "acts";
	private final static String ARG_TARGET_ACTIVITIES_LONG = "getActivities";
	private final static String ARG_TARGET_ACTIVITY_XT = "xtid";
	private final static String ARG_TARGET_ACTIVITY_XT_LONG = "getActivityXT";
	private final static String ARG_TARGET_ACTIVITY_AB = "abid";
	private final static String ARG_TARGET_ACTIVITY_AB_LONG = "getActivityAB";

	private final static String ARG_TARGET_AUDIENCES = "aud";
	private final static String ARG_TARGET_AUDIENCES_LONG = "getAudiences";
	private final static String ARG_TARGET_AUDIENCE = "auid";
	private final static String ARG_TARGET_AUDIENCE_LONG = "getAudience";
	private final static String ARG_TARGET_AUDIENCE_DELETE = "dau";
	private final static String ARG_TARGET_AUDIENCE_DELETE_LONG = "deleteAudience";

	
	private final static String ARG_TARGET_OFFERS = "offers";
	private final static String ARG_TARGET_OFFERS_LONG = "getOffers";

	private final static String ARG_TARGET_DELETE_OFFER = "doff";
	private final static String ARG_TARGET_DELETE_OFFER_LONG = "deleteOffer";

	private final static String ARG_TARGET_OFFER = "oid";
	private final static String ARG_TARGET_OFFER_LONG = "getOffer";
	
	private final static String ARG_TARGET_DELETE_XT = "dxt";
	private final static String ARG_TARGET_DELETE_XT_LONG = "deleteActivityXT";
	private final static String ARG_TARGET_DELETE_AB = "dab";
	private final static String ARG_TARGET_DELETE_AB_LONG = "deleteActivityAB";
	
	private final static String ARG_ORG_ID = "orgid";
	private final static String ARG_ORG_ID_LONG = "organizationId";
	
	private final static String ARG_TECH_ID = "techid";
	private final static String ARG_TECH_ID_LONG = "technicalAccountId";
	
	private final static String ARG_API_KEY = "apiKey";
	private final static String ARG_TENANT = "tenant";

	
	private final static String ARG_PRIV_KEY = "K";
	private final static String ARG_PRIV_KEY_LONG = "privateKeyFile";
	
	private final static String ARG_IMS_HOST = "ims";
	private final static String ARG_IMS_HOST_LONG = "imsHost";

	private final static String ARG_API_HOST = "apiHost";
	private final static String ARG_CLIENT_SECRET = "clientSecret";
	
	
	private final static String ARG_HELP = "help";
	private final static String ARG_BEARER_TOKEN = "bt";
	private final static String ARG_BEARER_TOKEN_LONG = "bearerToken";

	private final static String ARG_GET_BEARER_TOKEN = "gbt";
	private final static String ARG_GET_BEARER_TOKEN_LONG = "getBearerToken";

	
	private static Log log = LogFactory.getLog(JWT.class);

	
	
	public static void main(String[] args) throws Exception {
		
	    /* create command line parser */
		CommandLineParser parser = new DefaultParser();
	    CommandLine line = null;
	    Options options = buildCommandLineOptions();
	    
	    try {
	        line = parser.parse( options, args );
	        
	        // there is no arg'less invocation of this tool
	        if (args.length<1)
	        	throw new ParseException("No arguments specified");
	    }
	    catch( ParseException exp ) {
	    	
	    	/* error parsing */
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "CLI", options );
	        System.err.println( "\nError: " + exp.getMessage() );
	        return;
	    }
		
	    // TODO : add an arg to select a props file
	    // TODO : default to a homedir .adobeio.properties file

	    Properties prop = new Properties();
		
		InputStream inputStream = new CLI().getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

		if (inputStream != null) {
			prop.load(inputStream);
			log.debug("Loaded properties file " + PROPERTIES_FILE_NAME);
		} else {
			throw new FileNotFoundException("Property file '" + PROPERTIES_FILE_NAME + "' not found in the classpath");
		}
		
		// API key information from properties file
		String orgId = line.getOptionValue(ARG_ORG_ID, prop.getProperty("enterprise.organizationId"));
		String technicalAccountId = line.getOptionValue(ARG_TECH_ID, prop.getProperty("enterprise.technicalAccountId"));
		String apiKey = line.getOptionValue(ARG_API_KEY,prop.getProperty("enterprise.apiKey")); 
		String tenant = line.getOptionValue(ARG_TENANT,prop.getProperty("enterprise.tenant")); 
		String pathToSecretKey = line.getOptionValue(ARG_PRIV_KEY,prop.getProperty("enterprise.privateKeyFilename")); 
		String imsHost = line.getOptionValue(ARG_IMS_HOST,prop.getProperty("server.imsHost")); 
		String clientSecret = line.getOptionValue(ARG_CLIENT_SECRET,prop.getProperty("enterprise.clientSecret"));
		String apiHost = line.getOptionValue(ARG_API_HOST,prop.getProperty("server.apiHost"));

		// Get a JWT token 
		String jwtToken = JWT.getJWT(imsHost, orgId, technicalAccountId, apiKey, pathToSecretKey);
		log.debug("JWT:" + jwtToken);
		
		// Convert the JWT token to a Bearer token
		String bearerToken = "";
		if (line.hasOption(ARG_BEARER_TOKEN))
		{
			bearerToken = line.getOptionValue(ARG_BEARER_TOKEN);
			log.debug("Bearer specified: " + bearerToken);
		} else {
			bearerToken = JWT.getBearerTokenFromJWT(imsHost, apiKey, clientSecret, jwtToken);
			log.debug("Bearer fetched: " + bearerToken);
		}
		
		// go through the arguments and execute....

		
		if (line.hasOption(ARG_GET_BEARER_TOKEN)) {
			System.out.println(bearerToken);
			return;
		} 
		
		if (line.hasOption(ARG_TARGET_ACTIVITIES)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject activities = target.getActivities();
			System.out.println(activities.toString(1));
		} 
		if (line.hasOption(ARG_TARGET_ACTIVITY_XT)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_ACTIVITY_XT));
			JSONObject activities = target.getActivityXT(activityId);
			System.out.println(activities.toString(1));
		}
		if (line.hasOption(ARG_TARGET_ACTIVITY_AB)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_ACTIVITY_AB));
			JSONObject activities = target.getActivityAB(activityId);
			System.out.println(activities.toString(1));
		}

		if (line.hasOption(ARG_TARGET_DELETE_XT)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_DELETE_XT));
			JSONObject activities = target.deleteXTActivity(activityId);
			System.out.println(activities.toString(1));
		}
		
		if (line.hasOption(ARG_TARGET_DELETE_AB)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_DELETE_AB));
			JSONObject activities = target.deleteABActivity(activityId);
			System.out.println(activities.toString(1));
		}

		if (line.hasOption(ARG_TARGET_AUDIENCES)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject audiences = target.getAudiences();
			System.out.println(audiences.toString(1));
		}
		
		if (line.hasOption(ARG_TARGET_AUDIENCE)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject audiences = target.getAudience(Long.decode(line.getOptionValue(ARG_TARGET_AUDIENCE)));
			System.out.println(audiences.toString(1));
		}		
		if (line.hasOption(ARG_TARGET_AUDIENCE_DELETE)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject audiences = target.deleteAudience(Long.decode(line.getOptionValue(ARG_TARGET_AUDIENCE)));
			System.out.println(audiences.toString(1));
		}			
		if (line.hasOption(ARG_TARGET_OFFERS)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject audiences = target.getOffers();
			System.out.println(audiences.toString(1));
		}			
		if (line.hasOption(ARG_TARGET_OFFER)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_OFFER));
			JSONObject activities = target.getOffer(activityId);
			System.out.println(activities.toString(1));
		}	
		if (line.hasOption(ARG_TARGET_DELETE_OFFER)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_DELETE_OFFER));
			JSONObject activities = target.deleteOffer(activityId);
			System.out.println(activities.toString(1));
		}	

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
	    options.addOption(new Option( ARG_HELP, "print this message" ));

	    options.addOption(Option.builder(ARG_ORG_ID)
	    		.hasArg()
                .longOpt(ARG_ORG_ID_LONG)
                .desc("Organization ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_TECH_ID)
	    		.hasArg()
                .longOpt(ARG_TECH_ID_LONG)
                .desc("Technical account ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_API_KEY)
	    		.hasArg()
                .desc("API Key" )
                .argName("key")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_TENANT)
	    		.hasArg()
                .desc("Tenant ID" )
                .argName("id")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_PRIV_KEY)
	    		.hasArg()
                .longOpt(ARG_PRIV_KEY_LONG)
                .desc("Filename of private key in DER format" )
                .argName("filename")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_IMS_HOST)
	    		.hasArg()
                .longOpt(ARG_IMS_HOST_LONG)
                .desc("Hostname of IMS Host (FQDN, no protocol)" )
                .argName("hostname")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_API_HOST)
	    		.hasArg()
                .desc("API Hostname (FQDN, no protocol)" )
                .argName("hostname")
                .build()
                );

	    
	    options.addOption(Option.builder(ARG_CLIENT_SECRET)
	    		.hasArg()
                .desc("Client Secret" )
                .argName("secret")
                .build()
                );
	    
	    options.addOption(Option.builder(ARG_TARGET_ACTIVITIES)
                .longOpt(ARG_TARGET_ACTIVITIES_LONG)
                .desc("Get all target activities" )
                .build()
                );	    

	    options.addOption(Option.builder(ARG_TARGET_AUDIENCES)
                .longOpt(ARG_TARGET_AUDIENCES_LONG)
                .desc("Get all target audiences" )
                .build()
                );	 
	    
	    options.addOption(Option.builder(ARG_TARGET_AUDIENCE)
	    		.hasArg()
                .longOpt(ARG_TARGET_AUDIENCE_LONG)
                .desc("Get a specific target audience" )
                .argName("id")
                .type(Long.class)
                .build()
                );	    
	    options.addOption(Option.builder(ARG_TARGET_AUDIENCE_DELETE)
	    		.hasArg()
                .longOpt(ARG_TARGET_AUDIENCE_DELETE_LONG)
                .desc("Delete a specific target audience" )
                .argName("id")
                .type(Long.class)
                .build()
                );	 	    
	    options.addOption(Option.builder(ARG_TARGET_DELETE_XT)
	    		.hasArg()
                .longOpt(ARG_TARGET_DELETE_XT_LONG)
                .desc("Delete a specific  XT target activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(ARG_TARGET_DELETE_AB)
	    		.hasArg()
                .longOpt(ARG_TARGET_DELETE_AB_LONG)
                .desc("Delete a specific A/B target activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(ARG_TARGET_OFFER)
	    		.hasArg()
                .longOpt(ARG_TARGET_OFFER_LONG)
                .desc("Get a specific offer by ID" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    options.addOption(Option.builder(ARG_TARGET_OFFERS)
                .longOpt(ARG_TARGET_OFFERS_LONG)
                .desc("Get all offers" )
                .build()
                );
	    

	    options.addOption(Option.builder(ARG_TARGET_DELETE_OFFER)
	    		.hasArg()
                .longOpt(ARG_TARGET_DELETE_OFFER_LONG)
                .desc("Delete a specific target offer" )
                .argName("id")
                .type(Long.class)
                .build()
                );
	    
	    
	    options.addOption(Option.builder(ARG_TARGET_ACTIVITY_AB)
	    		.hasArg()
                .longOpt(ARG_TARGET_ACTIVITY_AB_LONG)
                .desc("Get a specific A/B Targeting activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );
	    options.addOption(Option.builder(ARG_TARGET_ACTIVITY_XT)
	    		.hasArg()
                .longOpt(ARG_TARGET_ACTIVITY_XT_LONG)
                .desc("Get a specific XT Targeting activity" )
                .argName("id")
                .type(Long.class)
                .build()
                );

	    
	    options.addOption(Option.builder(ARG_BEARER_TOKEN)
                .hasArg()
                .longOpt(ARG_BEARER_TOKEN_LONG)
                .argName("token")                
                .desc("Specify the bearer token instead of fetching from IMS host" )
                .build()
                );

	    
	    options.addOption(Option.builder(ARG_GET_BEARER_TOKEN)
                .longOpt(ARG_GET_BEARER_TOKEN_LONG)                
                .desc("Get and print the bearer token" )
                .build()
                );
	    
	    return options;
	}

}
