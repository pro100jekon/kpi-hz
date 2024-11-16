package com.example.kpi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lab3 {

    private static final Logger log = LoggerFactory.getLogger(Lab3.class);

    public static void main(String[] args) {
        try (var db = new DbInitializer("neo4j", "hellothere")) {
            var start = System.currentTimeMillis();
            for (int i = 0; i < 10_000; i++) {
                db.increment();
            }
            log.info("Total time: {}", System.currentTimeMillis() - start);
        }
    }
}
