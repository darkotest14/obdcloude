package com.darko.obdcloude.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.darko.obdcloude.core.database.converter.DatabaseConverters
import com.darko.obdcloude.core.database.dao.VehicleDao
import com.darko.obdcloude.core.database.dao.DiagnosticDao
import com.darko.obdcloude.core.database.model.*

@Database(
    entities = [
        VehicleManufacturer::class,
        VehicleModel::class,
        VehicleEngine::class,
        EcuSystem::class,
        DiagnosticParameter::class,
        DiagnosticCommand::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun diagnosticDao(): DiagnosticDao
}