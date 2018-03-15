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

## Building

```
mvn clean package
```
This will give you a fully contained executable JAR file in the ./target output directory


## Todo
* lots, lol.
* feed API credentials on command line


# How to Generate self signed certificate
_note, you can (prolly should) change the password.  it's hardcoded to password in these examples._

```
openssl genrsa -aes256 -out target-test-private.key -passout pass:password 4096

openssl rsa -in target-test-private.key -passin pass:password -out target-test-private.key

openssl req -sha256 -new -key target-test-private.key -out target-test-.csr -subj '/CN=localhost'

openssl x509 -req -days 365 -in target-test-.csr -signkey target-test-private.key -out target-test-.crt

openssl pkcs8 -topk8 -inform PEM -outform DER -in target-test-private.key -out target-test-private.der -nocrypt
```

## For debugging HTTPS calls
If you need/want to sniff the traffic between the CLI and the server, use Charles Proxy.

You'll need to add charles's certificate to the truststore for java, otherwise the clients will freak out.

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
