package com.obdcloud.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                val bluetoothEnabled by viewModel.bluetoothEnabled.collectAsState()
                val wifiEnabled by viewModel.wifiEnabled.collectAsState()
                val darkMode by viewModel.darkMode.collectAsState()
                
                SwitchPreference(
                    title = "Enable Bluetooth",
                    description = "Allow OBD adapters to connect via Bluetooth",
                    checked = bluetoothEnabled,
                    onCheckedChange = viewModel::setBluetoothEnabled
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SwitchPreference(
                    title = "Enable WiFi",
                    description = "Allow OBD adapters to connect via WiFi",
                    checked = wifiEnabled,
                    onCheckedChange = viewModel::setWifiEnabled
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SwitchPreference(
                    title = "Dark Mode",
                    description = "Enable dark theme",
                    checked = darkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("1.0.0") }
                )
                
                ListItem(
                    headlineContent = { Text("Build Number") },
                    supportingContent = { Text("1") }
                )
            }
        }
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}