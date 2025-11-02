package com.obdcloud.feature.diagnostics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.obdcloud.feature.diagnostics.pid.StandardPIDs

@Composable
fun LiveDataScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveDataViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPids by viewModel.selectedPids.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Quick actions
        QuickActions(
            onEngineDataClick = { viewModel.monitorEngineData() }
        )

        // Main content
        when (val state = uiState) {
            is LiveDataUiState.Data -> {
                LiveDataGrid(
                    values = state.values,
                    selectedPids = selectedPids,
                    onPidSelect = { viewModel.startMonitoring(it) },
                    onPidDeselect = { viewModel.stopMonitoring(it) }
                )
            }
            is LiveDataUiState.Warning -> {
                AlertMessage(message = state.message, severity = AlertSeverity.Warning)
            }
            is LiveDataUiState.Error -> {
                AlertMessage(message = state.message, severity = AlertSeverity.Error)
            }
            LiveDataUiState.Initial -> {
                InitialPrompt()
            }
        }
    }
}

@Composable
private fun QuickActions(
    onEngineDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton(onClick = onEngineDataClick) {
            Text("Monitor Engine Data")
        }
        // Add more quick action buttons as needed
    }
}

@Composable
private fun LiveDataGrid(
    values: Map<Int, PIDResponse>,
    selectedPids: Set<Int>,
    onPidSelect: (Int) -> Unit,
    onPidDeselect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = values.values.toList(),
            key = { it.pid }
        ) { response ->
            PIDCard(
                response = response,
                isSelected = response.pid in selectedPids,
                onToggle = { pid ->
                    if (pid in selectedPids) onPidDeselect(pid)
                    else onPidSelect(pid)
                }
            )
        }
    }
}

@Composable
private fun PIDCard(
    response: PIDResponse,
    isSelected: Boolean,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onToggle(response.pid) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = response.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${response.calculatedValue} ${response.unit}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = response.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AlertMessage(
    message: String,
    severity: AlertSeverity,
    modifier: Modifier = Modifier
) {
    val color = when (severity) {
        AlertSeverity.Warning -> MaterialTheme.colorScheme.errorContainer
        AlertSeverity.Error -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = color,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun InitialPrompt(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Select PIDs to monitor or use quick actions above",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private enum class AlertSeverity {
    Warning,
    Error
}