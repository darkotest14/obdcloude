package com.darko.obdcloude.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diagnostic_commands",
    foreignKeys = [
        ForeignKey(
            entity = EcuSystem::class,
            parentColumns = ["id"],
            childColumns = ["ecuSystemId"]
        )
    ]
)
data class DiagnosticCommand(
    @PrimaryKey
    val id: String,
    val ecuSystemId: String,
    val name: String,
    val code: String,
    val serviceId: Int,
    val subFunction: Int?,
    val description: String,
    val securityLevelRequired: Int,
    val parameterCount: Int,
    val responseLength: Int,
    val timeout: Int,
    val conditions: List<String>?,
    val validationRules: List<String>?
)