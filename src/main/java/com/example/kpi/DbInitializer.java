package com.example.kpi;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.internal.selector.PrimaryServerSelector;
import org.bson.BsonDocumentWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DbInitializer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);
    private static final String DATABASE = "lab4";
    private static final String URL = "mongodb://mongo-1:27017,mongo-2:27017,mongo-3:27017";
    private final MongoClient client;

    public DbInitializer(TaskType taskType) {
        var settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.serverSelector(new PrimaryServerSelector()))
                .applyToClusterSettings(builder -> builder.serverSelectionTimeout(60, TimeUnit.SECONDS))
                .applyToClusterSettings(builder -> builder.requiredReplicaSetName("rs0"))
                .readPreference(ReadPreference.primary())
                .writeConcern(taskType.getWriteConcern())
                .readConcern(ReadConcern.AVAILABLE)
                .applyConnectionString(new ConnectionString(URL))
                .build();
        log.info(settings.toString());
        this.client = MongoClients.create(settings);
    }

    public void increment() {
        client.getDatabase(DATABASE).getCollection(DATABASE)
                .findOneAndUpdate(
                        BsonDocumentWrapper.parse("{name:'Hello'}"),
                        BsonDocumentWrapper.parse("{$inc:{'likes':1}}"));
    }

    @Override
    public void close() {
        client.close();
    }
}
