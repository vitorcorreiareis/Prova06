package com.example.prova06.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object Properties : Screen("properties")
    object PropertyForm : Screen("property_form?propertyId={propertyId}") {
        fun createRoute(propertyId: String = "") = "property_form?propertyId=$propertyId"
    }
    object PropertyDetail : Screen("property_detail/{propertyId}") {
        fun createRoute(propertyId: String) = "property_detail/$propertyId"
    }
    object Rentals : Screen("rentals")
    object RentalForm : Screen("rental_form?rentalId={rentalId}") {
        fun createRoute(rentalId: String = "") = "rental_form?rentalId=$rentalId"
    }
    object RentalDetail : Screen("rental_detail/{rentalId}") {
        fun createRoute(rentalId: String) = "rental_detail/$rentalId"
    }
    object Finance : Screen("finance")
    object PaymentForm : Screen("payment_form?paymentId={paymentId}") {
        fun createRoute(paymentId: String = "") = "payment_form?paymentId=$paymentId"
    }
    object Documents : Screen("documents")
    object Maintenance : Screen("maintenance")
    object MaintenanceForm : Screen("maintenance_form?maintenanceId={maintenanceId}") {
        fun createRoute(maintenanceId: String = "") = "maintenance_form?maintenanceId=$maintenanceId"
    }
    object Profile : Screen("profile")
}
