package com.example.kpi;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class IAtomicLongHazelcastApplication {

    public static final String CLUSTER_NAME = "like-counter";

    public static void main(String[] args) {
        var instances = List.of(createInstance(1998), createInstance(1999), createInstance(2000));

        benchmark(() -> performTask(Executors.newFixedThreadPool(10), instances));
    }

    private static HazelcastInstance createInstance(int port) {
        var config = new Config();
        config.setClusterName(CLUSTER_NAME);
        var networkConfig = new NetworkConfig();
        networkConfig.setPort(port);
        config.setNetworkConfig(networkConfig);
        //
        var cpSubsystemConfig = new CPSubsystemConfig();
        cpSubsystemConfig.setCPMemberCount(3);
        config.setCPSubsystemConfig(cpSubsystemConfig);
        //
        return Hazelcast.newHazelcastInstance(config);
    }

    private static void performTask(ExecutorService executorService, List<HazelcastInstance> instances) {
        CompletionService<Long> service = new ExecutorCompletionService<>(executorService);
        instances.get(0).getCPSubsystem().getAtomicLong("likes").set(0);
        for (int i = 0; i < 10; i++) {
            service.submit(() -> {
                int randomInstance = ThreadLocalRandom.current().nextInt(3);
                var instance = instances.get(randomInstance);
                System.out.printf("Thread %16s will use Instance on port %s%n", Thread.currentThread().getName(), instance.getConfig().getNetworkConfig().getPort());
                for (int j = 0; j < 10000; j++) {
                    instance.getCPSubsystem().getAtomicLong("likes").incrementAndGet();
                }
                return instance.getCPSubsystem().getAtomicLong("likes").get();
            });
        }
        var completed = 0;
        while (completed < 10) {
            try {
                Future<Long> future = service.take();
                System.out.printf("Result from thread %6s%n", future.get());
                completed++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Final result: " + instances.get(0).getCPSubsystem().getAtomicLong("likes").get());
    }

    private static void benchmark(Runnable runnable) {
        var startTime = System.currentTimeMillis();
        runnable.run();
        System.out.printf("Finished in %d ms.%n", System.currentTimeMillis() - startTime);
    }
}
