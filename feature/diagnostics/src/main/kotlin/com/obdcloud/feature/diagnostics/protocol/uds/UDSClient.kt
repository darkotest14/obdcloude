package com.obdcloud.feature.diagnostics.protocol.uds

import com.obdcloud.feature.diagnostics.protocol.TransportLayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import java.io.IOException

class UDSClient(
    private val transport: TransportLayer
) {
    private var currentSession: Int = DiagnosticSessions.DEFAULT
    private var securityLevel: Int = 0

    /**
     * Sends a UDS request and waits for response
     */
    suspend fun sendRequest(
        service: Int,
        payload: ByteArray = byteArrayOf(),
        timeoutMs: Long = 3000L
    ): ByteArray = withTimeout(timeoutMs) {
        try {
            val request = byteArrayOf(service.toByte()) + payload
            transport.write(request)
            val response = transport.readResponse()
            
            // Check for negative response
            if (response.isNotEmpty() && response[0] == 0x7F.toByte()) {
                throw UDSException(
                    service = service,
                    nrc = if (response.size > 2) response[2].toInt() else 0
                )
            }
            
            // Verify positive response
            if (!UDSServices.isPositiveResponse(service, response[0].toInt())) {
                throw UDSException(service, NegativeResponseCode.GENERAL_REJECT)
            }
            
            response
        } catch (e: IOException) {
            throw UDSException(service, NegativeResponseCode.GENERAL_REJECT, e)
        } catch (e: CancellationException) {
            throw e  // Let cancellation propagate
        } catch (e: Exception) {
            throw UDSException(service, NegativeResponseCode.GENERAL_REJECT, e)
        }
    }

    /**
     * Changes diagnostic session
     */
    suspend fun changeDiagnosticSession(session: Int): Boolean {
        val response = sendRequest(
            UDSServices.DIAGNOSTIC_SESSION_CONTROL,
            byteArrayOf(session.toByte())
        )
        if (response.size > 1 && response[1].toInt() == session) {
            currentSession = session
            return true
        }
        return false
    }

    /**
     * Performs security access handshake
     */
    suspend fun performSecurityAccess(
        level: Int,
        keyCalculator: (ByteArray) -> ByteArray
    ): Boolean {
        // Request seed
        val seedResponse = sendRequest(
            UDSServices.SECURITY_ACCESS,
            byteArrayOf(level.toByte())
        )
        
        if (seedResponse.size <= 2) return false
        
        // Calculate and send key
        val seed = seedResponse.copyOfRange(2, seedResponse.size)
        val key = keyCalculator(seed)
        
        val keyResponse = sendRequest(
            UDSServices.SECURITY_ACCESS,
            byteArrayOf((level + 1).toByte()) + key
        )
        
        if (UDSServices.isPositiveResponse(UDSServices.SECURITY_ACCESS, keyResponse[0].toInt())) {
            securityLevel = level
            return true
        }
        return false
    }

    /**
     * Reads data by identifier (DID)
     */
    suspend fun readDataByIdentifier(did: Int): ByteArray {
        val hi = ((did shr 8) and 0xFF).toByte()
        val lo = (did and 0xFF).toByte()
        return sendRequest(UDSServices.READ_DATA_BY_IDENTIFIER, byteArrayOf(hi, lo))
    }

    /**
     * Writes data by identifier (DID)
     */
    suspend fun writeDataByIdentifier(did: Int, data: ByteArray): Boolean {
        val hi = ((did shr 8) and 0xFF).toByte()
        val lo = (did and 0xFF).toByte()
        val response = sendRequest(
            UDSServices.WRITE_DATA_BY_IDENTIFIER,
            byteArrayOf(hi, lo) + data
        )
        return response.isNotEmpty() && 
               UDSServices.isPositiveResponse(UDSServices.WRITE_DATA_BY_IDENTIFIER, response[0].toInt())
    }

    /**
     * Controls a routine
     */
    suspend fun routineControl(
        type: Int,
        routineId: Int,
        data: ByteArray = byteArrayOf()
    ): ByteArray {
        val hi = ((routineId shr 8) and 0xFF).toByte()
        val lo = (routineId and 0xFF).toByte()
        return sendRequest(
            UDSServices.ROUTINE_CONTROL,
            byteArrayOf(type.toByte(), hi, lo) + data
        )
    }

    /**
     * Keeps session alive by sending TesterPresent
     */
    suspend fun sendTesterPresent() {
        sendRequest(UDSServices.TESTER_PRESENT, byteArrayOf(0x00))
    }

    /**
     * Resets the ECU
     */
    suspend fun resetECU(type: Int = 0x01): Boolean {
        val response = sendRequest(UDSServices.ECU_RESET, byteArrayOf(type.toByte()))
        return response.isNotEmpty() && 
               UDSServices.isPositiveResponse(UDSServices.ECU_RESET, response[0].toInt())
    }
}