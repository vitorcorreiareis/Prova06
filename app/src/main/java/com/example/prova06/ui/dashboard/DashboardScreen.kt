package com.example.prova06.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.toCurrencyBR

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToProperties: () -> Unit,
    onNavigateToRentals: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToMaintenance: () -> Unit
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundGray),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(NavyBlue, PrimaryBlue)))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text("ImobiControl", color = SurfaceWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Visão geral do seu portfólio", color = SurfaceWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        } else {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-8).dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            StatCard(
                                title = "Total de imóveis",
                                value = "${stats.totalProperties}",
                                icon = Icons.Default.Home,
                                color = PrimaryBlue,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                        item {
                            StatCard(
                                title = "Disponíveis",
                                value = "${stats.availableProperties}",
                                icon = Icons.Default.CheckCircle,
                                color = SuccessGreen,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                        item {
                            StatCard(
                                title = "Alugados",
                                value = "${stats.rentedProperties}",
                                icon = Icons.Default.Key,
                                color = WarningOrange,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                        item {
                            StatCard(
                                title = "Contratos ativos",
                                value = "${stats.activeRentals}",
                                icon = Icons.Default.Description,
                                color = NavyBlue,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FinanceSummaryCard(
                            label = "Recebido no mês",
                            value = stats.receivedThisMonth.toCurrencyBR(),
                            color = SuccessGreen,
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        FinanceSummaryCard(
                            label = "Pagamentos pendentes",
                            value = "${stats.pendingPayments}",
                            color = WarningOrange,
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (stats.overduePayments > 0) {
                        Spacer(Modifier.height(12.dp))
                        FinanceSummaryCard(
                            label = "Em atraso",
                            value = "${stats.overduePayments}",
                            color = ErrorRed,
                            icon = Icons.Default.Warning,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    SectionHeader("Acesso rápido")
                    QuickAccessRow(
                        onNavigateToProperties, onNavigateToRentals,
                        onNavigateToFinance, onNavigateToDocuments, onNavigateToMaintenance
                    )
                }
            }
        }
    }
}

@Composable
private fun FinanceSummaryCard(
    label: String, value: String, color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGray)
                Text(label, fontSize = 12.sp, color = MediumGray)
            }
        }
    }
}

@Composable
private fun QuickAccessRow(
    onProperties: () -> Unit, onRentals: () -> Unit,
    onFinance: () -> Unit, onDocuments: () -> Unit, onMaintenance: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(listOf(
            Triple("Imóveis", Icons.Default.Home, onProperties),
            Triple("Locações", Icons.Default.Description, onRentals),
            Triple("Financeiro", Icons.Default.AttachMoney, onFinance),
            Triple("Documentos", Icons.Default.Folder, onDocuments),
            Triple("Manutenções", Icons.Default.Build, onMaintenance)
        )) { (label, icon, action) ->
            QuickAccessCard(label, icon, action)
        }
    }
}

@Composable
private fun QuickAccessCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(90.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 11.sp, color = DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}
