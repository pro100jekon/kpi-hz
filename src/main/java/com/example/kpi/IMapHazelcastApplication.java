package com.example.kpi;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.util.concurrent.ThreadFactoryImpl;
import com.hazelcast.map.IMap;

import java.util.List;
import java.util.concurrent.*;

import static com.example.kpi.TaskType.*;

public class IMapHazelcastApplication {

    public static final String CLUSTER_NAME = "like-counter";
    public static final String MAP_NAME = "likes";
    public static final String KEY = "like";


    private static HazelcastInstance createInstance(int port) {
        var config = new Config();
        config.setClusterName(CLUSTER_NAME);
        var networkConfig = new NetworkConfig();
        networkConfig.setPort(port);
        config.setNetworkConfig(networkConfig);
        //
        var mapConfig = new MapConfig(MAP_NAME);
        mapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        config.addMapConfig(mapConfig);
        //
        return Hazelcast.newHazelcastInstance(config);
    }

    public static void main(String[] args) throws InterruptedException {
        var instances = List.of(createInstance(1998), createInstance(1999), createInstance(2000));

        benchmark(() -> performTask(getIncrementor(), instances, NO_LOCK));
        Thread.sleep(10000);
        System.out.printf("%n=============================================%n%n");
        benchmark(() -> performTask(getIncrementor(), instances, PESSIMISTIC_LOCK));
        Thread.sleep(10000);
        System.out.printf("%n=============================================%n%n");
        benchmark(() -> performTask(getIncrementor(), instances, OPTIMISTIC_LOCK));
    }

    private static ThreadPoolExecutor getIncrementor() {
        return new ThreadPoolExecutor(10,
                100,
                2,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryImpl("Incrementor_"));
    }

    private static void performTask(ThreadPoolExecutor executor, List<HazelcastInstance> instances, TaskType taskType) {
        CompletionService<Long> service = new ExecutorCompletionService<>(executor);
        IMap<String, Long> initMap = instances.get(0).getMap(MAP_NAME);
        initMap.put(KEY, 0L);
        for (int i = 0; i < 10; i++) {
            switch (taskType) {
                case NO_LOCK: {
                    service.submit(() -> noLockingIncrement(instances));
                    break;
                }
                case PESSIMISTIC_LOCK: {
                    service.submit(() -> pessimisticLockingIncrement(instances));
                    break;
                }
                case OPTIMISTIC_LOCK: {
                    service.submit(() -> optimisticLockingIncrement(instances));
                    break;
                }
            }
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
        System.out.println("Final result: " + instances.get(0).getMap(MAP_NAME).get(KEY));
        instances.get(0).getMap(MAP_NAME).clear();
        executor.shutdown();
    }

    private static Long noLockingIncrement(List<HazelcastInstance> instances) {
        int randomInstance = ThreadLocalRandom.current().nextInt(3);
        var instance = instances.get(randomInstance);
        System.out.printf("Thread %14s will use Instance on port %s%n", Thread.currentThread().getName(), instance.getConfig().getNetworkConfig().getPort());
        IMap<String, Long> map = instance
                .getMap(MAP_NAME);
        for (int j = 0; j < 10000; j++) {
            Long like = map.get(KEY);
            map.put(KEY, like + 1);
        }
        return map.get(KEY);
    }

    private static Long pessimisticLockingIncrement(List<HazelcastInstance> instances) {
        int randomInstance = ThreadLocalRandom.current().nextInt(3);
        var instance = instances.get(randomInstance);
        System.out.printf("Thread %14s will use Instance on port %s%n", Thread.currentThread().getName(), instance.getConfig().getNetworkConfig().getPort());
        IMap<String, Long> map = instance
                .getMap(MAP_NAME);
        for (int j = 0; j < 10000; j++) {
            try {
                map.lock(KEY);
                Long like = map.get(KEY);
                map.put(KEY, like + 1);
            } finally {
                map.unlock(KEY);
            }
        }
        return map.get(KEY);
    }

    private static Long optimisticLockingIncrement(List<HazelcastInstance> instances) {
        int randomInstance = ThreadLocalRandom.current().nextInt(3);
        var instance = instances.get(randomInstance);
        System.out.printf("Thread %14s will use Instance on port %s%n", Thread.currentThread().getName(), instance.getConfig().getNetworkConfig().getPort());
        IMap<String, Long> map = instance
                .getMap(MAP_NAME);
        for (int j = 0; j < 10000; j++) {
            while (true) {
                Long like = map.get(KEY);
                if (map.replace(KEY, like, like + 1)) {
                    break;
                }
            }
        }
        return map.get(KEY);
    }

    private static void benchmark(Runnable runnable) {
        var startTime = System.currentTimeMillis();
        runnable.run();
        System.out.printf("Finished in %d ms.%n", System.currentTimeMillis() - startTime);
    }
}
