package com.example.prova06.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.Payment
import com.example.prova06.data.repository.PaymentRepository
import com.example.prova06.utils.PaymentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FinanceUiState {
    object Idle : FinanceUiState()
    object Loading : FinanceUiState()
    object Success : FinanceUiState()
    data class Error(val message: String) : FinanceUiState()
}

class FinanceViewModel(
    private val repository: PaymentRepository = PaymentRepository()
) : ViewModel() {

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _uiState = MutableStateFlow<FinanceUiState>(FinanceUiState.Idle)
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow("Todos")
    val filter: StateFlow<String> = _filter.asStateFlow()

    init { loadPayments() }

    fun loadPayments() {
        viewModelScope.launch {
            repository.getPaymentsFlow().collect { _payments.value = it }
        }
    }

    fun setFilter(f: String) { _filter.value = f }

    fun filteredPayments(): List<Payment> {
        val all = _payments.value
        return when (_filter.value) {
            PaymentStatus.PAID -> all.filter { it.status == PaymentStatus.PAID }
            PaymentStatus.PENDING -> all.filter { it.status == PaymentStatus.PENDING }
            PaymentStatus.OVERDUE -> all.filter { it.status == PaymentStatus.OVERDUE }
            else -> all
        }
    }

    fun savePayment(payment: Payment) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            repository.savePayment(payment)
                .onSuccess { _uiState.value = FinanceUiState.Success }
                .onFailure { _uiState.value = FinanceUiState.Error(it.localizedMessage ?: "Erro ao salvar") }
        }
    }

    fun markAsPaid(id: String, paymentDate: Long, receivedAmount: Double) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            repository.markAsPaid(id, paymentDate, receivedAmount)
                .onSuccess { _uiState.value = FinanceUiState.Success }
                .onFailure { _uiState.value = FinanceUiState.Error(it.localizedMessage ?: "Erro") }
        }
    }

    fun deletePayment(id: String) {
        viewModelScope.launch {
            repository.deletePayment(id)
        }
    }

    fun resetState() { _uiState.value = FinanceUiState.Idle }
}
