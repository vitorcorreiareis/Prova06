package com.example.prova06.data.repository

import com.example.prova06.data.model.Payment
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession
import com.example.prova06.utils.PaymentStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class PaymentRepository {

    private val prefs = AppContext.prefs("imobicontrol_payments")
    private val gson = Gson()
    private val type = object : TypeToken<List<Payment>>() {}.type
    private val _refresh = MutableStateFlow(0)

    private fun load(uid: String): List<Payment> {
        if (uid.isBlank()) return emptyList()
        val json = prefs.getString("data_$uid", "[]") ?: "[]"
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private fun persist(uid: String, items: List<Payment>) {
        prefs.edit().putString("data_$uid", gson.toJson(items)).apply()
        _refresh.value++
    }

    fun getPaymentsFlow(): Flow<List<Payment>> =
        LocalSession.userIdFlow.combine(_refresh) { uid, _ -> load(uid) }

    suspend fun savePayment(payment: Payment): Result<String> = runCatching {
        val uid = LocalSession.userId
        val list = load(uid).toMutableList()
        if (payment.id.isBlank()) {
            val id = UUID.randomUUID().toString()
            list.add(0, payment.copy(id = id, userId = uid, createdAt = System.currentTimeMillis()))
            persist(uid, list)
            id
        } else {
            val idx = list.indexOfFirst { it.id == payment.id }
            val item = payment.copy(userId = uid)
            if (idx >= 0) list[idx] = item else list.add(0, item)
            persist(uid, list)
            payment.id
        }
    }

    suspend fun markAsPaid(id: String, paymentDate: Long, receivedAmount: Double): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).map {
            if (it.id == id) it.copy(
                status = PaymentStatus.PAID,
                paymentDate = paymentDate,
                receivedAmount = receivedAmount
            ) else it
        })
    }

    suspend fun deletePayment(id: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).filter { it.id != id })
    }
}
