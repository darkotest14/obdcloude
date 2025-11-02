package com.obdcloud.feature.diagnostics.command

import com.obdcloud.feature.diagnostics.protocol.uds.DiagnosticSessions
import com.obdcloud.feature.diagnostics.protocol.uds.UDSClient
import com.obdcloud.feature.diagnostics.protocol.uds.UDSServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

/**
 * Result of a diagnostic command execution
 */
data class CommandResult(
    val commandId: String,
    val success: Boolean,
    val timestamp: Instant = Instant.now(),
    val rawResponse: ByteArray? = null,
    val error: String? = null,
    val warnings: List<String> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommandResult) return false
        return commandId == other.commandId &&
                success == other.success &&
                timestamp == other.timestamp &&
                rawResponse?.contentEquals(other.rawResponse) == true &&
                error == other.error &&
                warnings == other.warnings
    }

    override fun hashCode(): Int {
        var result = commandId.hashCode()
        result = 31 * result + success.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (rawResponse?.contentHashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + warnings.hashCode()
        return result
    }
}

/**
 * Service for executing diagnostic commands
 */
class CommandExecutor @Inject constructor(
    private val udsClient: UDSClient,
    private val commandDatabase: CommandDatabase,
    private val vehicleStateMonitor: VehicleStateMonitor
) {
    /**
     * Executes a diagnostic command and returns result flow
     */
    fun executeCommand(commandId: String): Flow<CommandResult> = flow {
        val command = findCommand(commandId) ?: run {
            emit(CommandResult(commandId, false, error = "Command not found"))
            return@flow
        }

        // Check prerequisites
        val prerequisites = command.prerequisites
        val failed = prerequisites.filter { prereq ->
            !vehicleStateMonitor.checkCondition(prereq.condition, prereq.value)
        }
        
        if (failed.isNotEmpty()) {
            emit(CommandResult(
                commandId = commandId,
                success = false,
                error = "Prerequisites not met",
                warnings = failed.map { "${it.description}: ${it.condition} must be ${it.value}" }
            ))
            return@flow
        }

        try {
            // Enter extended session if needed
            if (command.requiresSecurityAccess || command.service != UDSServices.READ_DATA_BY_IDENTIFIER) {
                udsClient.changeDiagnosticSession(DiagnosticSessions.EXTENDED)
            }

            // Perform security access if needed
            if (command.requiresSecurityAccess) {
                command.securityLevel?.let { level ->
                    val securitySuccess = udsClient.performSecurityAccess(level) { seed ->
                        calculateSecurityKey(seed, level)
                    }
                    if (!securitySuccess) {
                        emit(CommandResult(commandId, false, error = "Security access denied"))
                        return@flow
                    }
                }
            }

            // Execute command based on type
            val response = when {
                command.routineId != null -> {
                    udsClient.routineControl(
                        command.subFunction ?: 1,
                        command.routineId,
                        command.payloadTemplate.map { it.toByte() }.toByteArray()
                    )
                }
                command.did != null -> {
                    if (command.service == UDSServices.WRITE_DATA_BY_IDENTIFIER) {
                        val success = udsClient.writeDataByIdentifier(
                            command.did,
                            command.payloadTemplate.map { it.toByte() }.toByteArray()
                        )
                        if (success) byteArrayOf(0x6E) else byteArrayOf()
                    } else {
                        udsClient.readDataByIdentifier(command.did)
                    }
                }
                else -> {
                    udsClient.sendRequest(
                        command.service,
                        command.payloadTemplate.map { it.toByte() }.toByteArray(),
                        command.timeout
                    )
                }
            }

            emit(CommandResult(commandId, true, rawResponse = response))

        } catch (e: Exception) {
            emit(CommandResult(commandId, false, error = e.message))
        }
    }

    private fun findCommand(commandId: String): DiagnosticCommand? {
        return commandDatabase.groups
            .flatMap { it.commands }
            .find { it.id == commandId }
    }

    // This should be implemented according to manufacturer requirements
    private fun calculateSecurityKey(seed: ByteArray, level: Int): ByteArray {
        // TODO: Implement proper security key calculation
        return ByteArray(4) { 0x00 }
    }
}