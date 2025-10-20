package com.example.prova06.data.model

data class Rental(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val propertyName: String = "",
    val tenantName: String = "",
    val tenantCpf: String = "",
    val tenantPhone: String = "",
    val tenantEmail: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val monthlyRent: Double = 0.0,
    val dueDay: Int = 1,
    val depositValue: Double = 0.0,
    val notes: String = "",
    val status: String = "Ativo",
    val createdAt: Long = System.currentTimeMillis()
)
