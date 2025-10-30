package com.example.prova06.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.data.model.Payment
import com.example.prova06.ui.components.*
import com.example.prova06.ui.properties.FilterChips
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.PaymentStatus
import com.example.prova06.utils.toCurrencyBR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,
    onAddPayment: () -> Unit
) {
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val filtered = viewModel.filteredPayments()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is FinanceUiState.Success) viewModel.resetState()
    }

    showDeleteDialog?.let { id ->
        ConfirmDeleteDialog(
            "Excluir pagamento",
            "Excluir este registro de pagamento?",
            onConfirm = { viewModel.deletePayment(id); showDeleteDialog = null },
            onDismiss = { showDeleteDialog = null }
        )
    }

    val totalReceived = payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.receivedAmount }
    val totalPending = payments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financeiro", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPayment, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Adicionar pagamento", tint = SurfaceWhite)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Recebido", totalReceived.toCurrencyBR(), Icons.AutoMirrored.Filled.TrendingUp, SuccessGreen, Modifier.weight(1f))
                StatCard("Pendente", totalPending.toCurrencyBR(), Icons.Default.Schedule, WarningOrange, Modifier.weight(1f))
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                FilterChips(
                    selected = filter,
                    options = listOf("Todos", PaymentStatus.PAID, PaymentStatus.PENDING, PaymentStatus.OVERDUE),
                    onSelect = { viewModel.setFilter(it) }
                )
            }

            Spacer(Modifier.height(8.dp))

            if (filtered.isEmpty()) {
                EmptyState("Nenhum pagamento encontrado", Icons.Default.AttachMoney,
                    if (payments.isEmpty()) "Registrar pagamento" else null,
                    if (payments.isEmpty()) onAddPayment else null)
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filtered, key = { it.id }) { payment ->
                        PaymentCard(payment = payment, onDelete = { showDeleteDialog = payment.id })
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCard(payment: Payment, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    Card(
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(payment.tenantName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    Text(payment.propertyName, fontSize = 13.sp, color = MediumGray)
                    if (payment.referenceMonth.isNotBlank()) Text(payment.referenceMonth, fontSize = 12.sp, color = SecondaryGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(payment.status)
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Valor", fontSize = 11.sp, color = MediumGray)
                    Text(payment.amount.toCurrencyBR(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkGray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Vencimento", fontSize = 11.sp, color = MediumGray)
                    Text(sdf.format(Date(payment.dueDate)), fontSize = 13.sp, color = MediumGray)
                }
                if (payment.paymentDate != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pago em", fontSize = 11.sp, color = SuccessGreen)
                        Text(sdf.format(Date(payment.paymentDate)), fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
