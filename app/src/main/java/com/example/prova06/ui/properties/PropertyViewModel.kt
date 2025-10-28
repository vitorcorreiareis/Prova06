package com.example.prova06.ui.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.Property
import com.example.prova06.data.remote.RetrofitClient
import com.example.prova06.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PropertyUiState {
    object Idle : PropertyUiState()
    object Loading : PropertyUiState()
    object Success : PropertyUiState()
    data class Error(val message: String) : PropertyUiState()
}

class PropertyViewModel(
    private val repository: PropertyRepository = PropertyRepository()
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _uiState = MutableStateFlow<PropertyUiState>(PropertyUiState.Idle)
    val uiState: StateFlow<PropertyUiState> = _uiState.asStateFlow()

    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty: StateFlow<Property?> = _selectedProperty.asStateFlow()

    private val _cepLoading = MutableStateFlow(false)
    val cepLoading: StateFlow<Boolean> = _cepLoading.asStateFlow()

    private val _addressFromCep = MutableStateFlow<Map<String, String>>(emptyMap())
    val addressFromCep: StateFlow<Map<String, String>> = _addressFromCep.asStateFlow()

    init { loadProperties() }

    fun loadProperties() {
        viewModelScope.launch {
            repository.getPropertiesFlow().collect { _properties.value = it }
        }
    }

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _selectedProperty.value = repository.getProperty(id)
        }
    }

    fun saveProperty(property: Property) {
        viewModelScope.launch {
            _uiState.value = PropertyUiState.Loading
            repository.saveProperty(property)
                .onSuccess { _uiState.value = PropertyUiState.Success }
                .onFailure { _uiState.value = PropertyUiState.Error(it.localizedMessage ?: "Erro ao salvar") }
        }
    }

    fun deleteProperty(id: String) {
        viewModelScope.launch {
            _uiState.value = PropertyUiState.Loading
            repository.deleteProperty(id)
                .onSuccess { _uiState.value = PropertyUiState.Success }
                .onFailure { _uiState.value = PropertyUiState.Error(it.localizedMessage ?: "Erro ao excluir") }
        }
    }

    fun fetchAddressByCep(cep: String) {
        val digits = cep.replace("[^0-9]".toRegex(), "")
        if (digits.length != 8) return
        viewModelScope.launch {
            _cepLoading.value = true
            runCatching { RetrofitClient.viaCepService.getAddressByCep(digits) }
                .onSuccess { response ->
                    if (!response.error) {
                        _addressFromCep.value = mapOf(
                            "street" to response.street,
                            "neighborhood" to response.neighborhood,
                            "city" to response.city,
                            "state" to response.state,
                            "complement" to response.complement
                        )
                    }
                }
            _cepLoading.value = false
        }
    }

    fun clearAddress() { _addressFromCep.value = emptyMap() }

    fun resetState() { _uiState.value = PropertyUiState.Idle }
}
