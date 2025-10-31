package com.example.prova06.ui.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.MaintenanceRequest
import com.example.prova06.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MaintenanceUiState {
    object Idle : MaintenanceUiState()
    object Loading : MaintenanceUiState()
    object Success : MaintenanceUiState()
    data class Error(val message: String) : MaintenanceUiState()
}

class MaintenanceViewModel(
    private val repository: MaintenanceRepository = MaintenanceRepository()
) : ViewModel() {

    private val _maintenances = MutableStateFlow<List<MaintenanceRequest>>(emptyList())
    val maintenances: StateFlow<List<MaintenanceRequest>> = _maintenances.asStateFlow()

    private val _uiState = MutableStateFlow<MaintenanceUiState>(MaintenanceUiState.Idle)
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()

    init { loadMaintenances() }

    fun loadMaintenances() {
        viewModelScope.launch {
            repository.getMaintenancesFlow().collect { _maintenances.value = it }
        }
    }

    fun saveMaintenance(maintenance: MaintenanceRequest) {
        viewModelScope.launch {
            _uiState.value = MaintenanceUiState.Loading
            repository.saveMaintenance(maintenance)
                .onSuccess { _uiState.value = MaintenanceUiState.Success }
                .onFailure { _uiState.value = MaintenanceUiState.Error(it.localizedMessage ?: "Erro ao salvar") }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateStatus(id, status)
        }
    }

    fun deleteMaintenance(id: String) {
        viewModelScope.launch {
            repository.deleteMaintenance(id)
        }
    }

    fun resetState() { _uiState.value = MaintenanceUiState.Idle }
}
