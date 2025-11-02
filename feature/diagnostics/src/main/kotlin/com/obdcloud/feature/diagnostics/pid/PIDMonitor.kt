package com.obdcloud.feature.diagnostics.pid

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.Instant

/**
 * Handles live monitoring of PIDs with configurable sampling rate
 */
class PIDMonitor(
    private val protocol: OBDProtocol,
    private val samplingRate: Duration = Duration.ofMillis(100)
) {
    private val _monitoredPids = mutableSetOf<MonitoredPID>()
    private val _pidValues = MutableStateFlow<Map<Int, PIDResponse>>(emptyMap())
    
    val pidValues: StateFlow<Map<Int, PIDResponse>> = _pidValues.asStateFlow()

    /**
     * Add a PID to monitor with optional value range validation
     */
    fun addPID(
        mode: Int,
        pid: Int,
        minValue: Number? = null,
        maxValue: Number? = null,
        onRangeExceeded: ((PIDResponse) -> Unit)? = null
    ) {
        _monitoredPids.add(
            MonitoredPID(
                mode = mode,
                pid = pid,
                minValue = minValue,
                maxValue = maxValue,
                onRangeExceeded = onRangeExceeded
            )
        )
    }

    /**
     * Remove a PID from monitoring
     */
    fun removePID(pid: Int) {
        _monitoredPids.removeIf { it.pid == pid }
    }

    /**
     * Start monitoring all added PIDs at the configured sampling rate
     */
    suspend fun startMonitoring() {
        while (true) {
            _monitoredPids.forEach { monitoredPid ->
                try {
                    val response = protocol.sendRequest(monitoredPid.mode, monitoredPid.pid)
                    PIDResponseFactory.createResponse(monitoredPid.mode, monitoredPid.pid, response)?.let { pidResponse ->
                        validateAndUpdateValue(monitoredPid, pidResponse)
                    }
                } catch (e: Exception) {
                    // Log error but continue monitoring other PIDs
                }
            }
            kotlinx.coroutines.delay(samplingRate.toMillis())
        }
    }

    private fun validateAndUpdateValue(monitoredPid: MonitoredPID, response: PIDResponse) {
        val value = response.calculatedValue.toDouble()
        
        // Check if value is within configured range
        if ((monitoredPid.minValue != null && value < monitoredPid.minValue.toDouble()) ||
            (monitoredPid.maxValue != null && value > monitoredPid.maxValue.toDouble())) {
            monitoredPid.onRangeExceeded?.invoke(response)
        }

        // Update the StateFlow with new value
        _pidValues.value = _pidValues.value + (monitoredPid.pid to response)
    }

    private data class MonitoredPID(
        val mode: Int,
        val pid: Int,
        val minValue: Number?,
        val maxValue: Number?,
        val onRangeExceeded: ((PIDResponse) -> Unit)?
    )
}