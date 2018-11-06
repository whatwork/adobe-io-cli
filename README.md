# adobeio-wes-cli

### What this is
A basic command line client to call things on Adobe.io.  Use this as a basis to add your own functionality.

## What it does
* Authenticate to Adobe I/O
* Retrieve bearer tokens
* Target
 * List all activities
 * Get an activity
 * Delete an activity
 

## Setup

* rename adobeio.sample.properties to adobeio.properties
* populate adobeio.properties with your account credentials 
* compile and go

## Quick and easy start

### First. clone repo to your machine.
```
git clone https://git.corp.adobe.com/wehopkin/adobeio-wes-cli.git
```

### Second.  Build the executable JAR package.
```
mvn clean package
```

### Third. Save your IO credentials in a property file
By default the tool will look in your home directory for adobeio.properties .  You can also use the option '-properties' to specify a different file.

If you do not have an Adobe IO integration setup, see "How to create an Adobe IO integration" section below, then return here.

Populate the properties file.

You can view a sample properties file with this command:
```
java -jar ./target/CLI-executable-jar-with-dependencies.jar -sampleProperties
```
You may also write a sample properties file to your home folder using this command (Mac/Unix)
```
java -jar ./target/CLI-executable-jar-with-dependencies.jar -sampleProperties > ~/adobeio.properties
```

### Four. Begin playing with API

Third. Run.  See the help. It will tell you what args you need.
```
java -jar ./target/CLI-executable-jar-with-dependencies.jar -help
```

# How to create an Adobe IO integration
* if you don't have a private key and self signed certificate, follow instructions below. then return here.

## Prerequisite: A certificate and private key
Chances are you do not already have a key and certificate. If you do, skip this section. 

This is how to generate your own self-signed key and certificate.

_Note, you can (and probably should) change the password.  it's hardcoded to "password" in these examples._

```
openssl genrsa -aes256 -out adobe-io-private.key -passout pass:password 4096

openssl rsa -in adobe-io-private.key -passin pass:password -out adobe-io-private.key

openssl req -sha256 -new -key adobe-io-private.key -out adobe-io.csr -subj '/CN=localhost'

openssl x509 -req -days 365 -in adobe-io.csr -signkey adobe-io-private.key -out adobe-io.crt

openssl pkcs8 -topk8 -inform PEM -outform DER -in adobe-io-private.key -out adobe-io-private.der -nocrypt
```

## Creating the Adobe IO Integration
* goto console.adobe.io
 * Choose New integation
 * You want to "Access an API"
 * Select the service you want to access (Target) - note: you can add other services once the integration is created, but may only select one now.
  * Give it a name and description
  * Attach your .CRT file you generated (if you don't have one - see previous prerequisite section)
  * Optional - add additional services (AA, Launch, etc...) from 'Services' tab.
* Note the Client Credentials in the Overview tab - you will save these into the adobeio.properties file

## For debugging HTTPS calls
If you need/want to sniff the traffic between the CLI and the server, use Charles Proxy.

You'll need to add charles's certificate to the truststore for java, otherwise the clients will (rightfully) freak out and refuse to connect.

```
# add charles to java keystore
keytool -importcert -alias startssl -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file charles-ca.der

# verify charles ssl proxy is in keystore
keytool -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass changeit -list | grep startssl
```

# Help References
* Target API - http://developers.adobetarget.com/api/
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt.html
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt/jwt_java.html
* https://www.adobe.io/apis/experiencecloud/target/docs/authentication.html
* https://console.adobe.io/integrations/42687/40408/jwt make a JWT UI
* https://www.adobe.io/apis/experiencecloud/target/docs/getting-started.html target api

## Supporting Libraries
* http://commons.apache.org/proper/commons-cli/
