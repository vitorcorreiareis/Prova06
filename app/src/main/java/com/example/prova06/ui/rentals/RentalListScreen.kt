package com.example.prova06.ui.rentals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.data.model.Rental
import com.example.prova06.ui.components.*
import com.example.prova06.ui.properties.FilterChips
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.toCurrencyBR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalListScreen(
    viewModel: RentalViewModel,
    onAddRental: () -> Unit,
    onRentalDetail: (String) -> Unit
) {
    val rentals by viewModel.rentals.collectAsStateWithLifecycle()
    var filterStatus by remember { mutableStateOf("Todos") }

    val filtered = rentals.filter { filterStatus == "Todos" || it.status == filterStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locações", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRental, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Nova locação", tint = SurfaceWhite)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilterChips(
                    selected = filterStatus,
                    options = listOf("Todos", "Ativo", "Encerrado", "Próximo do vencimento"),
                    onSelect = { filterStatus = it }
                )
            }
            if (filtered.isEmpty()) {
                EmptyState(
                    "Nenhuma locação encontrada",
                    Icons.Default.Description,
                    if (rentals.isEmpty()) "Nova locação" else null,
                    if (rentals.isEmpty()) onAddRental else null
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { rental ->
                        RentalCard(rental = rental, onClick = { onRentalDetail(rental.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun RentalCard(rental: Rental, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(NavyBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Description, null, tint = NavyBlue, modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(rental.tenantName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = DarkGray)
                        Text(rental.propertyName, fontSize = 13.sp, color = MediumGray)
                    }
                }
                StatusBadge(rental.status)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Aluguel", fontSize = 11.sp, color = MediumGray)
                    Text(rental.monthlyRent.toCurrencyBR(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Vence dia ${rental.dueDay}", fontSize = 12.sp, color = MediumGray)
                    Text("${sdf.format(Date(rental.startDate))} – ${sdf.format(Date(rental.endDate))}", fontSize = 12.sp, color = SecondaryGray)
                }
            }
        }
    }
}
