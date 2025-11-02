package com.obdcloud.feature.adapter.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.obdcloud.core.domain.model.ConnectionStatus
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.model.Protocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class BluetoothSppAdapterDataSource @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter
) : AdapterDataSource {

    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override fun discover(): Flow<List<OBDAdapter>> = callbackFlow {
        val pairedDevices = bluetoothAdapter.bondedDevices
        val adapters = pairedDevices.map { device ->
            OBDAdapter(
                id = device.address,
                name = device.name ?: "Unknown SPP Device",
                macAddress = device.address,
                ipAddress = null,
                port = null,
                connectionType = ConnectionType.BLUETOOTH_SPP,
                status = ConnectionStatus.DISCONNECTED,
                supportedProtocols = listOf(
                    Protocol.ISO_15765_4_CAN,
                    Protocol.ISO_14230_4_KWP,
                    Protocol.ISO_9141_2
                )
            )
        }
        
        trySend(adapters.toList())
        
        awaitClose { }
    }

    override suspend fun connect(adapter: OBDAdapter): Result<OBDAdapter> = 
        suspendCancellableCoroutine { continuation ->
            try {
                val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(adapter.macAddress)
                withContext(Dispatchers.IO) {
                    socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket?.connect()
                    
                    inputStream = socket?.inputStream
                    outputStream = socket?.outputStream
                    
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
                inputStream?.close()
                outputStream?.close()
                socket?.close()
            } catch (e: Exception) {
                // Log error
            } finally {
                socket = null
                inputStream = null
                outputStream = null
            }
        }
    }

    override suspend fun sendCommand(command: String): Result<String> = 
        suspendCancellableCoroutine { continuation ->
            try {
                withContext(Dispatchers.IO) {
                    outputStream?.write((command + "\r").toByteArray())
                    outputStream?.flush()
                    
                    // Read response
                    val buffer = ByteArray(1024)
                    val bytes = inputStream?.read(buffer)
                    val response = bytes?.let { 
                        String(buffer, 0, it).trim()
                    } ?: ""
                    
                    continuation.resume(Result.success(response))
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
}