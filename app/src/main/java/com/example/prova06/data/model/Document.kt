package com.example.prova06.data.model

data class Document(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val rentalId: String = "",
    val name: String = "",
    val type: String = "",
    val url: String = "",
    val uploadedAt: Long = System.currentTimeMillis()
)
