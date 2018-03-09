package io.adobe.weshopkins;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import io.adobe.weshopkins.JWT;
import io.adobe.weshopkins.target.TargetApi;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 


public class CLI {
	
	private final static String PROPERTIES_FILE_NAME = "adobeio.properties";
	
	private final static String ARG_TARGET_ACTIVITIES = "targetActivities";
	private final static String ARG_HELP = "help";
	private final static String ARG_BEARER_TOKEN = "bearerToken";

	private static Log log = LogFactory.getLog(JWT.class);

	public static void main(String[] args) throws Exception {
		
	    CommandLineParser parser = new DefaultParser();
	    CommandLine line = null;
	    
	    Options options = new Options();
	    options.addOption(new Option( ARG_HELP, "print this message" ));
	    
	    options.addOption(Option.builder("ta")
                .longOpt(ARG_TARGET_ACTIVITIES)
                .desc("Get all target activities" )
                .build()
                );	    
	    
	    options.addOption(Option.builder("bt")
                .hasArg()
                .longOpt(ARG_BEARER_TOKEN)
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
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "CLI", options );
	        System.err.println( "\nError: " + exp.getMessage() );
	        return;
	    }
		
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
		
		if (line.hasOption(ARG_TARGET_ACTIVITIES)) {
			JSONObject activities = TargetApi.getActivities("https://" + apiHost + "/" + tenant + "/target/activities/", apiKey, bearerToken);
			System.out.println(activities.toString(1));
		}
		
	}

	

}
