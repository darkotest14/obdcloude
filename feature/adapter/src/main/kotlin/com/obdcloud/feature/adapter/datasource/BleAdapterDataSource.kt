package com.obdcloud.feature.adapter.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.obdcloud.core.domain.model.ConnectionStatus
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.model.Protocol
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class BleAdapterDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
) : AdapterDataSource {

    private var gatt: BluetoothGatt? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null
    private var rxCharacteristic: BluetoothGattCharacteristic? = null

    override fun discover(): Flow<List<OBDAdapter>> = callbackFlow {
        val scanner = bluetoothAdapter.bluetoothLeScanner
        val scanResults = mutableListOf<OBDAdapter>()
        
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val adapter = OBDAdapter(
                    id = device.address,
                    name = device.name ?: "Unknown BLE Device",
                    macAddress = device.address,
                    ipAddress = null,
                    port = null,
                    connectionType = ConnectionType.BLUETOOTH_LE,
                    status = ConnectionStatus.DISCONNECTED,
                    supportedProtocols = listOf(Protocol.ISO_15765_4_CAN) // Default support
                )
                
                if (!scanResults.any { it.id == adapter.id }) {
                    scanResults.add(adapter)
                    trySend(scanResults.toList())
                }
            }
        }
        
        scanner.startScan(scanCallback)
        
        awaitClose {
            scanner.stopScan(scanCallback)
        }
    }

    override suspend fun connect(adapter: OBDAdapter): Result<OBDAdapter> = 
        suspendCancellableCoroutine { continuation ->
            val device = bluetoothAdapter.getRemoteDevice(adapter.macAddress)
            
            val gattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    when (newState) {
                        BluetoothGatt.STATE_CONNECTED -> {
                            gatt.discoverServices()
                        }
                        BluetoothGatt.STATE_DISCONNECTED -> {
                            continuation.resume(
                                Result.failure(Exception("Failed to connect to adapter"))
                            )
                        }
                    }
                }
                
                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        findOBDCharacteristics(gatt.services)?.let { (tx, rx) ->
                            this@BleAdapterDataSource.gatt = gatt
                            txCharacteristic = tx
                            rxCharacteristic = rx
                            
                            val connectedAdapter = adapter.copy(
                                status = ConnectionStatus.CONNECTED
                            )
                            continuation.resume(Result.success(connectedAdapter))
                        } ?: continuation.resume(
                            Result.failure(Exception("OBD characteristics not found"))
                        )
                    } else {
                        continuation.resume(
                            Result.failure(Exception("Failed to discover services"))
                        )
                    }
                }
            }
            
            device.connectGatt(context, false, gattCallback)
            
            continuation.invokeOnCancellation {
                gatt?.disconnect()
                gatt?.close()
            }
        }

    override suspend fun disconnect(adapter: OBDAdapter) {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        txCharacteristic = null
        rxCharacteristic = null
    }

    override suspend fun sendCommand(command: String): Result<String> = 
        suspendCancellableCoroutine { continuation ->
            val gattCallback = object : BluetoothGattCallback() {
                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        // Enable notifications for response
                        gatt.setCharacteristicNotification(rxCharacteristic, true)
                    } else {
                        continuation.resume(Result.failure(Exception("Failed to write command")))
                    }
                }
                
                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic
                ) {
                    val response = characteristic.value.toString(Charsets.UTF_8)
                    continuation.resume(Result.success(response))
                }
            }
            
            gatt?.let { gatt ->
                txCharacteristic?.let { tx ->
                    tx.value = command.toByteArray(Charsets.UTF_8)
                    gatt.writeCharacteristic(tx)
                } ?: continuation.resume(Result.failure(Exception("TX characteristic not found")))
            } ?: continuation.resume(Result.failure(Exception("Not connected to adapter")))
            
            continuation.invokeOnCancellation {
                gatt?.setCharacteristicNotification(rxCharacteristic, false)
            }
        }

    private fun findOBDCharacteristics(
        services: List<BluetoothGattService>
    ): Pair<BluetoothGattCharacteristic, BluetoothGattCharacteristic>? {
        // Note: Replace these UUIDs with your actual OBD adapter's service and characteristic UUIDs
        val OBD_SERVICE_UUID = "0000FFE0-0000-1000-8000-00805F9B34FB"
        val OBD_CHARACTERISTIC_TX_UUID = "0000FFE1-0000-1000-8000-00805F9B34FB"
        val OBD_CHARACTERISTIC_RX_UUID = "0000FFE1-0000-1000-8000-00805F9B34FB"
        
        services.forEach { service ->
            if (service.uuid.toString() == OBD_SERVICE_UUID) {
                val tx = service.getCharacteristic(android.bluetooth.BluetoothUUID.fromString(OBD_CHARACTERISTIC_TX_UUID))
                val rx = service.getCharacteristic(android.bluetooth.BluetoothUUID.fromString(OBD_CHARACTERISTIC_RX_UUID))
                if (tx != null && rx != null) {
                    return Pair(tx, rx)
                }
            }
        }
        return null
    }
}