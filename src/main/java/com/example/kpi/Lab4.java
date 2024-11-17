package com.example.kpi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lab4 {

    public static void main(String[] args) {
        var context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        var log = LoggerFactory.getLogger(Lab4.class);
        try (var db = new DbInitializer(TaskType.fromArgs(args))) {
            var start = System.currentTimeMillis();
            for (int i = 0; i < 10_000; i++) {
                db.increment();
            }
            log.info("Total time: {}", System.currentTimeMillis() - start);
        }
    }
}
