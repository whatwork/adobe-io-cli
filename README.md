## Setup

* rename adobeio.sample.properties to adobeio.properties
* populate adobeio.properties with your account credentials 

## Libraries
* http://commons.apache.org/proper/commons-cli/

# Generate self signed cert
```
openssl genrsa -aes256 -out target-test-private.key -passout pass:password 4096

openssl rsa -in target-test-private.key -passin pass:password -out target-test-private.key

openssl req -sha256 -new -key target-test-private.key -out target-test-.csr -subj '/CN=localhost'

openssl x509 -req -days 365 -in target-test-.csr -signkey target-test-private.key -out target-test-.crt

openssl pkcs8 -topk8 -inform PEM -outform DER -in target-test-private.key -out target-test-private.der -nocrypt
```

# add charles to java keystore
keytool -importcert -alias startssl -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file charles-ca.der

# verify charles ssl proxy is in keystore
keytool -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass changeit -list | grep startssl


# Help References
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt.html
* https://www.adobe.io/apis/cloudplatform/console/authentication/createjwt/jwt_java.html
* https://www.adobe.io/apis/experiencecloud/target/docs/authentication.html
* https://console.adobe.io/integrations/42687/40408/jwt make a JWT UI
* https://www.adobe.io/apis/experiencecloud/target/docs/getting-started.html target api
