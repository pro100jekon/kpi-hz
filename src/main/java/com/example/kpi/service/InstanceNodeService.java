package com.example.kpi.service;

import com.hazelcast.core.HazelcastInstance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InstanceNodeService {

    private final RestTemplate restTemplate;
    private final HazelcastInstance hazelcastInstance;

    public long increment(int increments) {
        var members = new ArrayList<>(hazelcastInstance.getCluster().getMembers());
        if (members.isEmpty()) {
            log.info("No members found");
            throw new RuntimeException("No members found");
        }
        if (members.size() > 1) {
            Collections.shuffle(members.stream().filter(member -> {
                try {
                    return !member.getAddress().getHost().equals(InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
        }
        var host = members.iterator().next().getAddress().getHost();
        log.info("Sending {} increments to instance with host {}", increments, host);
        for (int i = 0; i < increments; i++) {
            restTemplate.getForEntity(getUrl(host, "increment"), Void.class);
        }
        var finalResult = restTemplate.getForEntity(getUrl(host, ""), Long.class);
        if (finalResult.hasBody()) {
            return finalResult.getBody();
        }
        throw new RuntimeException("Something went wrong");
    }

    private static String getUrl(String host, String path) {
        return "http://" + host + ":8080" + (path.isBlank() ? path : "/" + path);
    }
}
