# kpi-hz

**Ensure you have JAVA_HOME installed for `mvnw` 
or use IDE (i.e. IntelliJ IDEA) which has all bundled inside**

`mvn clean package` or `mvnw clean package`

`docker compose build --no-cache`

`docker compose up -d`

# How to use
After cluster is launched, simply make some requests to fill the data map.

`curl -X POST http://localhost:8081/test/value`

`curl http://localhost:8082/test`