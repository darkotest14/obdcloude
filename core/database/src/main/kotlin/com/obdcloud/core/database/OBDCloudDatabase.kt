package com.obdcloud.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.obdcloud.core.database.dao.VehicleDao
import com.obdcloud.core.database.dao.ECUModuleDao
import com.obdcloud.core.database.dao.DTCDao
import com.obdcloud.core.database.entity.VehicleEntity
import com.obdcloud.core.database.entity.ECUModuleEntity
import com.obdcloud.core.database.entity.DTCCodeEntity
import com.obdcloud.core.database.converter.Converters

@Database(
    entities = [
        VehicleEntity::class,
        ECUModuleEntity::class,
        DTCCodeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OBDCloudDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun ecuModuleDao(): ECUModuleDao
    abstract fun dtcDao(): DTCDao
}