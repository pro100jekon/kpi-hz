package com.example.kpi;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;

import java.io.Closeable;
import java.io.IOException;

public class DbInitializer implements AutoCloseable {

    private static final String DATABASE = "neo4j";
    private static final String URL = "neo4j://neo4j:7687";
    private Driver driver;

    public DbInitializer(String username, String password) {
        var driver = GraphDatabase.driver(URL, AuthTokens.basic(username, password));
        driver.verifyConnectivity();
        this.driver = driver;
    }

    public void increment() {
        driver.executableQuery("MATCH (item:ITEM{name:'Samsung Galaxy S24 Ultra'}) SET item.likes=item.likes+1 RETURN item")
                .withConfig(QueryConfig.builder().withDatabase(DATABASE).build())
                .execute();
    }

    @Override
    public void close() {
        driver.close();
    }
}
