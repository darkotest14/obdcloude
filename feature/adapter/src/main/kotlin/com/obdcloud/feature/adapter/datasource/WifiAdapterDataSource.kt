package com.obdcloud.feature.adapter.datasource

import com.obdcloud.core.domain.model.ConnectionStatus
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.model.Protocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import kotlin.coroutines.resume

class WifiAdapterDataSource @Inject constructor() : AdapterDataSource {
    
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null
    
    override fun discover(): Flow<List<OBDAdapter>> = flow {
        // Note: For WiFi adapters, we typically don't discover them automatically.
        // Instead, the user needs to provide the IP address and port.
        // This could be enhanced with mDNS/Bonjour discovery if the adapters support it.
        emit(emptyList())
    }
    
    override suspend fun connect(adapter: OBDAdapter): Result<OBDAdapter> = 
        suspendCancellableCoroutine { continuation ->
            try {
                withContext(Dispatchers.IO) {
                    socket = Socket()
                    socket?.connect(
                        InetSocketAddress(adapter.ipAddress, adapter.port ?: 35000),
                        5000 // 5 second timeout
                    )
                    
                    reader = BufferedReader(InputStreamReader(socket?.inputStream))
                    writer = PrintWriter(socket?.outputStream, true)
                    
                    if (socket?.isConnected == true) {
                        val connectedAdapter = adapter.copy(
                            status = ConnectionStatus.CONNECTED
                        )
                        continuation.resume(Result.success(connectedAdapter))
                    } else {
                        continuation.resume(Result.failure(Exception("Failed to connect to adapter")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
            
            continuation.invokeOnCancellation {
                disconnect(adapter)
            }
        }
    
    override suspend fun disconnect(adapter: OBDAdapter) {
        withContext(Dispatchers.IO) {
            try {
                reader?.close()
                writer?.close()
                socket?.close()
            } catch (e: Exception) {
                // Log error
            } finally {
                socket = null
                reader = null
                writer = null
            }
        }
    }
    
    override suspend fun sendCommand(command: String): Result<String> = 
        suspendCancellableCoroutine { continuation ->
            try {
                withContext(Dispatchers.IO) {
                    writer?.println(command)
                    writer?.flush()
                    
                    val response = reader?.readLine() ?: ""
                    continuation.resume(Result.success(response))
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    
    companion object {
        fun createAdapter(ipAddress: String, port: Int): OBDAdapter {
            return OBDAdapter(
                id = "$ipAddress:$port",
                name = "WiFi OBD Adapter",
                macAddress = null,
                ipAddress = ipAddress,
                port = port,
                connectionType = ConnectionType.WIFI_TCP,
                status = ConnectionStatus.DISCONNECTED,
                supportedProtocols = listOf(
                    Protocol.ISO_15765_4_CAN,
                    Protocol.ISO_14230_4_KWP
                )
            )
        }
    }
}