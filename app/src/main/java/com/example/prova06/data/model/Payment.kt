package com.example.prova06.data.model

data class Payment(
    val id: String = "",
    val userId: String = "",
    val rentalId: String = "",
    val propertyId: String = "",
    val propertyName: String = "",
    val tenantName: String = "",
    val dueDate: Long = 0L,
    val paymentDate: Long? = null,
    val amount: Double = 0.0,
    val receivedAmount: Double = 0.0,
    val status: String = "Pendente",
    val notes: String = "",
    val referenceMonth: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
