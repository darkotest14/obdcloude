package com.obdcloud.feature.diagnostics.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Protocol type supported by diagnostic commands
 */
enum class CommandProtocol {
    OBD2,
    UDS,
    CUSTOM
}

/**
 * Represents vehicle state requirements for command execution
 */
@Serializable
data class VehiclePrerequisite(
    val condition: String,
    val value: String,
    val description: String
)

/**
 * Represents a diagnostic command with all metadata
 */
@Serializable
data class DiagnosticCommand(
    val id: String,
    val name: String,
    val description: String,
    
    @SerialName("protocol")
    val protocolType: CommandProtocol,
    
    val service: Int,
    val subFunction: Int? = null,
    val did: Int? = null,
    val routineId: Int? = null,
    
    val payloadTemplate: List<Int> = emptyList(),
    val responseParser: String? = null,
    
    val requiresSecurityAccess: Boolean = false,
    val securityLevel: Int? = null,
    
    val prerequisites: List<VehiclePrerequisite> = emptyList(),
    val safetyNotes: String? = null,
    
    @SerialName("manufacturer")
    val manufacturerName: String? = null,
    val model: String? = null,
    val year: IntRange? = null,
    
    val timeout: Long = 3000,
    val retries: Int = 1
)

/**
 * Groups commands by manufacturer and functionality
 */
@Serializable
data class CommandGroup(
    val groupId: String,
    val name: String,
    val description: String,
    val commands: List<DiagnosticCommand>
)

/**
 * Complete command database structure
 */
@Serializable
data class CommandDatabase(
    val version: String,
    val groups: List<CommandGroup>
)