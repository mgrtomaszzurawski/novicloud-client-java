# WireMock - local mock server for NoviCloud API

Run the `demo-app` without a live NoviCloud account.

## Requirements

- Java (available in PATH)
- `wiremock-standalone.jar` in this folder (not included in the repository)

## Download WireMock

The standalone JAR is not checked into the repository (19 MB binary).
Download it manually:

**Option A - curl:**
```bash
curl -L -o wiremock/wiremock-standalone.jar \
  https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.12.1/wiremock-standalone-3.12.1.jar
```

**Option B - wget:**
```bash
wget -O wiremock/wiremock-standalone.jar \
  https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.12.1/wiremock-standalone-3.12.1.jar
```

**Option C - browser:**
Download from [wiremock.org/docs/standalone/java-jar](https://wiremock.org/docs/standalone/java-jar/)
and save as `wiremock/wiremock-standalone.jar`.

Tested version: **3.12.1**. Any 3.x release should work.

## Start the mock server

**Windows:**
```bat
wiremock\start-mock.bat
```

**Linux/macOS:**
```bash
chmod +x wiremock/start-mock.sh
wiremock/start-mock.sh
```

The server starts on `http://localhost:4010` and logs every request.

## Swagger UI

After starting the mock server, open in a browser:

```
http://localhost:4010/swagger-ui
```

Browse the full API (all endpoints, models, parameters). The "Try it out" button works -
requests are automatically sent to `localhost:4010` instead of the production server.

## Run demo-app with mock

Set the `NOVICLOUD_BASE_URL` environment variable and run:

**Windows:**
```bat
set NOVICLOUD_BASE_URL=http://localhost:4010
mvn -pl demo-app exec:java -Dexec.mainClass=io.github.mgrtomaszzurawski.novicloud.demo.app.NoviCloudDemoApp
```

**Linux/macOS:**
```bash
NOVICLOUD_BASE_URL=http://localhost:4010 mvn -pl demo-app exec:java \
  -Dexec.mainClass=io.github.mgrtomaszzurawski.novicloud.demo.app.NoviCloudDemoApp
```

Credentials (`NOVICLOUD_ACCOUNT_NAME`, `NOVICLOUD_PASSWORD`) are ignored in mock mode -
the app uses `demo` as the default account name.

## Folder structure

```
wiremock/
  mappings/         # Routing stubs (one file per endpoint)
    towary.json
    asorty.json
    ...             # 18 files - all API endpoints
  __files/          # Response bodies
    towary-list.json
    towary-single.json
    ...             # ~35 files with realistic data from api-verification
  start-mock.bat    # Windows start script
  start-mock.sh     # Linux/macOS start script
  README.md
```

## What the mock does NOT simulate

The mock returns static responses - it does not simulate state:

- CREATE always returns `id=9999`; DELETE/UPDATE return 200 regardless of ID
- Query parameter filters are ignored - the mock returns the same response for all queries
- Pagination (`listAll()`) - the mock has no `links.next`, so iteration stops after the first page
- Data in `dane[]` is synthetic (`DUMMY_NAME`, `DUMMY_CODE`, etc.)

## Extending

To add more realistic data or simulate stateful behavior (e.g. 404 after DELETE):

1. Edit the relevant files in `__files/`
2. Add new stubs in `mappings/` using the [WireMock documentation](https://wiremock.org/docs/stubbing/)
3. Restart the mock server - WireMock loads files at startup
