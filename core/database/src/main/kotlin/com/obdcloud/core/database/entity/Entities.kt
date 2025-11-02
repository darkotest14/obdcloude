package com.obdcloud.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.obdcloud.core.domain.model.Protocol

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val vin: String,
    val year: Int,
    val make: String,
    val model: String,
    val engine: String,
    val transmission: String,
    val supportedProtocols: List<Protocol>,
    val lastScanTimestamp: Long
)

@Entity(tableName = "ecu_modules")
data class ECUModuleEntity(
    @PrimaryKey
    val id: String,
    val vehicleVin: String,
    val name: String,
    val address: String,
    val protocol: Protocol,
    val supportedPids: List<String>
)

@Entity(tableName = "dtc_codes")
data class DTCCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleId: String,
    val code: String,
    val description: String,
    val severity: String,
    val timestamp: Long,
    val freezeFrameData: Map<String, String>?
)