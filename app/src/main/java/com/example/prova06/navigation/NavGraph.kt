package com.example.prova06.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.prova06.ui.auth.*
import com.example.prova06.ui.dashboard.*
import com.example.prova06.ui.documents.*
import com.example.prova06.ui.finance.*
import com.example.prova06.ui.maintenance.*
import com.example.prova06.ui.profile.*
import com.example.prova06.ui.properties.*
import com.example.prova06.ui.rentals.*
import com.example.prova06.ui.theme.NavyBlue
import com.example.prova06.ui.theme.PrimaryBlue
import com.example.prova06.ui.theme.SurfaceWhite

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Início", Icons.Default.Home),
    BottomNavItem(Screen.Properties, "Imóveis", Icons.Default.Apartment),
    BottomNavItem(Screen.Rentals, "Locações", Icons.Default.Description),
    BottomNavItem(Screen.Finance, "Financeiro", Icons.Default.AttachMoney),
    BottomNavItem(Screen.Profile, "Perfil", Icons.Default.Person)
)

val bottomNavRoutes = bottomNavItems.map { it.screen.route }

@Composable
fun ImobiNavGraph(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = bottomNavRoutes.any { currentRoute?.startsWith(it) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = SurfaceWhite) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val propertyViewModel: PropertyViewModel = viewModel()
        val rentalViewModel: RentalViewModel = viewModel()
        val financeViewModel: FinanceViewModel = viewModel()
        val documentViewModel: DocumentViewModel = viewModel()
        val maintenanceViewModel: MaintenanceViewModel = viewModel()
        val profileViewModel: ProfileViewModel = viewModel()
        val dashboardViewModel: DashboardViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    isLoggedIn = authViewModel.isLoggedIn,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToProperties = { navController.navigate(Screen.Properties.route) },
                    onNavigateToRentals = { navController.navigate(Screen.Rentals.route) },
                    onNavigateToFinance = { navController.navigate(Screen.Finance.route) },
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                    onNavigateToMaintenance = { navController.navigate(Screen.Maintenance.route) }
                )
            }

            composable(Screen.Properties.route) {
                PropertyListScreen(
                    viewModel = propertyViewModel,
                    onAddProperty = { navController.navigate(Screen.PropertyForm.createRoute()) },
                    onPropertyDetail = { navController.navigate(Screen.PropertyDetail.createRoute(it)) }
                )
            }

            composable(
                Screen.PropertyForm.route,
                arguments = listOf(navArgument("propertyId") { defaultValue = ""; nullable = false })
            ) { backStack ->
                val propertyId = backStack.arguments?.getString("propertyId")
                PropertyFormScreen(
                    viewModel = propertyViewModel,
                    propertyId = propertyId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                Screen.PropertyDetail.route,
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStack ->
                val propertyId = backStack.arguments?.getString("propertyId") ?: ""
                PropertyDetailScreen(
                    viewModel = propertyViewModel,
                    propertyId = propertyId,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.PropertyForm.createRoute(it)) },
                    onDeleted = { navController.popBackStack() }
                )
            }

            composable(Screen.Rentals.route) {
                RentalListScreen(
                    viewModel = rentalViewModel,
                    onAddRental = { navController.navigate(Screen.RentalForm.createRoute()) },
                    onRentalDetail = { navController.navigate(Screen.RentalDetail.createRoute(it)) }
                )
            }

            composable(
                Screen.RentalForm.route,
                arguments = listOf(navArgument("rentalId") { defaultValue = ""; nullable = false })
            ) { backStack ->
                val rentalId = backStack.arguments?.getString("rentalId")
                RentalFormScreen(
                    rentalViewModel = rentalViewModel,
                    propertyViewModel = propertyViewModel,
                    rentalId = rentalId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                Screen.RentalDetail.route,
                arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
            ) { backStack ->
                val rentalId = backStack.arguments?.getString("rentalId") ?: ""
                RentalDetailScreen(
                    viewModel = rentalViewModel,
                    rentalId = rentalId,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.RentalForm.createRoute(it)) },
                    onDeleted = { navController.popBackStack() }
                )
            }

            composable(Screen.Finance.route) {
                FinanceScreen(
                    viewModel = financeViewModel,
                    onAddPayment = { navController.navigate(Screen.PaymentForm.createRoute()) }
                )
            }

            composable(
                Screen.PaymentForm.route,
                arguments = listOf(navArgument("paymentId") { defaultValue = ""; nullable = false })
            ) {
                PaymentFormScreen(
                    financeViewModel = financeViewModel,
                    rentalViewModel = rentalViewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Documents.route) {
                DocumentScreen(
                    viewModel = documentViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Maintenance.route) {
                MaintenanceScreen(
                    viewModel = maintenanceViewModel,
                    onAddMaintenance = { navController.navigate(Screen.MaintenanceForm.createRoute()) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.MaintenanceForm.route,
                arguments = listOf(navArgument("maintenanceId") { defaultValue = ""; nullable = false })
            ) {
                MaintenanceFormScreen(
                    maintenanceViewModel = maintenanceViewModel,
                    propertyViewModel = propertyViewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
