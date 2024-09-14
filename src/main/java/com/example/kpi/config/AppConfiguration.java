package com.example.kpi.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("like-counter");
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
