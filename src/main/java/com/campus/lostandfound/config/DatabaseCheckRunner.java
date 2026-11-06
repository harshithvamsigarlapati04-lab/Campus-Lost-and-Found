package com.campus.lostandfound.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Startup runner to verify and log active database connection details.
 */
@Component
public class DatabaseCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCheckRunner.class);
    private final DataSource dataSource;

    public DatabaseCheckRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("==================================================================");
            log.info("DATABASE CONNECTION VERIFIED SUCCESSFULLY!");
            log.info("Connected to : {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            log.info("Database URL : {}", metaData.getURL());
            log.info("Database User: {}", metaData.getUserName());
            log.info("==================================================================");
        } catch (Exception e) {
            log.error("==================================================================");
            log.error("FAILED TO CONNECT TO DATABASE: {}", e.getMessage());
            log.error("Please verify that MySQL Server is running and DB_PASSWORD is set.");
            log.error("==================================================================");
        }
    }
}
