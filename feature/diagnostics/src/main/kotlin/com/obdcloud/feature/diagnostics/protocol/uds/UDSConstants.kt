package com.obdcloud.feature.diagnostics.protocol.uds

/**
 * UDS services as defined in ISO-14229
 */
object UDSServices {
    const val DIAGNOSTIC_SESSION_CONTROL = 0x10
    const val SECURITY_ACCESS = 0x27
    const val READ_DATA_BY_IDENTIFIER = 0x22
    const val WRITE_DATA_BY_IDENTIFIER = 0x2E
    const val ROUTINE_CONTROL = 0x31
    const val TESTER_PRESENT = 0x3E
    const val ECU_RESET = 0x11
    const val CLEAR_DTC = 0x14
    const val READ_DTC = 0x19
    
    // Positive response = request service + 0x40
    fun isPositiveResponse(service: Int, response: Int): Boolean {
        return response == (service + 0x40)
    }
}

/**
 * UDS diagnostic session types
 */
object DiagnosticSessions {
    const val DEFAULT = 0x01
    const val PROGRAMMING = 0x02
    const val EXTENDED = 0x03
    const val SAFETY_SYSTEM = 0x04
}

/**
 * UDS security access levels
 */
object SecurityLevels {
    const val REQUEST_SEED = 0x01
    const val SEND_KEY = 0x02
    const val EXTENDED_REQUEST_SEED = 0x05
    const val EXTENDED_SEND_KEY = 0x06
}

/**
 * UDS routine control types
 */
object RoutineControlTypes {
    const val START = 0x01
    const val STOP = 0x02
    const val REQUEST_RESULTS = 0x03
}