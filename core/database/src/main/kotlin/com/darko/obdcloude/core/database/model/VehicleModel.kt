package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_models",
    foreignKeys = [
        ForeignKey(
            entity = VehicleManufacturer::class,
            parentColumns = ["id"],
            childColumns = ["manufacturerId"]
        )
    ]
)
data class VehicleModel(
    @PrimaryKey
    val id: String,
    val manufacturerId: String,
    val name: String,
    val code: String,
    val yearStart: Int,
    val yearEnd: Int?,
    val platform: String,
    val generation: String
)