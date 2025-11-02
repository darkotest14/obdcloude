package com.obdcloud.core.domain.repository

import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.Protocol
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing OBD adapter connections
 */
interface AdapterRepository {
    /**
     * Discover available adapters of the specified connection type
     */
    suspend fun discoverAdapters(type: ConnectionType): Flow<List<OBDAdapter>>
    
    /**
     * Connect to a specific adapter
     */
    suspend fun connectAdapter(adapter: OBDAdapter): Result<OBDAdapter>
    
    /**
     * Disconnect from the currently connected adapter
     */
    suspend fun disconnectAdapter(adapter: OBDAdapter)
    
    /**
     * Get the currently connected adapter if any
     */
    fun getConnectedAdapter(): Flow<OBDAdapter?>
    
    /**
     * Initialize communication with the vehicle using the specified protocol
     */
    suspend fun initializeProtocol(protocol: Protocol): Result<Boolean>
    
    /**
     * Send a raw command to the adapter
     */
    suspend fun sendCommand(command: String): Result<String>
}