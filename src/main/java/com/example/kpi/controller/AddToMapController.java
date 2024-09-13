package com.example.kpi.controller;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Function;

@RestController
public class AddToMapController {

    private final HazelcastInstance hazelcastInstance;
    {
        Config config = new Config();
        config.setClusterName("hello-kpi");
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    @PostMapping("{key}/{value}")
    public void addToMap(@PathVariable String key, @PathVariable String value) {

        hazelcastInstance.getMap("map").put(key, value);
    }

    @GetMapping("{key}")
    public ResponseEntity<String> getFromMap(@PathVariable String key) {
        return ResponseEntity.of(Optional.ofNullable(
                hazelcastInstance.getMap("map")
                        .get(key))
                .map(Object::toString));
    }
}
