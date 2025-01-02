package com.example.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {

    static RestClient restClient;

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }

    @Scheduled(fixedRate = 1000)
    public void fixRate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (random.nextInt(4) % 4) {
            case 0:
                restClient.get().uri("http://localhost:8080/get");
                break;
            case 1:
                restClient.get().uri("http://localhost:8080/post");

                break;
            case 2:
                restClient.get().uri("http://localhost:8080/options");
                break;
            case 3:
                restClient.get().uri("http://localhost:8080/put");
        }
    }

    public static void main(String[] args) {
        var ctx = SpringApplication.run(ClientApplication.class, args);
        restClient = ctx.getBean(RestClient.class);
    }
}
