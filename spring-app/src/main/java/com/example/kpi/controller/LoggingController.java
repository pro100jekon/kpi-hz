package com.example.kpi.controller;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@RestController
public class LoggingController {

    private void randomness() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(2000));
    }

    @GetMapping("get")
    public String get() throws InterruptedException {
        randomness();
        return "get";
    }

    @PutMapping("put")
    public String put() throws InterruptedException {
        randomness();
        return "put";
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "options")
    public String options() throws InterruptedException {
        randomness();
        return "options";
    }

    @PostMapping("post")
    public String post() throws InterruptedException {
        randomness();
        return "post";
    }
}
