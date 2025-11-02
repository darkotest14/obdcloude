package com.obdcloud.feature.diagnostics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obdcloud.feature.diagnostics.command.CommandDatabase
import com.obdcloud.feature.diagnostics.command.CommandExecutor
import com.obdcloud.feature.diagnostics.command.CommandResult
import com.obdcloud.feature.diagnostics.command.DiagnosticCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for diagnostic commands
 */
sealed class DiagnosticCommandState {
    data object Initial : DiagnosticCommandState()
    data class Loading(val command: DiagnosticCommand) : DiagnosticCommandState()
    data class Success(val result: CommandResult) : DiagnosticCommandState()
    data class Error(val message: String, val warnings: List<String> = emptyList()) : DiagnosticCommandState()
    data class SafetyPrompt(
        val command: DiagnosticCommand,
        val prerequisites: List<String>,
        val safetyNotes: String?
    ) : DiagnosticCommandState()
}

/**
 * ViewModel for handling diagnostic command execution
 */
@HiltViewModel
class DiagnosticCommandViewModel @Inject constructor(
    private val commandExecutor: CommandExecutor,
    private val commandDatabase: CommandDatabase
) : ViewModel() {

    private val _state = MutableStateFlow<DiagnosticCommandState>(DiagnosticCommandState.Initial)
    val state: StateFlow<DiagnosticCommandState> = _state.asStateFlow()

    private val _availableCommands = MutableStateFlow<List<DiagnosticCommand>>(emptyList())
    val availableCommands: StateFlow<List<DiagnosticCommand>> = _availableCommands.asStateFlow()

    init {
        _availableCommands.value = commandDatabase.groups.flatMap { it.commands }
    }

    /**
     * Initiates command execution with safety checks
     */
    fun executeCommand(commandId: String, bypassSafety: Boolean = false) {
        val command = commandDatabase.groups
            .flatMap { it.commands }
            .find { it.id == commandId } ?: run {
                _state.value = DiagnosticCommandState.Error("Command not found")
                return
            }

        // Show safety prompt for dangerous operations
        if (!bypassSafety && (command.requiresSecurityAccess || command.safetyNotes != null)) {
            _state.value = DiagnosticCommandState.SafetyPrompt(
                command = command,
                prerequisites = command.prerequisites.map { it.description },
                safetyNotes = command.safetyNotes
            )
            return
        }

        viewModelScope.launch {
            _state.value = DiagnosticCommandState.Loading(command)
            
            try {
                commandExecutor.executeCommand(commandId).collect { result ->
                    _state.value = if (result.success) {
                        DiagnosticCommandState.Success(result)
                    } else {
                        DiagnosticCommandState.Error(
                            message = result.error ?: "Unknown error",
                            warnings = result.warnings
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = DiagnosticCommandState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Confirm and execute a command after safety prompt
     */
    fun confirmAndExecute(commandId: String) {
        executeCommand(commandId, bypassSafety = true)
    }

    /**
     * Get commands for a specific group
     */
    fun getCommandsByGroup(groupId: String): List<DiagnosticCommand> {
        return commandDatabase.groups
            .find { it.groupId == groupId }
            ?.commands ?: emptyList()
    }

    /**
     * Filter commands by manufacturer, model, and year
     */
    fun filterCommands(
        manufacturer: String? = null,
        model: String? = null,
        year: Int? = null
    ) {
        _availableCommands.value = commandDatabase.groups
            .flatMap { it.commands }
            .filter { command ->
                (manufacturer == null || command.manufacturerName == manufacturer) &&
                (model == null || command.model == null || command.model == model) &&
                (year == null || command.year == null || year in command.year)
            }
    }
}