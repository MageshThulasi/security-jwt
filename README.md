# Security with JWT

[![build status](https://gitlab.ins.risk.regn.net/batch/batch-security/badges/master/build.svg)](https://gitlab.ins.risk.regn.net/batch/batch-security/commits/master)
[![coverage report](https://gitlab.ins.risk.regn.net/batch/batch-security/badges/master/coverage.svg)](https://batch.pages.gitlab.ins.risk.regn.net/batch-security/buildDashboard/index.html)

Batch Security - Synopsis
Using OAuth2 and JWT

## JWT
Use Java's keytool to produce asymmetric key pair and use it to sign the converter.

```
# To generate the keystore (for batch-security microservice)
keytool -genkeypair -alias batchkeys -keyalg RSA -dname "CN=LexisNexis, L=Alpharetta, S=Alpharetta, C=US" -keypass changeme -keystore batchkeys.jks -storepass changeme

# To display the public key and certificate (for client microservices)
keytool -list -rfc --keystore batchkeys.jks | openssl x509 -inform pem -pubkey
```

## Build Instructions using Gradle
Gradle is an open source build automation system that builds upon the concepts of Apache Ant and Apache Maven and introduces a Groovy-based domain-specific language (DSL) instead of the XML  for declaring the project configuration. Gradle uses a directed acyclic graph ("DAG") to determine the order in which tasks can be run.

### To build and run the unit tests
./gradlew clean build

### To generate and get the access token
curl -H "Accept: application/json" rbb-api-client:password@localhost:8080/oauth/token -d grant_type=client_credentials

### Use the access token and retrieve a protected resource
curl -H "Authorization: Bearer <ACCESS-TOKEN>" localhost:8080/protected/

Thank you!
