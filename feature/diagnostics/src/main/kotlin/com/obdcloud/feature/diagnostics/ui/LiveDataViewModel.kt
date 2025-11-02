package com.obdcloud.feature.diagnostics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obdcloud.feature.diagnostics.pid.PIDMonitor
import com.obdcloud.feature.diagnostics.pid.PIDResponse
import com.obdcloud.feature.diagnostics.pid.StandardPIDs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

/**
 * ViewModel for handling live PID data monitoring and UI state
 */
@HiltViewModel
class LiveDataViewModel @Inject constructor(
    private val pidMonitor: PIDMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow<LiveDataUiState>(LiveDataUiState.Initial)
    val uiState: StateFlow<LiveDataUiState> = _uiState.asStateFlow()

    private val _selectedPids = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPids: StateFlow<Set<Int>> = _selectedPids.asStateFlow()

    init {
        viewModelScope.launch {
            pidMonitor.pidValues.collect { values ->
                _uiState.value = LiveDataUiState.Data(values)
            }
        }
    }

    /**
     * Start monitoring a specific PID
     */
    fun startMonitoring(pid: Int, minValue: Number? = null, maxValue: Number? = null) {
        pidMonitor.addPID(
            mode = 0x01, 
            pid = pid,
            minValue = minValue,
            maxValue = maxValue
        ) { response ->
            // Handle out of range values
            viewModelScope.launch {
                _uiState.value = LiveDataUiState.Warning(
                    "Value out of range for ${response.name}: ${response.calculatedValue} ${response.unit}"
                )
            }
        }
        _selectedPids.value = _selectedPids.value + pid
        
        if (_selectedPids.value.size == 1) {
            // Start monitoring when first PID is added
            viewModelScope.launch {
                pidMonitor.startMonitoring()
            }
        }
    }

    /**
     * Stop monitoring a specific PID
     */
    fun stopMonitoring(pid: Int) {
        pidMonitor.removePID(pid)
        _selectedPids.value = _selectedPids.value - pid
    }

    /**
     * Common engine PIDs for quick monitoring
     */
    fun monitorEngineData() {
        listOf(
            StandardPIDs.ENGINE_RPM to null,
            StandardPIDs.VEHICLE_SPEED to null,
            StandardPIDs.ENGINE_COOLANT_TEMP to 120, // Max temp warning at 120°C
            StandardPIDs.CALCULATED_ENGINE_LOAD to null,
            StandardPIDs.MAF_SENSOR to null,
            StandardPIDs.THROTTLE_POSITION to null
        ).forEach { (pid, maxValue) ->
            startMonitoring(pid, maxValue = maxValue)
        }
    }
}

/**
 * UI state for live data monitoring
 */
sealed class LiveDataUiState {
    data object Initial : LiveDataUiState()
    data class Data(val values: Map<Int, PIDResponse>) : LiveDataUiState()
    data class Warning(val message: String) : LiveDataUiState()
    data class Error(val message: String) : LiveDataUiState()
}