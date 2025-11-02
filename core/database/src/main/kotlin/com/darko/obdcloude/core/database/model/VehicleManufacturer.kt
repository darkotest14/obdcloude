package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_manufacturers")
data class VehicleManufacturer(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val ecuProtocols: List<String>
)