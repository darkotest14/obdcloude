package com.obdcloud.feature.adapter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obdcloud.core.domain.model.ConnectionStatus
import com.obdcloud.core.domain.model.ConnectionType
import com.obdcloud.core.domain.model.OBDAdapter
import com.obdcloud.core.domain.repository.AdapterRepository
import com.obdcloud.feature.adapter.datasource.WifiAdapterDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdapterViewModel @Inject constructor(
    private val adapterRepository: AdapterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdapterUiState>(AdapterUiState.Loading)
    val uiState: StateFlow<AdapterUiState> = _uiState.asStateFlow()
    
    private val _availableAdapters = MutableStateFlow<List<OBDAdapter>>(emptyList())
    val availableAdapters: StateFlow<List<OBDAdapter>> = _availableAdapters.asStateFlow()
    
    private val _connectedAdapter = MutableStateFlow<OBDAdapter?>(null)
    val connectedAdapter: StateFlow<OBDAdapter?> = _connectedAdapter.asStateFlow()
    
    init {
        observeConnectedAdapter()
    }
    
    private fun observeConnectedAdapter() {
        adapterRepository.getConnectedAdapter()
            .onEach { adapter ->
                _connectedAdapter.value = adapter
                _uiState.value = AdapterUiState.Success
            }
            .catch { e ->
                _uiState.value = AdapterUiState.Error(e.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }
    
    fun startScan(type: ConnectionType) {
        viewModelScope.launch {
            _uiState.value = AdapterUiState.Loading
            
            adapterRepository.discoverAdapters(type)
                .onEach { adapters ->
                    _availableAdapters.value = adapters
                    _uiState.value = AdapterUiState.Success
                }
                .catch { e ->
                    _uiState.value = AdapterUiState.Error(e.message ?: "Failed to scan for adapters")
                }
                .launchIn(this)
        }
    }
    
    fun connectAdapter(adapter: OBDAdapter) {
        viewModelScope.launch {
            _uiState.value = AdapterUiState.Loading
            
            adapterRepository.connectAdapter(adapter)
                .onSuccess { connectedAdapter ->
                    _connectedAdapter.value = connectedAdapter
                    _uiState.value = AdapterUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = AdapterUiState.Error(e.message ?: "Failed to connect to adapter")
                }
        }
    }
    
    fun disconnectAdapter() {
        viewModelScope.launch {
            _connectedAdapter.value?.let { adapter ->
                _uiState.value = AdapterUiState.Loading
                adapterRepository.disconnectAdapter(adapter)
                _connectedAdapter.value = adapter.copy(status = ConnectionStatus.DISCONNECTED)
                _uiState.value = AdapterUiState.Success
            }
        }
    }
    
    fun addWifiAdapter(ipAddress: String, port: Int) {
        val adapter = WifiAdapterDataSource.createAdapter(ipAddress, port)
        _availableAdapters.value = _availableAdapters.value + adapter
    }
}

sealed class AdapterUiState {
    object Loading : AdapterUiState()
    object Success : AdapterUiState()
    data class Error(val message: String) : AdapterUiState()
}