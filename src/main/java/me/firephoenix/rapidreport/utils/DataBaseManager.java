package me.firephoenix.rapidreport.utils;


import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.firephoenix.rapidreport.RapidReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author NieGestorben
 * Copyright© (c) 2023, All Rights Reserved.
 */
@Getter
public class DataBaseManager {

    private final HikariDataSource hikariCP;

    public DataBaseManager() {
        this.hikariCP = new HikariDataSource();
        try {
            String host = RapidReport.INSTANCE.getConfig().getString("mysql.host");
            int port = Integer.parseInt(RapidReport.INSTANCE.getConfig().getString("mysql.port"));
            String username = RapidReport.INSTANCE.getConfig().getString("mysql.user");
            String password = RapidReport.INSTANCE.getConfig().getString("mysql.password");
            String database = RapidReport.INSTANCE.getConfig().getString("mysql.db");

            getHikariCP().setMaximumPoolSize(25);
            getHikariCP().setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8");
            getHikariCP().addDataSourceProperty("port", port);
            getHikariCP().addDataSourceProperty("password", password);
            getHikariCP().addDataSourceProperty("databaseName", database);
            getHikariCP().addDataSourceProperty("user", username);
            getHikariCP().addDataSourceProperty("cachePrepStmts", true);
            getHikariCP().addDataSourceProperty("prepStmtCacheSize", 250);
            getHikariCP().addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            getHikariCP().addDataSourceProperty("useServerPrepStmts", true);
            getHikariCP().addDataSourceProperty("useLocalSessionState", true);
            getHikariCP().addDataSourceProperty("rewriteBatchedStatements", true);
            getHikariCP().addDataSourceProperty("cacheResultSetMetadata", true);
            getHikariCP().addDataSourceProperty("cacheServerConfiguration", true);
            getHikariCP().addDataSourceProperty("elideSetAutoCommits", true);
            getHikariCP().addDataSourceProperty("maintainTimeStats", false);
            getHikariCP().addDataSourceProperty("alwaysSendSetIsolation", false);
            getHikariCP().addDataSourceProperty("cacheCallableStmts", true);

            getHikariCP().setUsername(username);
            getHikariCP().setPassword(password);

            this.hikariCP.getConnection();
            RapidReport.INSTANCE.logger.info("Successfully connected to database!");
        } catch (SQLException e) {
            RapidReport.INSTANCE.logger.info("There was an error connecting to the database! Error: {}", e.getMessage());
        }
    }

    public void runStatementAsync(String statement) {
        RapidReport.INSTANCE.proxy.getScheduler().buildTask(RapidReport.INSTANCE, () -> {
            try {
                Connection connection = hikariCP.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.execute();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).schedule();
    }

    public CompletableFuture<ResultSet> getSQLStatementResultAsync(String statement) {
        CompletableFuture<ResultSet> future = new CompletableFuture<>();

        RapidReport.INSTANCE.proxy.getScheduler().buildTask(RapidReport.INSTANCE, () -> {
            try (Connection connection = hikariCP.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                future.complete(resultSet);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        }).schedule();

        return future;
    }

    public void submitNewReportToDB(Report report) {
        runStatementAsync("INSERT INTO rapid_report_reports (reporterName, reportedName, reportedUUID, reason, status) VALUES ('" + report.reporterPlayerName + "', '" + report.reportedPlayerName + "', '" + report.reportedPlayerUUID.toString() + "', '" + report.reason + "', '" + report.status + "')");
    }

    public void initDB() {
        // first lets read our setup file.
        // This file contains statements to create our inital tables.
        // it is located in the resources.
        String setup;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("dbsetup.sql")) {
            // Java 9+ way
            // Legacy way
            assert in != null;
            setup = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            // Mariadb can only handle a single query per statement. We need to split at ;.
            String[] queries = setup.split(";");
            // execute each query to the database.
            for (String query : queries) {
                // If you use the legacy way you have to check for empty queries here.
                if (query.isEmpty()) continue;
                try (Connection conn = hikariCP.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.execute();
                } catch (SQLException e) {
                    RapidReport.INSTANCE.logger.info("Error executing database setup!", e);
                }
            }
        } catch (IOException e) {
            RapidReport.INSTANCE.logger.info("Could not read db setup file.", e);
        }
    }

    public void close() {
        hikariCP.close();
    }
}

