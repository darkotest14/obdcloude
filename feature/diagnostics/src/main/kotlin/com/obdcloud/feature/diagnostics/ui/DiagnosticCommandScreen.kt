package com.obdcloud.feature.diagnostics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.obdcloud.feature.diagnostics.command.DiagnosticCommand

@Composable
fun DiagnosticCommandScreen(
    modifier: Modifier = Modifier,
    viewModel: DiagnosticCommandViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val availableCommands by viewModel.availableCommands.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Top toolbar
        SmallTopAppBar(
            title = { Text("Diagnostic Commands") },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Main content
        Box(modifier = Modifier.weight(1f)) {
            when (val currentState = state) {
                is DiagnosticCommandState.Initial -> {
                    CommandList(
                        commands = availableCommands,
                        onCommandSelect = { viewModel.executeCommand(it.id) }
                    )
                }
                is DiagnosticCommandState.Loading -> {
                    LoadingIndicator(command = currentState.command)
                }
                is DiagnosticCommandState.Success -> {
                    CommandResult(result = currentState)
                }
                is DiagnosticCommandState.Error -> {
                    ErrorDisplay(
                        message = currentState.message,
                        warnings = currentState.warnings
                    )
                }
                is DiagnosticCommandState.SafetyPrompt -> {
                    SafetyPromptDialog(
                        state = currentState,
                        onConfirm = { viewModel.confirmAndExecute(currentState.command.id) },
                        onDismiss = { /* Reset state */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommandList(
    commands: List<DiagnosticCommand>,
    onCommandSelect: (DiagnosticCommand) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(commands, key = { it.id }) { command ->
            CommandCard(
                command = command,
                onClick = { onCommandSelect(command) }
            )
        }
    }
}

@Composable
private fun CommandCard(
    command: DiagnosticCommand,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = command.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = command.description,
                style = MaterialTheme.typography.bodyMedium
            )
            if (command.requiresSecurityAccess) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Requires security access",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SafetyPromptDialog(
    state: DiagnosticCommandState.SafetyPrompt,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Safety Warning") },
        text = {
            Column {
                Text("Prerequisites:")
                state.prerequisites.forEach { prerequisite ->
                    Text("• $prerequisite")
                }
                if (state.safetyNotes != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = state.safetyNotes,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = confirmed,
                        onCheckedChange = { confirmed = it }
                    )
                    Text("I understand and accept the risks")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = confirmed
            ) {
                Text("Execute")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LoadingIndicator(
    command: DiagnosticCommand,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Executing: ${command.name}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CommandResult(
    result: DiagnosticCommandState.Success,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Command executed successfully",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        // Add response data visualization here
    }
}

@Composable
private fun ErrorDisplay(
    message: String,
    warnings: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        if (warnings.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Warnings:",
                style = MaterialTheme.typography.titleMedium
            )
            warnings.forEach { warning ->
                Text(
                    text = "• $warning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}