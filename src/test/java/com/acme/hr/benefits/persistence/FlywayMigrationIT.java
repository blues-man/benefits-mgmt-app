package com.acme.hr.benefits.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the application's Flyway migrations.
 *
 * These run under failsafe rather than surefire because they drive a real
 * database: Flyway applies this application's own db/migration SQL to an H2
 * instance and the assertions read the result back over JDBC. flyway-core is
 * the dependency Lightwell remediates here, so a bump that changed migration
 * behaviour surfaces in this build rather than in production.
 */
class FlywayMigrationIT {

    /** A private database per test, so ordering never matters. */
    private String freshUrl() {
        return "jdbc:h2:mem:benefits-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
    }

    private Flyway flywayFor(String url) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(url, "sa", "");
        // The application's real migrations, not test fixtures.
        flyway.setLocations("db/migration");
        return flyway;
    }

    private int scalar(String url, String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    @DisplayName("the application schema migrates from an empty database")
    void migratesFromEmpty() throws SQLException {
        String url = freshUrl();
        int applied = flywayFor(url).migrate();

        assertEquals(2, applied, "V1_1 and V1_2 should both run against an empty database");
        assertTrue(scalar(url, "SELECT COUNT(*) FROM PRODUCT_CATALOG") > 0,
                "V1_2 must seed the product catalog");
        assertTrue(scalar(url, "SELECT COUNT(*) FROM INVENTORY") > 0,
                "V1_2 must seed inventory");
        // Created empty by V1_1; proves the table exists and is queryable.
        assertEquals(0, scalar(url, "SELECT COUNT(*) FROM ORDERS"));
    }

    @Test
    @DisplayName("migration is idempotent on an already-migrated database")
    void secondRunAppliesNothing() {
        String url = freshUrl();
        assertEquals(2, flywayFor(url).migrate());
        assertEquals(0, flywayFor(url).migrate(),
                "a second migrate() must be a no-op, not a re-apply");
    }

    @Test
    @DisplayName("the schema history records both migrations as successful")
    void schemaHistoryIsRecorded() throws SQLException {
        String url = freshUrl();
        flywayFor(url).migrate();

        // Flyway 4 creates this table quoted and lower-case, and H2 2.x is
        // case-sensitive about that, so the identifier has to stay quoted.
        // Later Flyway majors renamed it to flyway_schema_history -- if a
        // remediation ever crossed that boundary, this assertion is where it
        // would show up.
        assertEquals(2, scalar(url,
                "SELECT COUNT(*) FROM \"schema_version\" WHERE \"success\" = TRUE"));
    }

    @Test
    @DisplayName("the hibernate sequence is usable after migration")
    void sequenceIsUsable() throws SQLException {
        String url = freshUrl();
        flywayFor(url).migrate();

        int first = scalar(url, "SELECT NEXT VALUE FOR hibernate_sequence");
        int second = scalar(url, "SELECT NEXT VALUE FOR hibernate_sequence");
        assertTrue(second > first, "the sequence must advance between calls");
    }
}
