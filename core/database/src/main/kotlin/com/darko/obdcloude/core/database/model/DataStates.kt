package com.darko.obdcloude.core.database.model

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}

sealed class ImportResult {
    object Success : ImportResult()
    data class Error(val message: String) : ImportResult()
    data class Progress(val current: Int, val total: Int, val phase: String) : ImportResult()
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

data class DiagnosticResult<T>(
    val value: T,
    val raw: ByteArray,
    val timestamp: Long,
    val validation: ValidationResult
)