package com.obdcloud.feature.diagnostics.protocol

/**
 * Implementation of ISO 9141-2 (K-Line) protocol
 */
class ISO9141Protocol(
    private val sendCommand: suspend (ByteArray) -> Result<ByteArray>
) : BaseProtocol() {

    companion object {
        // Protocol constants
        private const val K_LINE_BAUD_RATE = 10400
        private const val INIT_ADDRESS = 0x33
        private const val TESTER_ADDRESS = 0xF1
        private const val ECU_ADDRESS = 0x11
        
        // Timing constants (milliseconds)
        private const val P1_MIN = 0    // Inter byte time for ECU response
        private const val P1_MAX = 20   // Maximum time for ECU response
        private const val P2_MIN = 25   // Time between tester request and ECU response
        private const val P2_MAX = 50   // Maximum time between tester request and ECU response
        private const val P3_MIN = 55   // Time between ECU response and next tester request
        private const val P3_MAX = 5000 // Maximum time between messages
        private const val P4_MIN = 5    // Inter byte time for tester request
        private const val P4_MAX = 20   // Maximum inter byte time
        
        // Message format bytes
        private const val START_COMMUNICATION = 0x81
        private const val STOP_COMMUNICATION = 0x82
        private const val KEEP_ALIVE = 0x3E
    }
    
    override suspend fun initialize(): Result<Boolean> {
        // 5-baud initialization sequence
        val initSequence = byteArrayOf(
            0x33.toByte(),  // Address byte at 5 baud
            INIT_ADDRESS.toByte(),
            TESTER_ADDRESS.toByte()
        )
        
        return sendCommand(initSequence).map { response ->
            response.size >= 3 && 
            response[0] == 0x55.toByte() && // Sync pattern
            (response[1] == 0x08.toByte() || response[1] == 0x94.toByte()) && // Key bytes
            response[2] == INIT_ADDRESS.toByte() // Inverted address
        }
    }
    
    override suspend fun readPid(mode: Int, pid: Int): Result<ByteArray> {
        val message = formatISO9141Message(mode, pid)
        return sendCommand(message)
    }
    
    override suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean> {
        val message = formatISO9141Message(mode, pid, data)
        return sendCommand(message).map { response ->
            checkResponse(response, mode)
        }
    }
    
    override suspend fun requestSeed(): Result<ByteArray> {
        // ISO 9141 security access is implementation specific
        // This is a generic implementation
        val message = formatISO9141Message(0x27, 0x01)
        return sendCommand(message)
    }
    
    override suspend fun sendKey(key: ByteArray): Result<Boolean> {
        val message = formatISO9141Message(0x27, 0x02, key)
        return sendCommand(message).map { response ->
            response.isNotEmpty() && response[0] == 0x67.toByte()
        }
    }
    
    suspend fun startCommunication(): Result<Boolean> {
        val message = byteArrayOf(START_COMMUNICATION.toByte())
        return sendCommand(message).map { response ->
            response.isNotEmpty() && response[0] == 0xC1.toByte()
        }
    }
    
    suspend fun stopCommunication(): Result<Boolean> {
        val message = byteArrayOf(STOP_COMMUNICATION.toByte())
        return sendCommand(message).map { response ->
            response.isNotEmpty() && response[0] == 0xC2.toByte()
        }
    }
    
    suspend fun keepAlive(): Result<Boolean> {
        val message = byteArrayOf(KEEP_ALIVE.toByte())
        return sendCommand(message).map { response ->
            response.isNotEmpty() && response[0] == 0x7E.toByte()
        }
    }
    
    private fun formatISO9141Message(
        mode: Int,
        pid: Int,
        data: ByteArray = byteArrayOf()
    ): ByteArray {
        // Format: Header + Length + Mode + PID + Data + Checksum
        val messageLength = 1 + 1 + data.size // Mode + PID + Data
        val header = byteArrayOf(
            TESTER_ADDRESS.toByte(),
            ECU_ADDRESS.toByte(),
            messageLength.toByte()
        )
        
        val message = ByteArray(header.size + messageLength + 1) // +1 for checksum
        System.arraycopy(header, 0, message, 0, header.size)
        message[header.size] = mode.toByte()
        message[header.size + 1] = pid.toByte()
        
        if (data.isNotEmpty()) {
            System.arraycopy(data, 0, message, header.size + 2, data.size)
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