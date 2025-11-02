package com.obdcloud.feature.diagnostics.protocol

/**
 * Implementation of ISO 14229 (UDS) protocol
 */
class UDSProtocol(
    private val transportProtocol: OBDProtocol
) : BaseProtocol() {

    companion object {
        // Service IDs
        const val DIAGNOSTIC_SESSION_CONTROL = 0x10
        const val ECU_RESET = 0x11
        const val SECURITY_ACCESS = 0x27
        const val COMMUNICATION_CONTROL = 0x28
        const val TESTER_PRESENT = 0x3E
        const val READ_DATA_BY_IDENTIFIER = 0x22
        const val WRITE_DATA_BY_IDENTIFIER = 0x2E
        const val CLEAR_DIAGNOSTIC_INFORMATION = 0x14
        const val READ_DTC_INFORMATION = 0x19
        const val INPUT_OUTPUT_CONTROL_BY_IDENTIFIER = 0x2F
        const val ROUTINE_CONTROL = 0x31
        
        // Session types
        const val DEFAULT_SESSION = 0x01
        const val PROGRAMMING_SESSION = 0x02
        const val EXTENDED_DIAGNOSTIC_SESSION = 0x03
        const val SAFETY_SYSTEM_DIAGNOSTIC_SESSION = 0x04
        
        // Response codes
        const val POSITIVE_RESPONSE = 0x40
        const val NEGATIVE_RESPONSE = 0x7F
    }
    
    override suspend fun initialize(): Result<Boolean> {
        return transportProtocol.initialize()
    }
    
    override suspend fun readPid(mode: Int, pid: Int): Result<ByteArray> {
        return transportProtocol.readPid(mode, pid)
    }
    
    override suspend fun writePid(mode: Int, pid: Int, data: ByteArray): Result<Boolean> {
        return transportProtocol.writePid(mode, pid, data)
    }
    
    override suspend fun requestSeed(): Result<ByteArray> {
        return transportProtocol.requestSeed()
    }
    
    override suspend fun sendKey(key: ByteArray): Result<Boolean> {
        return transportProtocol.sendKey(key)
    }
    
    suspend fun startDiagnosticSession(sessionType: Int): Result<Boolean> {
        return transportProtocol.writePid(DIAGNOSTIC_SESSION_CONTROL, sessionType, byteArrayOf())
    }
    
    suspend fun ecuReset(resetType: Int): Result<Boolean> {
        return transportProtocol.writePid(ECU_RESET, resetType, byteArrayOf())
    }
    
    suspend fun readDataByIdentifier(identifier: Int): Result<ByteArray> {
        return transportProtocol.readPid(READ_DATA_BY_IDENTIFIER, identifier)
    }
    
    suspend fun writeDataByIdentifier(identifier: Int, data: ByteArray): Result<Boolean> {
        return transportProtocol.writePid(WRITE_DATA_BY_IDENTIFIER, identifier, data)
    }
    
    suspend fun clearDiagnosticInformation(groupOfDTC: Int): Result<Boolean> {
        val data = byteArrayOf(
            (groupOfDTC shr 16).toByte(),
            (groupOfDTC shr 8).toByte(),
            groupOfDTC.toByte()
        )
        return transportProtocol.writePid(CLEAR_DIAGNOSTIC_INFORMATION, 0, data)
    }
    
    suspend fun readDTCInformation(subfunction: Int): Result<ByteArray> {
        return transportProtocol.readPid(READ_DTC_INFORMATION, subfunction)
    }
    
    suspend fun inputOutputControlByIdentifier(
        identifier: Int,
        controlOption: Int,
        controlState: ByteArray
    ): Result<Boolean> {
        val data = byteArrayOf(controlOption.toByte()) + controlState
        return transportProtocol.writePid(INPUT_OUTPUT_CONTROL_BY_IDENTIFIER, identifier, data)
    }
    
    suspend fun routineControl(
        subfunction: Int,
        routineIdentifier: Int,
        routineControlOption: ByteArray = byteArrayOf()
    ): Result<ByteArray> {
        val data = byteArrayOf(subfunction.toByte()) +
                (routineIdentifier shr 8).toByte() +
                routineIdentifier.toByte() +
                routineControlOption
        
        return transportProtocol.readPid(ROUTINE_CONTROL, 0, data)
    }
    
    private suspend fun readPid(service: Int, subfunction: Int, data: ByteArray): Result<ByteArray> {
        val message = byteArrayOf(service.toByte(), subfunction.toByte()) + data
        return transportProtocol.readPid(service, 0, message)
    }
}