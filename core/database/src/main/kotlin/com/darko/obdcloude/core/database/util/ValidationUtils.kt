package com.darko.obdcloude.core.database.util

import com.darko.obdcloude.core.database.model.*

object ValidationUtils {
    fun validateParameter(parameter: DiagnosticParameter): ValidationResult {
        return when {
            parameter.pid.isBlank() -> ValidationResult.Invalid("PID cannot be empty")
            parameter.bytesCount <= 0 -> ValidationResult.Invalid("Invalid bytes count")
            parameter.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            parameter.minValue != null && parameter.maxValue != null && 
                parameter.minValue > parameter.maxValue -> 
                    ValidationResult.Invalid("Min value cannot be greater than max value")
            else -> ValidationResult.Valid
        }
    }

    fun validateCommand(command: DiagnosticCommand): ValidationResult {
        return when {
            command.serviceId < 0 -> ValidationResult.Invalid("Invalid service ID")
            command.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            command.securityLevelRequired < 0 -> ValidationResult.Invalid("Invalid security level")
            command.timeout <= 0 -> ValidationResult.Invalid("Invalid timeout value")
            else -> ValidationResult.Valid
        }
    }

    fun validateEngine(engine: VehicleEngine): ValidationResult {
        return when {
            engine.code.isBlank() -> ValidationResult.Invalid("Engine code cannot be empty")
            engine.displacement <= 0 -> ValidationResult.Invalid("Invalid displacement")
            engine.power < 0 -> ValidationResult.Invalid("Invalid power value")
            engine.ecuType.isBlank() -> ValidationResult.Invalid("ECU type cannot be empty")
            else -> ValidationResult.Valid
        }
    }

    fun validateEcuSystem(ecuSystem: EcuSystem): ValidationResult {
        return when {
            ecuSystem.code.isBlank() -> ValidationResult.Invalid("ECU code cannot be empty")
            ecuSystem.supplier.isBlank() -> ValidationResult.Invalid("Supplier cannot be empty")
            ecuSystem.version.isBlank() -> ValidationResult.Invalid("Version cannot be empty")
            ecuSystem.protocol.isBlank() -> ValidationResult.Invalid("Protocol cannot be empty")
            ecuSystem.securityLevel < 0 -> ValidationResult.Invalid("Invalid security level")
            else -> ValidationResult.Valid
        }
    }
}