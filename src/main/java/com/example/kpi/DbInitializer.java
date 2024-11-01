package com.example.kpi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbInitializer {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);
    private static final String DATABASE = "counters";
    private static final String TABLE = "user_counter";
    private static final String URL_FORMAT = "jdbc:postgresql://%s:%s/%s";

    public static void init(String username, String password) {
        try {
            var conn = getConnection(username, password);
            if (hasTable(conn)) {
                resetTable(conn);
            } else {
                createTable(conn);
            }
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            System.exit(1);
        }
    }

    private static boolean hasTable(Connection connection) throws SQLException {
        try (var stmt = connection.prepareStatement(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")) {
            stmt.execute();
            var rs = stmt.getResultSet();
            var result = false;
            while (rs.next()) {
                if (rs.getString("table_name").equals(TABLE)) {
                    result = true;
                    break;
                }
            }
            rs.close();
            return result;
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (var stmt = connection.prepareStatement("""
                CREATE TABLE user_counter(
                    user_id serial PRIMARY KEY,
                    counter integer default 0,
                    version integer default 0
                )
                """)) {
            stmt.execute();
        }
    }

    private static void resetTable(Connection connection) throws SQLException {
        try (var stmt = connection.prepareStatement("UPDATE user_counter SET counter=0, version=0 WHERE user_id=1")) {
            stmt.execute();
        }

    }

    public static Connection getConnection(String username, String password) throws SQLException {
        var conn = DriverManager.getConnection(URL_FORMAT.formatted("host.docker.internal", "5432", DATABASE), username, password);
        conn.setAutoCommit(false);
        return conn;
    }
}
