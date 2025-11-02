package com.obdcloud.core.domain.model

/**
 * Represents a vehicle with its basic identification information
 */
data class Vehicle(
    val vin: String,
    val year: Int,
    val make: String,
    val model: String,
    val engine: String,
    val transmission: String,
    val supportedProtocols: List<Protocol>,
    val ecuModules: List<ECUModule>
)

/**
 * Represents an Electronic Control Unit in the vehicle
 */
data class ECUModule(
    val id: String,
    val name: String,
    val address: String,
    val protocol: Protocol,
    val supportedPids: List<String>,
    val dtcCodes: List<DTCCode>? = null
)

/**
 * Represents a Diagnostic Trouble Code
 */
data class DTCCode(
    val code: String,
    val description: String,
    val severity: DTCSeverity,
    val timestamp: Long,
    val freezeFrameData: Map<String, String>? = null
)

enum class DTCSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}