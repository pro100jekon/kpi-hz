package com.example.kpi;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import com.mongodb.internal.selector.PrimaryServerSelector;
import org.bson.BsonDocumentWrapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DbInitializer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);
    private static final String DATABASE = "lab4";
    private static final String URL = "mongodb://mongo-1:27017,mongo-2:27017,mongo-3:27017";
    private final MongoClient client;
    private MongoCollection<Document> db;

    public DbInitializer(TaskType taskType) {
        var settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .mode(ClusterConnectionMode.MULTIPLE)
                        .requiredClusterType(ClusterType.REPLICA_SET)
                        .serverSelector(new PrimaryServerSelector())
                        .requiredReplicaSetName("rs0"))
                .applyToSocketSettings(builder -> builder
                        .readTimeout(1000, TimeUnit.MILLISECONDS))
                .readPreference(ReadPreference.secondary())
                .writeConcern(taskType.getWriteConcern())
                .readConcern(ReadConcern.DEFAULT)
                .applyConnectionString(new ConnectionString(URL))
                .build();
        log.info(settings.toString());
        this.client = MongoClients.create(settings);
        db = client.getDatabase(DATABASE).getCollection(DATABASE);
    }

    public void increment() {
        try {
            db.findOneAndUpdate(
                    BsonDocumentWrapper.parse("{name:'Hello'}"),
                    BsonDocumentWrapper.parse("{$inc:{'likes':1}}"),
                    new FindOneAndUpdateOptions().maxTime(1, TimeUnit.SECONDS));
        } catch (MongoSocketReadTimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        client.close();
    }
}
