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

First. clone repo to your machine.
```
git clone https://git.corp.adobe.com/wehopkin/adobeio-wes-cli.git
```

Second.  Build the executable JAR package.
```
mvn clean package
```

Third. Run.  See the help. It will tell you what args you need.
```
java -jar ./target/CLI-executable-jar-with-dependencies.jar -help
```
# How to get an integration set up
* if you don't have a private key and self signed certificate, follow instructions below. then return here.
* goto console.adobe.io
 * new integation
 * access an API
 * adobe target
  * name: target CLI
  * drag in the .CRT file from 
  
# How to Generate self signed certificate
_note, you can (prolly should) change the password.  it's hardcoded to password in these examples._

```
openssl genrsa -aes256 -out adobe-io-private.key -passout pass:password 4096

openssl rsa -in adobe-io-private.key -passin pass:password -out adobe-io-private.key

openssl req -sha256 -new -key adobe-io-private.key -out adobe-io.csr -subj '/CN=localhost'

openssl x509 -req -days 365 -in adobe-io.csr -signkey adobe-io-private.key -out adobe-io.crt

openssl pkcs8 -topk8 -inform PEM -outform DER -in adobe-io-private.key -out adobe-io-private.der -nocrypt
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
