package com.example.kpi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class Lab2 {

    private static final Logger log = LoggerFactory.getLogger(Lab2.class);

    public static void main(String[] args) throws SQLException, InterruptedException {
        var hazelcast = HazelcastInitializer.createInstance(System.getenv("INSTANCES"));
        var conn = DbInitializer.getConnection("postgres", "postgres");

        resetDatabase(hazelcast, TaskType.LOST_UPDATE);
        benchmark(() -> lostUpdate(conn), hazelcast, TaskType.LOST_UPDATE);
        waitTillAllFinish(hazelcast, TaskType.LOST_UPDATE);

        resetDatabase(hazelcast, TaskType.IN_PLACE_UPDATE);
        benchmark(() -> inPlaceUpdate(conn), hazelcast, TaskType.IN_PLACE_UPDATE);
        waitTillAllFinish(hazelcast, TaskType.IN_PLACE_UPDATE);
        validateResult(conn);

        resetDatabase(hazelcast, TaskType.ROW_LEVEL_LOCKING);
        benchmark(() -> rowLevelLocking(conn), hazelcast, TaskType.ROW_LEVEL_LOCKING);
        waitTillAllFinish(hazelcast, TaskType.ROW_LEVEL_LOCKING);
        validateResult(conn);

        resetDatabase(hazelcast, TaskType.OPTIMISTIC_CONCURRENCY_CONTROL);
        benchmark(() -> optimisticConcurrencyControl(conn), hazelcast, TaskType.OPTIMISTIC_CONCURRENCY_CONTROL);
        waitTillAllFinish(hazelcast, TaskType.OPTIMISTIC_CONCURRENCY_CONTROL);
        validateResult(conn);
    }

    private static void validateResult(Connection conn) throws SQLException {
        var result = getResult(conn);
        if (result != 100_000) {
            log.error("Not expected value: {}", result);
            System.exit(1);
        }
    }

    private static int getResult(Connection conn) throws SQLException {
        var rs = conn.prepareStatement("SELECT counter FROM user_counter WHERE user_id=1").executeQuery();
        rs.next();
        return rs.getInt("counter");
    }

    private static void waitTillAllFinish(HazelcastInstance hz, TaskType taskType) throws InterruptedException {
        IMap<String, Object> map = hz.getMap("map");
        String key = taskType.name() + "_finish";
        while (true) {
            map.lock(key);
            if (((Integer) map.get(key) != 10)) {
                Thread.sleep(500);
            } else {
                map.unlock(key);
                break;
            }
            map.unlock(key);
        }
    }

    private static void resetDatabase(HazelcastInstance hazelcast, TaskType taskType) throws InterruptedException {
        while (hazelcast.getCluster().getMembers().size() != 10) {
            Thread.sleep(500);
        }
        IMap<String, Object> map = hazelcast.getMap("map");
        map.lock(taskType.name());
        var flag = map.get(taskType.name());
        if (BooleanUtils.isNotTrue((Boolean) flag)) {
            log.info("Resetting database for task {}", taskType.name());
            DbInitializer.init("postgres", "postgres");
            map.put(taskType.name(), true);
            map.unlock(taskType.name());
        } else {
            map.unlock(taskType.name());
        }
    }

    private static void lostUpdate(Connection conn) {
        try {
            for (var i = 0; i < 10_000; i++) {
                var rs = conn.prepareStatement("SELECT counter FROM user_counter WHERE user_id=1").executeQuery();
                rs.next();
                var counter = rs.getInt("counter");
                counter++;
                rs.close();
                var stmt = conn.prepareStatement("UPDATE user_counter SET counter=? WHERE user_id=1");
                stmt.setInt(1, counter);
                stmt.executeUpdate();
                stmt.close();
                conn.commit();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void inPlaceUpdate(Connection conn) {
        try {
            for (var i = 0; i < 10_000; i++) {
                var stmt = conn.prepareStatement("UPDATE user_counter SET counter=counter+1 WHERE user_id=1");
                stmt.executeUpdate();
                stmt.close();
                conn.commit();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void rowLevelLocking(Connection conn) {
        try {
            for (var i = 0; i < 10_000; i++) {
                var rs = conn.prepareStatement("SELECT counter FROM user_counter WHERE user_id=1 FOR UPDATE").executeQuery();
                rs.next();
                var counter = rs.getInt("counter");
                counter++;
                rs.close();
                var stmt = conn.prepareStatement("UPDATE user_counter SET counter=? WHERE user_id=1");
                stmt.setInt(1, counter);
                stmt.executeUpdate();
                stmt.close();
                conn.commit();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void optimisticConcurrencyControl(Connection conn) {
        try {
            for (var i = 0; i < 10_000; i++) {
                while (true) {
                    var rs = conn.prepareStatement("SELECT counter, version FROM user_counter WHERE user_id=1").executeQuery();
                    rs.next();
                    var counter = rs.getInt("counter");
                    var version = rs.getInt("version");
                    rs.close();
                    var stmt = conn.prepareStatement("UPDATE user_counter SET counter=?, version=? WHERE user_id=1 AND version=?");
                    stmt.setInt(1, counter + 1);
                    stmt.setInt(2, version + 1);
                    stmt.setInt(3, version);
                    var rows = stmt.executeUpdate();
                    conn.commit();
                    stmt.close();
                    if (rows > 0) {
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void benchmark(Runnable runnable, HazelcastInstance hz, TaskType taskType) {
        var startTime = System.currentTimeMillis();
        runnable.run();
        log.info("Finished {} in {} ms.", taskType.name(), System.currentTimeMillis() - startTime);
        IMap<String, Object> map = hz.getMap("map");
        String key = taskType.name() + "_finish";
        map.lock(key);
        if (map.get(key) == null) {
            map.put(key, 1);
        } else {
            map.put(key, (Integer) map.get(key) + 1);
        }
        map.unlock(key);
    }
}
