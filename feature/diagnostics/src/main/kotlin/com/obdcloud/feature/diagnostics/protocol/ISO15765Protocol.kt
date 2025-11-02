package com.obdcloud.feature.diagnostics.protocol

/**
 * Implementation of ISO 15765-4 (CAN) protocol
 */
class ISO15765Protocol(
    private val sendCommand: suspend (ByteArray) -> Result<ByteArray>
) : BaseProtocol() {

    companion object {
        private const val FLOW_CONTROL_CLEAR_TO_SEND = 0x30.toByte()
        private const val FLOW_CONTROL_WAIT = 0x31.toByte()
        private const val FLOW_CONTROL_OVERFLOW = 0x32.toByte()
        
        private const val SINGLE_FRAME = 0x00
        private const val FIRST_FRAME = 0x10
        private const val CONSECUTIVE_FRAME = 0x20
        private const val FLOW_CONTROL_FRAME = 0x30
        
        private const val MAX_DATA_LENGTH = 7
        private const val TIMEOUT_MS = 5000L
    }
    
    override suspend fun initialize(): Result<Boolean> {
        // Set up CAN parameters (500kbps, 11-bit ID)
        val setupCommand = byteArrayOf(0xC1.toByte(), 0x33.toByte())
        return sendCommand(setupCommand).map { response ->
            response.isNotEmpty() && response[0] == 0x50.toByte()
        }
    }
    
    override suspend fun readPid(mode: Int, pid: Int): Result<ByteArray> {
        val message = formatMessage(mode, pid)
        return sendCanMessage(message)
    }
    
    override suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean> {
        val message = formatMessage(mode, pid, data)
        return sendCanMessage(message).map { response ->
            checkResponse(response, mode)
        }
    }
    
    override suspend fun requestSeed(): Result<ByteArray> {
        // Security Access Level 1 request seed
        val message = byteArrayOf(0x27.toByte(), 0x01.toByte())
        return sendCanMessage(message)
    }
    
    override suspend fun sendKey(key: ByteArray): Result<Boolean> {
        // Security Access Level 1 send key
        val message = byteArrayOf(0x27.toByte(), 0x02.toByte()) + key
        return sendCanMessage(message).map { response ->
            response.isNotEmpty() && response[0] == 0x67.toByte()
        }
    }
    
    private suspend fun sendCanMessage(data: ByteArray): Result<ByteArray> {
        return if (data.size <= MAX_DATA_LENGTH) {
            // Single frame
            sendSingleFrame(data)
        } else {
            // Multi frame
            sendMultiFrame(data)
        }
    }
    
    private suspend fun sendSingleFrame(data: ByteArray): Result<ByteArray> {
        val frame = ByteArray(1 + data.size)
        frame[0] = SINGLE_FRAME.toByte()
        System.arraycopy(data, 0, frame, 1, data.size)
        
        return sendCommand(frame)
    }
    
    private suspend fun sendMultiFrame(data: ByteArray): Result<ByteArray> {
        // First frame
        val firstFrame = ByteArray(MAX_DATA_LENGTH + 1)
        firstFrame[0] = (FIRST_FRAME or (data.size shr 8)).toByte()
        firstFrame[1] = (data.size and 0xFF).toByte()
        System.arraycopy(data, 0, firstFrame, 2, MAX_DATA_LENGTH - 1)
        
        val firstResult = sendCommand(firstFrame).getOrElse {
            return Result.failure(it)
        }
        
        if (!checkFlowControl(firstResult)) {
            return Result.failure(Exception("Flow control error"))
        }
        
        // Consecutive frames
        var offset = MAX_DATA_LENGTH - 1
        var sequenceNumber = 1
        
        while (offset < data.size) {
            val remaining = data.size - offset
            val length = minOf(MAX_DATA_LENGTH, remaining)
            
            val frame = ByteArray(length + 1)
            frame[0] = (CONSECUTIVE_FRAME or (sequenceNumber and 0x0F)).toByte()
            System.arraycopy(data, offset, frame, 1, length)
            
            sendCommand(frame).getOrElse {
                return Result.failure(it)
            }
            
            offset += length
            sequenceNumber = (sequenceNumber + 1) and 0x0F
        }
        
        return Result.success(firstResult)
    }
    
    private fun checkFlowControl(response: ByteArray): Boolean {
        return response.isNotEmpty() && response[0] == FLOW_CONTROL_CLEAR_TO_SEND
    }
}