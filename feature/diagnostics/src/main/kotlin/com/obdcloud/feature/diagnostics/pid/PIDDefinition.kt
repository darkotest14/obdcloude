package com.obdcloud.feature.diagnostics.pid

/**
 * Represents a standardized OBD Parameter ID with its description and calculation formula
 */
data class PIDDefinition(
    val mode: Int,
    val pid: Int,
    val name: String,
    val description: String,
    val unit: String,
    val bytesReturned: Int,
    val formula: (ByteArray) -> Number
)

/**
 * Standard Mode 01 PID definitions with their formulas
 */
object Mode01PIDs {
    val definitions = mapOf(
        StandardPIDs.CALCULATED_ENGINE_LOAD to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.CALCULATED_ENGINE_LOAD,
            name = "LOAD_PCT",
            description = "Calculated Engine Load",
            unit = "%",
            bytesReturned = 1,
            formula = { PIDFormulas.calculateEngineLoad(it) }
        ),
        
        StandardPIDs.ENGINE_COOLANT_TEMP to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.ENGINE_COOLANT_TEMP,
            name = "ECT",
            description = "Engine Coolant Temperature",
            unit = "°C",
            bytesReturned = 1,
            formula = { PIDFormulas.calculateEngineCoolantTemp(it) }
        ),
        
        StandardPIDs.ENGINE_RPM to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.ENGINE_RPM,
            name = "RPM",
            description = "Engine RPM",
            unit = "rpm",
            bytesReturned = 2,
            formula = { PIDFormulas.calculateEngineRPM(it) }
        ),
        
        StandardPIDs.VEHICLE_SPEED to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.VEHICLE_SPEED,
            name = "VSS",
            description = "Vehicle Speed",
            unit = "km/h",
            bytesReturned = 1,
            formula = { PIDFormulas.calculateVehicleSpeed(it) }
        ),
        
        StandardPIDs.MAF_SENSOR to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.MAF_SENSOR,
            name = "MAF",
            description = "Mass Air Flow Rate",
            unit = "g/s",
            bytesReturned = 2,
            formula = { PIDFormulas.calculateMAF(it) }
        ),
        
        StandardPIDs.THROTTLE_POSITION to PIDDefinition(
            mode = 0x01,
            pid = StandardPIDs.THROTTLE_POSITION,
            name = "TP",
            description = "Throttle Position",
            unit = "%",
            bytesReturned = 1,
            formula = { PIDFormulas.calculateThrottlePosition(it) }
        )
        // Add more PID definitions as needed
    )
    
    fun getPIDDefinition(pid: Int): PIDDefinition? = definitions[pid]
}