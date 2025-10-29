package com.example.prova06.ui.rentals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.Rental
import com.example.prova06.data.repository.RentalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RentalUiState {
    object Idle : RentalUiState()
    object Loading : RentalUiState()
    object Success : RentalUiState()
    data class Error(val message: String) : RentalUiState()
}

class RentalViewModel(
    private val repository: RentalRepository = RentalRepository()
) : ViewModel() {

    private val _rentals = MutableStateFlow<List<Rental>>(emptyList())
    val rentals: StateFlow<List<Rental>> = _rentals.asStateFlow()

    private val _uiState = MutableStateFlow<RentalUiState>(RentalUiState.Idle)
    val uiState: StateFlow<RentalUiState> = _uiState.asStateFlow()

    private val _selectedRental = MutableStateFlow<Rental?>(null)
    val selectedRental: StateFlow<Rental?> = _selectedRental.asStateFlow()

    init { loadRentals() }

    fun loadRentals() {
        viewModelScope.launch {
            repository.getRentalsFlow().collect { _rentals.value = it }
        }
    }

    fun loadRental(id: String) {
        viewModelScope.launch {
            _selectedRental.value = repository.getRental(id)
        }
    }

    fun saveRental(rental: Rental) {
        viewModelScope.launch {
            _uiState.value = RentalUiState.Loading
            repository.saveRental(rental)
                .onSuccess { _uiState.value = RentalUiState.Success }
                .onFailure { _uiState.value = RentalUiState.Error(it.localizedMessage ?: "Erro ao salvar") }
        }
    }

    fun deleteRental(id: String) {
        viewModelScope.launch {
            _uiState.value = RentalUiState.Loading
            repository.deleteRental(id)
                .onSuccess { _uiState.value = RentalUiState.Success }
                .onFailure { _uiState.value = RentalUiState.Error(it.localizedMessage ?: "Erro ao excluir") }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateStatus(id, status)
        }
    }

    fun resetState() { _uiState.value = RentalUiState.Idle }
}
