package com.example.prova06.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.Payment
import com.example.prova06.data.model.Property
import com.example.prova06.data.model.Rental
import com.example.prova06.data.repository.PaymentRepository
import com.example.prova06.data.repository.PropertyRepository
import com.example.prova06.data.repository.RentalRepository
import com.example.prova06.utils.PaymentStatus
import com.example.prova06.utils.PropertyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalProperties: Int = 0,
    val availableProperties: Int = 0,
    val rentedProperties: Int = 0,
    val activeRentals: Int = 0,
    val receivedThisMonth: Double = 0.0,
    val pendingPayments: Int = 0,
    val overduePayments: Int = 0,
    val upcomingDue: List<Payment> = emptyList(),
    val recentProperties: List<Property> = emptyList(),
    val recentRentals: List<Rental> = emptyList()
)

class DashboardViewModel(
    private val propertyRepo: PropertyRepository = PropertyRepository(),
    private val rentalRepo: RentalRepository = RentalRepository(),
    private val paymentRepo: PaymentRepository = PaymentRepository()
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            combine(
                propertyRepo.getPropertiesFlow(),
                rentalRepo.getRentalsFlow(),
                paymentRepo.getPaymentsFlow()
            ) { properties, rentals, payments ->
                val now = System.currentTimeMillis()
                val monthStart = getMonthStart()
                val sevenDaysLater = now + 7 * 24 * 60 * 60 * 1000L

                DashboardStats(
                    totalProperties = properties.size,
                    availableProperties = properties.count { it.status == PropertyStatus.AVAILABLE },
                    rentedProperties = properties.count { it.status == PropertyStatus.RENTED },
                    activeRentals = rentals.count { it.status == "Ativo" },
                    receivedThisMonth = payments.filter {
                        it.status == PaymentStatus.PAID &&
                        (it.paymentDate ?: 0L) >= monthStart
                    }.sumOf { it.receivedAmount },
                    pendingPayments = payments.count { it.status == PaymentStatus.PENDING },
                    overduePayments = payments.count { it.status == PaymentStatus.OVERDUE },
                    upcomingDue = payments.filter {
                        it.status == PaymentStatus.PENDING && it.dueDate in now..sevenDaysLater
                    }.take(5),
                    recentProperties = properties.take(5),
                    recentRentals = rentals.take(5)
                )
            }.collect { stats ->
                _stats.value = stats
                _isLoading.value = false
            }
        }
    }

    private fun getMonthStart(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
