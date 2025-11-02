package com.obdcloud.feature.adapter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.obdcloud.core.domain.model.ConnectionStatus
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.OBDAdapter

@Composable
fun AdapterScreen(
    viewModel: AdapterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val adapters by viewModel.availableAdapters.collectAsState()
    val connectedAdapter by viewModel.connectedAdapter.collectAsState()
    var showAddWifiDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OBD Adapters") },
                actions = {
                    IconButton(onClick = { viewModel.startScan(ConnectionType.BLUETOOTH_LE) }) {
                        Icon(Icons.Default.Bluetooth, "Scan BLE")
                    }
                    IconButton(onClick = { viewModel.startScan(ConnectionType.BLUETOOTH_SPP) }) {
                        Icon(Icons.Default.Refresh, "Scan Bluetooth")
                    }
                    IconButton(onClick = { showAddWifiDialog = true }) {
                        Icon(Icons.Default.Wifi, "Add WiFi")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is AdapterUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is AdapterUiState.Error -> {
                    Text(
                        text = (uiState as AdapterUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                
                is AdapterUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Connected adapter section
                        connectedAdapter?.let { adapter ->
                            ConnectedAdapterCard(
                                adapter = adapter,
                                onDisconnect = { viewModel.disconnectAdapter() }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // Available adapters list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(adapters.filter { it.status == ConnectionStatus.DISCONNECTED }) { adapter ->
                                AdapterCard(
                                    adapter = adapter,
                                    onClick = { viewModel.connectAdapter(adapter) }
                                )
                            }
                        }
                    }
                }
            }
            
            if (showAddWifiDialog) {
                AddWifiAdapterDialog(
                    onAdd = { ip, port ->
                        viewModel.addWifiAdapter(ip, port)
                        showAddWifiDialog = false
                    },
                    onDismiss = { showAddWifiDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdapterCard(
    adapter: OBDAdapter,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (adapter.connectionType) {
                    ConnectionType.BLUETOOTH_LE -> Icons.Default.Bluetooth
                    ConnectionType.BLUETOOTH_SPP -> Icons.Default.Bluetooth
                    ConnectionType.WIFI_TCP -> Icons.Default.Wifi
                    else -> Icons.Default.Add
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = adapter.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = adapter.macAddress ?: adapter.ipAddress ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedAdapterCard(
    adapter: OBDAdapter,
    onDisconnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (adapter.connectionType) {
                    ConnectionType.BLUETOOTH_LE -> Icons.Default.Bluetooth
                    ConnectionType.BLUETOOTH_SPP -> Icons.Default.Bluetooth
                    ConnectionType.WIFI_TCP -> Icons.Default.Wifi
                    else -> Icons.Default.Add
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = adapter.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            IconButton(onClick = onDisconnect) {
                Icon(Icons.Default.Close, "Disconnect")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWifiAdapterDialog(
    onAdd: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var ipAddress by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("35000") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add WiFi Adapter") },
        text = {
            Column {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    port.toIntOrNull()?.let { portNum ->
                        onAdd(ipAddress, portNum)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}