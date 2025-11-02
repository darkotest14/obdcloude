package com.obdcloud.feature.diagnostics.protocol

/**
 * Base interface for OBD protocol implementations
 */
interface OBDProtocol {
    suspend fun initialize(): Result<Boolean>
    suspend fun readPid(mode: Int, pid: Int): Result<ByteArray>
    suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean>
    suspend fun requestSeed(): Result<ByteArray>
    suspend fun sendKey(key: ByteArray): Result<Boolean>
}

/**
 * Base class for message formatting and parsing
 */
abstract class BaseProtocol : OBDProtocol {
    protected fun formatMessage(mode: Int, pid: Int): ByteArray {
        return byteArrayOf(mode.toByte(), pid.toByte())
    }
    
    protected fun formatMessage(mode: Int, pid: Int, data: ByteArray): ByteArray {
        return byteArrayOf(mode.toByte(), pid.toByte()) + data
    }
    
    protected fun checkResponse(response: ByteArray, mode: Int): Boolean {
        return response.isNotEmpty() && (response[0].toInt() and 0x40) == (mode and 0x40)
    }
    
    protected fun extractResponseData(response: ByteArray): ByteArray {
        return if (response.size > 2) response.copyOfRange(2, response.size) else byteArrayOf()
    }
}