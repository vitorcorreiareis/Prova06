package com.example.prova06.data.model

data class Property(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "",
    val purpose: String = "Residencial",
    val cep: String = "",
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val rentValue: Double = 0.0,
    val iptuValue: Double = 0.0,
    val condoValue: Double = 0.0,
    val description: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val parkingSpots: Int = 0,
    val status: String = "Disponível",
    val photos: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullAddress: String get() = "$street, $number${if (complement.isNotBlank()) " - $complement" else ""}, $neighborhood, $city - $state"
}
