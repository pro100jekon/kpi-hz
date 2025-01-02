package com.example.kpi;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.config.JetConfig;

import java.util.List;

public class HazelcastInitializer {

    public static final String CLUSTER_NAME = "test";

    public static HazelcastInstance createInstance() {
        return Hazelcast.newHazelcastInstance(
                new Config()
                        .addListConfig(new ListConfig("RequestsCountList"))
                        .addMapConfig(new MapConfig("RequestsCountMap")
                                .setInMemoryFormat(MapConfig.DEFAULT_IN_MEMORY_FORMAT))
                        .setClusterName(CLUSTER_NAME)
                        .setJetConfig(new JetConfig().setEnabled(true))
                        .setNetworkConfig(new NetworkConfig()
                                .setPort(5701)
                                .setJoin(new JoinConfig()
                                        .setTcpIpConfig(new TcpIpConfig()
                                                .setEnabled(true)))));
    }
}
