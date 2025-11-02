package com.obdcloud.feature.diagnostics.protocol

import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.repository.AdapterRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolManager @Inject constructor(
    private val adapterRepository: AdapterRepository,
    private val protocolFactory: ProtocolFactory
) {
    private var currentProtocol: OBDProtocol? = null
    private var udsProtocol: UDSProtocol? = null
    
    suspend fun initializeProtocol(): Result<Boolean> {
        val adapter = adapterRepository.getConnectedAdapter().firstOrNull()
            ?: return Result.failure(Exception("No adapter connected"))
        
        return initializeProtocol(adapter)
    }
    
    suspend fun initializeProtocol(adapter: OBDAdapter): Result<Boolean> {
        val protocol = adapter.supportedProtocols.firstOrNull()
            ?: return Result.failure(Exception("No supported protocols found"))
        
        currentProtocol = protocolFactory.createProtocol(protocol) { command ->
            adapterRepository.sendCommand(String(command)).map { response ->
                response.toByteArray()
            }
        }
        
        return currentProtocol?.initialize() ?: Result.failure(Exception("Failed to initialize protocol"))
    }
    
    suspend fun readPid(mode: Int, pid: Int): Result<ByteArray> {
        return currentProtocol?.readPid(mode, pid)
            ?: Result.failure(Exception("Protocol not initialized"))
    }
    
    suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean> {
        return currentProtocol?.writePid(mode, pid, data)
            ?: Result.failure(Exception("Protocol not initialized"))
    }
    
    suspend fun getUDSProtocol(): Result<UDSProtocol> {
        val protocol = currentProtocol
            ?: return Result.failure(Exception("Base protocol not initialized"))
        
        if (udsProtocol == null) {
            udsProtocol = protocolFactory.createUDSProtocol(protocol)
        }
        
        return Result.success(udsProtocol!!)
    }
    
    fun clearProtocols() {
        currentProtocol = null
        udsProtocol = null
    }
}