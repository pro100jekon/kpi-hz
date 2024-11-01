package com.example.kpi;

import com.hazelcast.config.*;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HazelcastInitializer {

    public static final String CLUSTER_NAME = "like-counter";

    public static HazelcastInstance createInstance(String instancesToDiscover) {
        return Hazelcast.newHazelcastInstance(
                new Config()
                        .addMapConfig(new MapConfig("map")
                                .setInMemoryFormat(MapConfig.DEFAULT_IN_MEMORY_FORMAT))
                        //.setCPSubsystemConfig(new CPSubsystemConfig()
                        //        .setCPMemberCount(3)
                        //        .setLockConfigs(Arrays.stream(TaskType.values())
                        //                .collect(Collectors.toMap(Enum::name,
                        //                        task -> new FencedLockConfig()
                        //                                .setName(task.name())
                        //                                .setLockAcquireLimit(1)))))
                        .setClusterName(CLUSTER_NAME)
                        .setNetworkConfig(new NetworkConfig()
                                .setPort(5701)
                                .setJoin(new JoinConfig()
                                        .setTcpIpConfig(new TcpIpConfig()
                                                .setEnabled(true)
                                                .setMembers(Arrays.stream(instancesToDiscover.split(","))
                                                        .map(host -> host + ":5701").toList())))));
    }
}
