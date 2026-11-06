package com.campus.lostandfound;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test to verify connection to the configured MySQL database.
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        assertNotNull(dataSource, "DataSource bean must not be null");
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Database connection must not be null");
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("==================================================================");
            System.out.println(">>> DATABASE CONNECTION SUCCESSFUL! <<<");
            System.out.println("Product Name    : " + metaData.getDatabaseProductName());
            System.out.println("Product Version : " + metaData.getDatabaseProductVersion());
            System.out.println("Database URL    : " + metaData.getURL());
            System.out.println("Database User   : " + metaData.getUserName());
            System.out.println("==================================================================");
            assertTrue(connection.isValid(3), "Database connection must be valid");
        }
    }
}
