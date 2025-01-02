package com.example.kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class WebAppCaller {

    final String uri;

    public WebAppCaller(RestClient restClient) {
        this.restClient = restClient;
        uri = System.getenv("URI");
    }

    @Autowired
    RestClient restClient;

    @Scheduled(fixedRate = 10)
    public void fixRate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (random.nextInt(4) % 4) {
            case 0:
                restClient.get().uri(uri + "/get").retrieve().body(String.class);
                break;
            case 1:
                restClient.post().uri(uri + "/post").retrieve().body(String.class);
                break;
            case 2:
                restClient.patch().uri(uri + "/patch").retrieve().body(String.class);
                break;
            case 3:
                restClient.put().uri(uri + "/put").retrieve().body(String.class);
        }
    }
}
