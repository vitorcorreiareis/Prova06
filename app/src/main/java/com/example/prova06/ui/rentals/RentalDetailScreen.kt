package com.example.prova06.ui.rentals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.toCurrencyBR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDetailScreen(
    viewModel: RentalViewModel,
    rentalId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit
) {
    val rental by viewModel.selectedRental.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    LaunchedEffect(rentalId) { viewModel.loadRental(rentalId) }
    LaunchedEffect(uiState) {
        if (uiState is RentalUiState.Success) { viewModel.resetState(); onDeleted() }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            "Encerrar contrato",
            "Tem certeza que deseja excluir este contrato?",
            onConfirm = { showDeleteDialog = false; viewModel.deleteRental(rentalId) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rental?.tenantName ?: "Contrato", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite) }
                },
                actions = {
                    IconButton(onClick = { onEdit(rentalId) }) { Icon(Icons.Default.Edit, "Editar", tint = SurfaceWhite) }
                    IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Excluir", tint = SurfaceWhite) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { padding ->
        if (rental == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            val r = rental!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(SurfaceWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.tenantName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGray)
                                Text(r.propertyName, fontSize = 14.sp, color = MediumGray)
                            }
                            StatusBadge(r.status)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)
                        DetailInfoRow("CPF", r.tenantCpf)
                        DetailInfoRow("Telefone", r.tenantPhone)
                        DetailInfoRow("E-mail", r.tenantEmail)
                    }
                }

                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(SurfaceWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contrato", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        DetailInfoRow("Início", sdf.format(Date(r.startDate)))
                        DetailInfoRow("Término", sdf.format(Date(r.endDate)))
                        DetailInfoRow("Aluguel mensal", r.monthlyRent.toCurrencyBR())
                        DetailInfoRow("Dia de vencimento", "Todo dia ${r.dueDay}")
                        if (r.depositValue > 0) DetailInfoRow("Caução", r.depositValue.toCurrencyBR())
                        if (r.notes.isNotBlank()) DetailInfoRow("Observações", r.notes)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MediumGray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DarkGray)
    }
}
