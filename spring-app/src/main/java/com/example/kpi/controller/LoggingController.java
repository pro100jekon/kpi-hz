package com.example.kpi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class LoggingController {

    private int randomness() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(2000));
        switch (new Random().nextInt(5)) {
            case 1 -> {
                return 204;
            }
            case 2 -> {
                return 400;
            }
            case 3 -> {
                return 401;
            }
            case 4 -> {
                return 500;
            }
            default -> {
                return 200;
            }
        }
    }

    @GetMapping("get")
    public ResponseEntity<String> get() throws InterruptedException {
        return ResponseEntity.status(randomness()).body("get");
    }

    @PutMapping("put")
    public ResponseEntity<String> put() throws InterruptedException {
        return ResponseEntity.status(randomness()).body("put");
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "options")
    public ResponseEntity<String> options() throws InterruptedException {
        return ResponseEntity.status(randomness()).body("options");
    }

    @PostMapping("post")
    public ResponseEntity<String> post() throws InterruptedException {
        return ResponseEntity.status(randomness()).body("post");
    }
}
