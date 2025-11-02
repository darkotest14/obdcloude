package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_engines",
    foreignKeys = [
        ForeignKey(
            entity = VehicleModel::class,
            parentColumns = ["id"],
            childColumns = ["modelId"]
        )
    ]
)
data class VehicleEngine(
    @PrimaryKey
    val id: String,
    val modelId: String,
    val code: String,
    val name: String,
    val displacement: Int, // in cc
    val fuelType: String,
    val power: Int, // in kW
    val ecuType: String,
    val ecuVersion: String,
    val protocolIds: List<String>
)