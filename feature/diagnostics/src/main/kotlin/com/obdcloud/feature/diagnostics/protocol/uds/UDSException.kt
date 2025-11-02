package com.obdcloud.feature.diagnostics.protocol.uds

/**
 * UDS negative response codes as defined in ISO-14229
 */
object NegativeResponseCode {
    const val GENERAL_REJECT = 0x10
    const val SERVICE_NOT_SUPPORTED = 0x11
    const val SUB_FUNCTION_NOT_SUPPORTED = 0x12
    const val INCORRECT_MESSAGE_LENGTH = 0x13
    const val CONDITIONS_NOT_CORRECT = 0x22
    const val REQUEST_SEQUENCE_ERROR = 0x24
    const val REQUEST_OUT_OF_RANGE = 0x31
    const val SECURITY_ACCESS_DENIED = 0x33
    const val INVALID_KEY = 0x35
    const val EXCEEDED_NUMBER_OF_ATTEMPTS = 0x36
    const val REQUIRED_TIME_DELAY_NOT_EXPIRED = 0x37
    const val UPLOAD_DOWNLOAD_NOT_ACCEPTED = 0x70
    const val TRANSFER_DATA_SUSPENDED = 0x71
    const val GENERAL_PROGRAMMING_FAILURE = 0x72
    const val WRONG_BLOCK_SEQUENCE_COUNTER = 0x73
    const val RESPONSE_PENDING = 0x78
    const val SUB_FUNCTION_NOT_SUPPORTED_IN_ACTIVE_SESSION = 0x7E
    const val SERVICE_NOT_SUPPORTED_IN_ACTIVE_SESSION = 0x7F
    
    fun getMessage(code: Int): String = when(code) {
        GENERAL_REJECT -> "General reject"
        SERVICE_NOT_SUPPORTED -> "Service not supported"
        SUB_FUNCTION_NOT_SUPPORTED -> "Sub-function not supported"
        INCORRECT_MESSAGE_LENGTH -> "Incorrect message length"
        CONDITIONS_NOT_CORRECT -> "Conditions not correct"
        REQUEST_SEQUENCE_ERROR -> "Request sequence error"
        REQUEST_OUT_OF_RANGE -> "Request out of range"
        SECURITY_ACCESS_DENIED -> "Security access denied"
        INVALID_KEY -> "Invalid key"
        EXCEEDED_NUMBER_OF_ATTEMPTS -> "Exceeded number of attempts"
        REQUIRED_TIME_DELAY_NOT_EXPIRED -> "Required time delay not expired"
        UPLOAD_DOWNLOAD_NOT_ACCEPTED -> "Upload/download not accepted"
        TRANSFER_DATA_SUSPENDED -> "Transfer data suspended"
        GENERAL_PROGRAMMING_FAILURE -> "General programming failure"
        WRONG_BLOCK_SEQUENCE_COUNTER -> "Wrong block sequence counter"
        RESPONSE_PENDING -> "Response pending"
        SUB_FUNCTION_NOT_SUPPORTED_IN_ACTIVE_SESSION -> "Sub-function not supported in active session"
        SERVICE_NOT_SUPPORTED_IN_ACTIVE_SESSION -> "Service not supported in active session"
        else -> "Unknown error code: 0x${code.toString(16).uppercase()}"
    }
}

/**
 * Exception thrown for UDS protocol errors
 */
class UDSException(
    val service: Int,
    val nrc: Int,
    cause: Throwable? = null
) : Exception(
    "UDS service 0x${service.toString(16).uppercase()} failed: ${NegativeResponseCode.getMessage(nrc)}",
    cause
)