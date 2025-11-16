package com.enterprise.wallet.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway configuration to handle migration checksum mismatches
 * This runs repair before migration to fix any checksum issues in development
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // Repair checksums before migration
            flyway.repair();
            // Then run migrations
            flyway.migrate();
        };
    }
}
