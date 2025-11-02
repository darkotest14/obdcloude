package com.darko.obdcloude.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example migration when needed
        }
    }
}

interface MigrationCallback {
    fun onMigrationStarted(fromVersion: Int, toVersion: Int)
    fun onMigrationCompleted(fromVersion: Int, toVersion: Int)
    fun onMigrationFailed(fromVersion: Int, toVersion: Int, error: Exception)
}