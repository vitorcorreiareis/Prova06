package com.example.prova06.data.model

data class MaintenanceRequest(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val propertyName: String = "",
    val description: String = "",
    val openDate: Long = System.currentTimeMillis(),
    val estimatedCost: Double = 0.0,
    val responsible: String = "",
    val status: String = "Aberta",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
