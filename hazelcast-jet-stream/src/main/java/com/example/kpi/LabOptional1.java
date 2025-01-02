package com.example.kpi;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;

public class LabOptional1 {

    public static void main (String[] args) throws InterruptedException {
        var hazelcast = HazelcastClient.newHazelcastClient(
                new ClientConfig()
                        .setClusterName("test")
                        .setNetworkConfig(new ClientNetworkConfig()
                                .addAddress("localhost:5701")));
        while (true) {
            hazelcast.getMap("RequestsCountMap").forEach(System.out::println);
            Thread.sleep(5000);
        }
    }
}
