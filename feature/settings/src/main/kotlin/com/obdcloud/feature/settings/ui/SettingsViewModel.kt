package com.obdcloud.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obdcloud.feature.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val bluetoothEnabled: StateFlow<Boolean> = settingsRepository.bluetoothEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val wifiEnabled: StateFlow<Boolean> = settingsRepository.wifiEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val darkMode: StateFlow<Boolean> = settingsRepository.darkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun setBluetoothEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBluetoothEnabled(enabled)
        }
    }

    fun setWifiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setWifiEnabled(enabled)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }
}