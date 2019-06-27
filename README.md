# adobe-io-cli

## What this is

A basic command line client to call things on Adobe.io.  Use this as a basis to add your own functionality.

## What it does

* Authenticate to Adobe I/O
* Retrieve bearer tokens
* Target
  * List all activities
  * Get an activity
  * Delete an activity
  * Update a profile
* Launch
  * List companies
* Campaign
  * Retrieve a profile

## Prerequisites

### A certificate and private key

You will need a public/private keypair and a signed certificate to set up an Adobe IO integration.

If you already have your keypair and certificate, skip this section.

#### Generate your own self-signed key and certificate

_Note, you can (and probably should) change the password.  it's hardcoded to "password" in these examples._

``` sh
openssl genrsa -aes256 -out adobe-io-private.key -passout pass:password 4096
openssl rsa -in adobe-io-private.key -passin pass:password -out adobe-io-private.key
openssl req -sha256 -new -key adobe-io-private.key -out adobe-io.csr -subj '/CN=localhost'
openssl x509 -req -days 365 -in adobe-io.csr -signkey adobe-io-private.key -out adobe-io.crt
openssl pkcs8 -topk8 -inform PEM -outform DER -in adobe-io-private.key -out adobe-io-private.der -nocrypt
```

### An Adobe IO Integration

You will need to create an integration on Adobe IO for the CLI application to talk to.  If you have created one and have noted its credentials, skip this step.  Otherwise, read on to create.

#### You will need

* A certificate and private keypair (Prerequisite 1, above)
* Administrative privileges to your Experience Cloud organization
  * If the options are greyed out when creating an Integration you may not be an administrator.  If you __are__ and they're still grey, contact Adobe Client Care.

#### Creating the Adobe IO Integration

* Log in to console.adobe.io.
  * Choose New integation
  * You want to "Access an API"
  * Select a service you want to access (Target, ACS, or Launch).  You can add other services after the integration is created.
    * Give your integration a name (ie: AdobeIO CLI) and description
    * Attach your .CRT file (if you don't have one - see prerequisite section)
    * Optional - add additional services (Campaign, Launch, etc...) from 'Services' tab.
* Note the Client Credentials in the Overview tab - you will save these into the adobeio.properties (see )

## Clone, Compile, Configure, Run

## First. Clone this repo to your machine

```sh
git clone https://github.com/whatwork/adobe-io-cli.git
```

### Second.  Compile the executable JAR package.

```sh
mvn clean package
```

### Third.  Configure the CLI's properties file

By default the tool will look in your home directory for adobeio.properties .  You can also use the option '-properties' to specify a different file.

If you do not have an Adobe IO integration setup, see Prerequisites above.

Values for the properties file may be taken from the Overview screen of your integration's configuration on console.adobe.io.

You can view a sample properties file with this command:

```sh
java -jar ./target/CLI-executable-jar-with-dependencies.jar -sampleProperties
```

You may also write a sample properties file to your home folder using this command (Mac/Unix)

```sh
java -jar ./target/CLI-executable-jar-with-dependencies.jar -sampleProperties > ~/adobeio.properties
```

### Four. Play with the API

Run. Refer to the help. It will tell you what things it can do and what arguments you need to specify.
```
java -jar ./target/CLI-executable-jar-with-dependencies.jar -help
```

## Bonus: Charles Proxy for debugging HTTPS calls

If you need/want to sniff the traffic between the CLI and the server, use Charles Proxy.

You'll need to add charles's certificate to the truststore for java, otherwise the clients will (rightfully) freak out and refuse to connect.

```sh
# add charles to java keystore
keytool -importcert -alias startssl -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file charles-ca.der

# verify charles ssl proxy is in keystore
keytool -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass changeit -list | grep startssl
```

## Help References

* Target API - http://developers.adobetarget.com/api/
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt.html
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt/jwt_java.html
* https://www.adobe.io/apis/experiencecloud/target/docs/authentication.html
* https://console.adobe.io/integrations/42687/40408/jwt make a JWT UI
* https://www.adobe.io/apis/experiencecloud/target/docs/getting-started.html target api

## Supporting Libraries

* http://commons.apache.org/proper/commons-cli/
