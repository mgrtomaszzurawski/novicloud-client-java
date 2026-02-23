<!-- line width: 80 -->
# novicloud-client-java

Java client library for the Novicloud REST API.

This is an unofficial, independent project and is not affiliated with or
endorsed by Novicloud.

## Repository structure

The repository contains **two modules**:

### novicloud-client

SDK – REST API client library (Java 17, `java.net.http.HttpClient`,
Jackson). Add as a dependency in your project.

### demo-app

Demo application (Spring Boot) – example usage of the SDK (e.g. fetching
product list). Depends on the SDK.

Build from the root directory: `mvn clean install` – builds the SDK first,
then demo-app. Use separate branches for new features
(e.g. `feature/feature-name`).

## Status

Work in progress.

## License

AGPL-3.0
