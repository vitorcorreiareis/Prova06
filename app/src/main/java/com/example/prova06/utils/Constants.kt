package com.example.prova06.utils

object Collections {
    const val USERS = "users"
    const val PROPERTIES = "properties"
    const val RENTALS = "rentals"
    const val PAYMENTS = "payments"
    const val DOCUMENTS = "documents"
    const val MAINTENANCES = "maintenances"
}

object PropertyStatus {
    const val AVAILABLE = "Disponível"
    const val RENTED = "Alugado"
    const val MAINTENANCE = "Em manutenção"
}

object RentalStatus {
    const val ACTIVE = "Ativo"
    const val ENDED = "Encerrado"
    const val EXPIRING = "Próximo do vencimento"
}

object PaymentStatus {
    const val PAID = "Pago"
    const val PENDING = "Pendente"
    const val OVERDUE = "Atrasado"
}

object MaintenanceStatus {
    const val OPEN = "Aberta"
    const val IN_PROGRESS = "Em andamento"
    const val DONE = "Concluída"
}

object PropertyType {
    val types = listOf(
        "Apartamento", "Casa", "Kitnet", "Studio",
        "Sala Comercial", "Loja", "Galpão", "Terreno", "Outro"
    )
}

object DocumentType {
    val types = listOf(
        "Contrato de locação", "Laudo de vistoria",
        "Documento do imóvel", "Documento do locatário",
        "Comprovante de pagamento", "Outro"
    )
}
