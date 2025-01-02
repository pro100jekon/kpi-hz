package com.example.kpi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.ToLongFunctionEx;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.datamodel.WindowResult;
import com.hazelcast.jet.impl.pipeline.SinkImpl;
import com.hazelcast.jet.pipeline.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@SpringBootApplication
public class LoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);
        Pipeline p = buildPipeline(System.getenv("DIR"));
        HazelcastInstance hz = HazelcastInitializer.createInstance();

        hz.getJet().newJob(p);
    }

    private static Pipeline buildPipeline(String dir) {
        Pipeline p = Pipeline.create();
        StreamSource<String> source = Sources.filesBuilder(dir)
                .charset(StandardCharsets.UTF_8)
                .glob("*.log")
                .sharedFileSystem(false)
                .buildWatcher();

        Pattern pattern = Pattern.compile("Processed request for (.+?) in \\d+? ms. HTTP Status code: 200");

        var filtered = p.readFrom(source)
                .withTimestamps(parseDate(), 1000)
                .filter(line -> pattern.matcher(line).find())
                .groupingKey(line -> {
                    var m = pattern.matcher(line);
                    m.find();
                    return m.group(1);
                });
        filtered.window(WindowDefinition.tumbling(30_000))
                .aggregate(AggregateOperations.counting())
                .writeTo(Sinks.logger());
        filtered
                .rollingAggregate(AggregateOperations.counting())
                .writeTo(Sinks.map("RequestsCountMap"));


        return p;
    }

    private static ToLongFunctionEx<String> parseDate() {
        return line -> LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(line.substring(0, 24))).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
