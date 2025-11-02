package com.obdcloud.feature.garage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obdcloud.core.domain.model.Vehicle
import com.obdcloud.core.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow<GarageUiState>(GarageUiState.Loading)
    val state: StateFlow<GarageUiState> = _state.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            try {
                val vehicles = vehicleRepository.getAllVehicles()
                _state.value = GarageUiState.Success(vehicles)
            } catch (e: Exception) {
                _state.value = GarageUiState.Error("Failed to load vehicles: ${e.message}")
            }
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicle(vehicle)
                loadVehicles()
            } catch (e: Exception) {
                _state.value = GarageUiState.Error("Failed to delete vehicle: ${e.message}")
            }
        }
    }
}

sealed interface GarageUiState {
    data object Loading : GarageUiState
    data class Success(val vehicles: List<Vehicle>) : GarageUiState
    data class Error(val message: String) : GarageUiState
}