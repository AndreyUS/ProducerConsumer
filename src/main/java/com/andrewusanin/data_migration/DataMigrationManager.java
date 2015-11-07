package com.andrewusanin.data_migration;

public interface DataMigrationManager {
    /**
     * Create consumers and producer for data migration and start this process.
     */
    void start();
}
