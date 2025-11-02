package com.obdcloud.core.domain.repository

import com.obdcloud.core.domain.model.Vehicle
import com.obdcloud.core.domain.model.DTCCode
import com.obdcloud.core.domain.model.ECUModule
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for vehicle diagnostics operations
 */
interface DiagnosticsRepository {
    /**
     * Read the vehicle's VIN number
     */
    suspend fun readVIN(): Result<String>
    
    /**
     * Perform a full system scan of all ECUs
     */
    suspend fun performSystemScan(): Flow<List<ECUModule>>
    
    /**
     * Read DTCs from a specific ECU module
     */
    suspend fun readDTCs(module: ECUModule): Result<List<DTCCode>>
    
    /**
     * Clear DTCs from a specific ECU module
     */
    suspend fun clearDTCs(module: ECUModule): Result<Boolean>
    
    /**
     * Read a specific PID value from an ECU
     */
    suspend fun readPID(module: ECUModule, pid: String): Result<String>
    
    /**
     * Start a live data stream for specified PIDs
     */
    suspend fun startLiveData(module: ECUModule, pids: List<String>): Flow<Map<String, String>>
    
    /**
     * Perform a specific service function
     */
    suspend fun performServiceFunction(
        module: ECUModule,
        functionId: String,
        parameters: Map<String, String>
    ): Result<Boolean>
    
    /**
     * Save vehicle profile with scan results
     */
    suspend fun saveVehicleProfile(vehicle: Vehicle): Result<Vehicle>
    
    /**
     * Get saved vehicle profiles
     */
    fun getSavedVehicles(): Flow<List<Vehicle>>
}