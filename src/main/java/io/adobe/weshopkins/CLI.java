package io.adobe.weshopkins;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import io.adobe.weshopkins.JWT;
import io.adobe.weshopkins.target.TargetAPI;

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
	
	private final static String ARG_TARGET_ACTIVITIES = "tacts";
	private final static String ARG_TARGET_ACTIVITIES_LONG = "targetActivities";
	private final static String ARG_TARGET_ACTIVITY_XT = "txt";
	private final static String ARG_TARGET_ACTIVITY_XT_LONG = "getTargetXT";
	private final static String ARG_TARGET_ACTIVITY_AB = "tab";
	private final static String ARG_TARGET_ACTIVITY_AB_LONG = "getTargetAB";

	private final static String ARG_TARGET_DELETE_XT = "tdxt";
	private final static String ARG_TARGET_DELETE_XT_LONG = "deleteTargetXT";
	private final static String ARG_TARGET_DELETE_AB = "tdab";
	private final static String ARG_TARGET_DELETE_AB_LONG = "targetDeleteAB";
	
	private final static String ARG_HELP = "help";
	private final static String ARG_BEARER_TOKEN = "bt";
	private final static String ARG_BEARER_TOKEN_LONG = "bearerToken";

	private static Log log = LogFactory.getLog(JWT.class);

	
	
	public static void main(String[] args) throws Exception {
		
	    /* create command line parser */
		CommandLineParser parser = new DefaultParser();
	    CommandLine line = null;
	    
	    /* add all the acceptable command line arguments */
	    Options options = new Options();
	    options.addOption(new Option( ARG_HELP, "print this message" ));
	    
	    options.addOption(Option.builder(ARG_TARGET_ACTIVITIES)
                .longOpt(ARG_TARGET_ACTIVITIES_LONG)
                .desc("Get all target activities" )
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
		
		if (line.hasOption(ARG_TARGET_ACTIVITIES)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			JSONObject activities = target.getActivities();
			System.out.println(activities.toString(1));
		} 
		if (line.hasOption(ARG_TARGET_ACTIVITY_XT)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_ACTIVITY_XT));
			JSONObject activities = target.getActivity(activityId);
			System.out.println(activities.toString(1));
		}
		if (line.hasOption(ARG_TARGET_ACTIVITY_AB)) {
			TargetAPI target = new TargetAPI(apiHost, tenant, apiKey, bearerToken);
			Long activityId = Long.decode(line.getOptionValue(ARG_TARGET_ACTIVITY_AB));
			JSONObject activities = target.getActivity(activityId);
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
		
	}

	

}
