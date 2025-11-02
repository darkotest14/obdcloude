package com.obdcloud.feature.diagnostics.command

import com.obdcloud.feature.diagnostics.pid.PIDMonitor
import com.obdcloud.feature.diagnostics.pid.StandardPIDs
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors vehicle state and validates command prerequisites
 */
@Singleton
class VehicleStateMonitor @Inject constructor(
    private val pidMonitor: PIDMonitor
) {
    private val stateCache = ConcurrentHashMap<String, Any>()

    init {
        // Start monitoring essential PIDs
        startMonitoring()
    }

    private fun startMonitoring() {
        listOf(
            StandardPIDs.ENGINE_RPM,
            StandardPIDs.VEHICLE_SPEED,
            StandardPIDs.ENGINE_COOLANT_TEMP,
            StandardPIDs.CALCULATED_ENGINE_LOAD
        ).forEach { pid ->
            pidMonitor.addPID(0x01, pid)
        }
    }

    /**
     * Checks if a condition is met based on the latest vehicle state
     */
    suspend fun checkCondition(condition: String, value: String): Boolean {
        return when (condition) {
            "engine_running" -> checkEngineRunning(value.toBoolean())
            "ignition" -> checkIgnition(value)
            "vehicle_speed" -> checkVehicleSpeed(value.toDouble())
            "engine_temp", "coolant_temp" -> checkEngineTemp(value)
            "battery_voltage" -> checkBatteryVoltage(value)
            "transmission_temp" -> checkTransmissionTemp(value)
            else -> false
        }
    }

    private suspend fun checkEngineRunning(required: Boolean): Boolean {
        val rpm = getLatestValue(StandardPIDs.ENGINE_RPM)?.calculatedValue as? Number
        return if (required) (rpm?.toInt() ?: 0) > 400 else (rpm?.toInt() ?: 0) < 50
    }

    private fun checkIgnition(state: String): Boolean {
        // TODO: Implement proper ignition state check
        return true
    }

    private suspend fun checkVehicleSpeed(maxSpeed: Double): Boolean {
        val speed = getLatestValue(StandardPIDs.VEHICLE_SPEED)?.calculatedValue as? Number
        return (speed?.toDouble() ?: 0.0) <= maxSpeed
    }

    private suspend fun checkEngineTemp(value: String): Boolean {
        val temp = getLatestValue(StandardPIDs.ENGINE_COOLANT_TEMP)?.calculatedValue as? Number
        return when {
            value.startsWith(">") -> (temp?.toDouble() ?: 0.0) > value.substring(1).toDouble()
            value.startsWith("<") -> (temp?.toDouble() ?: 0.0) < value.substring(1).toDouble()
            value.contains("-") -> {
                val (min, max) = value.split("-").map { it.toDouble() }
                val current = temp?.toDouble() ?: 0.0
                current in min..max
            }
            else -> (temp?.toDouble() ?: 0.0) == value.toDouble()
        }
    }

    private fun checkBatteryVoltage(value: String): Boolean {
        // TODO: Implement proper battery voltage check
        return true
    }

    private fun checkTransmissionTemp(value: String): Boolean {
        // TODO: Implement transmission temperature check
        return true
    }

    private suspend fun getLatestValue(pid: Int) = 
        pidMonitor.pidValues.firstOrNull()?.get(pid)
}