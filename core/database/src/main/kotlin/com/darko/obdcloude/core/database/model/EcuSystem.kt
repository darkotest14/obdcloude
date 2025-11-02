package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ecu_systems",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEngine::class,
            parentColumns = ["id"],
            childColumns = ["engineId"]
        )
    ]
)
data class EcuSystem(
    @PrimaryKey
    val id: String,
    val engineId: String,
    val name: String,
    val code: String,
    val type: EcuType,
    val supplier: String,
    val version: String,
    val protocol: String,
    val securityLevel: Int,
    val diagnosticFeatures: List<String>
)

enum class EcuType {
    ENGINE_CONTROL,
    TRANSMISSION,
    AIRBAG,
    BODY_CONTROL,
    ABS_ESP,
    CLIMATE_CONTROL,
    INSTRUMENT_CLUSTER,
    STEERING,
    SUSPENSION
}