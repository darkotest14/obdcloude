package com.obdcloud.feature.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.settingsDataStore

    val bluetoothEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[BLUETOOTH_ENABLED] ?: true }

    val wifiEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[WIFI_ENABLED] ?: true }

    val darkMode: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[DARK_MODE] ?: false }

    suspend fun setBluetoothEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BLUETOOTH_ENABLED] = enabled
        }
    }

    suspend fun setWifiEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[WIFI_ENABLED] = enabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }

    companion object {
        private val BLUETOOTH_ENABLED = booleanPreferencesKey("bluetooth_enabled")
        private val WIFI_ENABLED = booleanPreferencesKey("wifi_enabled")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }
}