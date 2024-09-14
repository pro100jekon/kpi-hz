# kpi-hz

**Ensure you have JAVA_HOME installed for `mvnw` 
or use IDE (i.e. IntelliJ IDEA) which has all bundled inside**

`mvn clean package` or `mvnw clean package`

`docker compose build --no-cache`

`docker compose up -d`

# How to use
`curl http://localhost:8081/random[?count=5000]` - performs `[count]` or 10000 increments on a random Hazelcast node 

`curl http://localhost:8081` - gets current counter value

`curl http://localhost:8081/increment` - manually increment counter by 1