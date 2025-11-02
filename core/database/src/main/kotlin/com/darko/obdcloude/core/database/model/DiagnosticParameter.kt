package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diagnostic_parameters",
    foreignKeys = [
        ForeignKey(
            entity = EcuSystem::class,
            parentColumns = ["id"],
            childColumns = ["ecuSystemId"]
        )
    ]
)
data class DiagnosticParameter(
    @PrimaryKey
    val id: String,
    val ecuSystemId: String,
    val name: String,
    val code: String,
    val pid: String,
    val description: String,
    val unit: String,
    val formula: String?,
    val minValue: Float?,
    val maxValue: Float?,
    val bytesCount: Int,
    val offset: Int,
    val factor: Float,
    val signed: Boolean
)