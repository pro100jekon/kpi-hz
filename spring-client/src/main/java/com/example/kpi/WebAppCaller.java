package com.example.kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class WebAppCaller {

    public WebAppCaller(RestClient restClient) {
        this.restClient = restClient;
    }

    @Autowired
    RestClient restClient;

    @Scheduled(fixedRate = 1000)
    public void fixRate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (random.nextInt(4) % 4) {
            case 0:
                restClient.get().uri("http://localhost:8080/get").retrieve().body(String.class);
                break;
            case 1:
                restClient.post().uri("http://localhost:8080/post").retrieve().body(String.class);
                break;
            case 2:
                restClient.options().uri("http://localhost:8080/options").retrieve().body(String.class);
                break;
            case 3:
                restClient.put().uri("http://localhost:8080/put").retrieve().body(String.class);
        }
    }
}
