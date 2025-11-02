package com.darko.obdcloude.core.database.repository

import com.darko.obdcloude.core.database.dao.VehicleDao
import com.darko.obdcloude.core.database.dao.DiagnosticDao
import com.darko.obdcloude.core.database.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleDatabaseRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val diagnosticDao: DiagnosticDao
) {
    // Vehicle hierarchy queries
    fun getAllManufacturers() = vehicleDao.getAllManufacturers()
    fun getModelsForManufacturer(manufacturerId: String) = vehicleDao.getModelsForManufacturer(manufacturerId)
    fun getEnginesForModel(modelId: String) = vehicleDao.getEnginesForModel(modelId)
    fun getEcuSystemsForEngine(engineId: String) = vehicleDao.getEcuSystemsForEngine(engineId)
    
    // Platform specific queries
    fun getModelsForPlatform(platform: String) = vehicleDao.getModelsForPlatform(platform)
    fun getEcuSystemsByType(type: String) = vehicleDao.getEcuSystemsByType(type)
    
    // Diagnostic data queries
    fun getParametersForEcuSystem(ecuSystemId: String) = diagnosticDao.getParametersForEcuSystem(ecuSystemId)
    fun getCommandsForEcuSystem(ecuSystemId: String) = diagnosticDao.getCommandsForEcuSystem(ecuSystemId)
    
    // Parameter lookup
    suspend fun getParameterByPid(pid: String) = diagnosticDao.getParameterByPid(pid)
    suspend fun getCommandByService(serviceId: Int, subFunction: Int?) = 
        diagnosticDao.getCommandByService(serviceId, subFunction)
    
    // Filtered queries
    fun getParametersByCodes(ecuSystemId: String, codes: List<String>) = 
        diagnosticDao.getParametersByCodes(ecuSystemId, codes)
    fun getCommandsBySecurityLevel(securityLevel: Int) = 
        diagnosticDao.getCommandsBySecurityLevel(securityLevel)
    
    // Data insertion
    suspend fun insertManufacturer(manufacturer: VehicleManufacturer) = 
        vehicleDao.insertManufacturer(manufacturer)
    suspend fun insertModel(model: VehicleModel) = 
        vehicleDao.insertModel(model)
    suspend fun insertEngine(engine: VehicleEngine) = 
        vehicleDao.insertEngine(engine)
    suspend fun insertEcuSystem(ecuSystem: EcuSystem) = 
        vehicleDao.insertEcuSystem(ecuSystem)
    suspend fun insertParameter(parameter: DiagnosticParameter) = 
        diagnosticDao.insertParameter(parameter)
    suspend fun insertCommand(command: DiagnosticCommand) = 
        diagnosticDao.insertCommand(command)
}