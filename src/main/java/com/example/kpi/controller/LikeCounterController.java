package com.example.kpi.controller;

import com.example.kpi.service.InstanceNodeService;
import com.hazelcast.core.HazelcastInstance;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LikeCounterController {

    private final HazelcastInstance hazelcastInstance;
    private final InstanceNodeService service;
    private static final String COUNTER_NAME = "likeCounter";

    @GetMapping("increment")
    public ResponseEntity<Long> incrementCounter() {
        return ResponseEntity.ok(
                hazelcastInstance.getPNCounter(COUNTER_NAME)
                        .incrementAndGet());
    }

    @GetMapping
    public ResponseEntity<Long> getCounter() {
        return ResponseEntity.ok(
                hazelcastInstance.getPNCounter(COUNTER_NAME)
                        .get());
    }

    @GetMapping("random")
    public ResponseEntity<Long> randomIncrement(@RequestParam(defaultValue = "10000") int count) {
        return ResponseEntity.ok(service.increment(count));
    }
}
