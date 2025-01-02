package com.example.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
