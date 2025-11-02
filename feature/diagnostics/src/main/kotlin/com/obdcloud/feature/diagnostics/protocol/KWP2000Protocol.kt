package com.obdcloud.feature.diagnostics.protocol

/**
 * Implementation of KWP2000 (ISO 14230-4) protocol
 */
class KWP2000Protocol(
    private val sendCommand: suspend (ByteArray) -> Result<ByteArray>
) : BaseProtocol() {

    companion object {
        // Header format bytes
        private const val HEADER_LENGTH = 0x81.toByte()  // Format byte, physical addressing
        
        // Service IDs
        const val START_DIAGNOSTIC_SESSION = 0x10
        const val ECU_RESET = 0x11
        const val READ_ECU_IDENTIFICATION = 0x1A
        const val READ_DATA_BY_LOCAL_IDENTIFIER = 0x21
        const val READ_DATA_BY_COMMON_IDENTIFIER = 0x22
        const val READ_MEMORY_BY_ADDRESS = 0x23
        const val SECURITY_ACCESS = 0x27
        const val READ_DIAGNOSTIC_TROUBLE_CODES = 0x13
        const val CLEAR_DIAGNOSTIC_TROUBLE_CODES = 0x14
        const val READ_STATUS_OF_DTCS = 0x17
        
        // Session types
        const val STANDARD_SESSION = 0x81.toByte()
        const val PROGRAMMING_SESSION = 0x85.toByte()
        const val DEVELOPMENT_SESSION = 0x86.toByte()
        const val ADJUSTMENT_SESSION = 0x87.toByte()
    }
    
    override suspend fun initialize(): Result<Boolean> {
        // Fast initialization sequence for KWP2000
        val initSequence = byteArrayOf(0xC1.toByte(), HEADER_LENGTH)
        return sendCommand(initSequence).map { response ->
            response.isNotEmpty() && response[0] == 0x50.toByte()
        }
    }
    
    override suspend fun readPid(mode: Int, pid: Int): Result<ByteArray> {
        val message = formatKWPMessage(mode, pid)
        return sendCommand(message)
    }
    
    override suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean> {
        val message = formatKWPMessage(mode, pid, data)
        return sendCommand(message).map { response ->
            checkResponse(response, mode)
        }
    }
    
    override suspend fun requestSeed(): Result<ByteArray> {
        val message = formatKWPMessage(SECURITY_ACCESS, 0x01)
        return sendCommand(message)
    }
    
    override suspend fun sendKey(key: ByteArray): Result<Boolean> {
        val message = formatKWPMessage(SECURITY_ACCESS, 0x02, key)
        return sendCommand(message).map { response ->
            checkResponse(response, SECURITY_ACCESS)
        }
    }
    
    suspend fun startDiagnosticSession(sessionType: Byte): Result<Boolean> {
        val message = formatKWPMessage(START_DIAGNOSTIC_SESSION, sessionType.toInt())
        return sendCommand(message).map { response ->
            checkResponse(response, START_DIAGNOSTIC_SESSION)
        }
    }
    
    suspend fun readECUIdentification(dataIdentifier: Int): Result<ByteArray> {
        val message = formatKWPMessage(READ_ECU_IDENTIFICATION, dataIdentifier)
        return sendCommand(message)
    }
    
    suspend fun readDiagnosticTroubleCodes(): Result<ByteArray> {
        val message = formatKWPMessage(READ_DIAGNOSTIC_TROUBLE_CODES, 0x00)
        return sendCommand(message)
    }
    
    suspend fun clearDiagnosticTroubleCodes(): Result<Boolean> {
        val message = formatKWPMessage(CLEAR_DIAGNOSTIC_TROUBLE_CODES, 0x00)
        return sendCommand(message).map { response ->
            checkResponse(response, CLEAR_DIAGNOSTIC_TROUBLE_CODES)
        }
    }
    
    private fun formatKWPMessage(service: Int, parameter: Int, data: ByteArray = byteArrayOf()): ByteArray {
        // Format: Length + Target + Source + Service + Parameter + Data + Checksum
        val messageLength = 4 + data.size // Not including length byte and checksum
        val target = 0x33.toByte() // ECU address
        val source = 0xF1.toByte() // Tester address
        
        val message = ByteArray(messageLength + 2) // +2 for length and checksum
        message[0] = messageLength.toByte()
        message[1] = target
        message[2] = source
        message[3] = service.toByte()
        message[4] = parameter.toByte()
        
        if (data.isNotEmpty()) {
            System.arraycopy(data, 0, message, 5, data.size)
        }
        
        // Calculate checksum
        message[message.size - 1] = calculateChecksum(message)
        
        return message
    }
    
    private fun calculateChecksum(data: ByteArray): Byte {
        var sum = 0
        for (i in 0 until data.size - 1) {
            sum += data[i].toInt() and 0xFF
        }
        return (sum and 0xFF).toByte()
    }
}