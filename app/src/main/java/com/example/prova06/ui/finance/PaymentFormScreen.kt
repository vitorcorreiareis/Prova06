package com.example.prova06.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.data.model.Payment
import com.example.prova06.ui.components.*
import com.example.prova06.ui.rentals.RentalViewModel
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    financeViewModel: FinanceViewModel,
    rentalViewModel: RentalViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by financeViewModel.uiState.collectAsStateWithLifecycle()
    val rentals by rentalViewModel.rentals.collectAsStateWithLifecycle()

    var rentalId by remember { mutableStateOf("") }
    var propertyId by remember { mutableStateOf("") }
    var propertyName by remember { mutableStateOf("") }
    var tenantName by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var receivedAmount by remember { mutableStateOf("") }
    var referenceMonth by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PaymentStatus.PENDING) }
    var notes by remember { mutableStateOf("") }
    var showRentalMenu by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is FinanceUiState.Success -> { financeViewModel.resetState(); onSaved() }
            is FinanceUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar pagamento", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { padding ->
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Locação", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    HorizontalDivider(color = DividerColor)

                    ExposedDropdownMenuBox(expanded = showRentalMenu, onExpandedChange = { showRentalMenu = it }) {
                        OutlinedTextField(
                            value = if (tenantName.isNotBlank()) "$tenantName - $propertyName" else "",
                            onValueChange = {}, readOnly = true,
                            label = { Text("Locação *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showRentalMenu) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showRentalMenu, onDismissRequest = { showRentalMenu = false }) {
                            rentals.filter { it.status == "Ativo" }.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text("${r.tenantName} - ${r.propertyName}") },
                                    onClick = {
                                        rentalId = r.id; propertyId = r.propertyId
                                        propertyName = r.propertyName; tenantName = r.tenantName
                                        amount = r.monthlyRent.toString()
                                        showRentalMenu = false
                                    }
                                )
                            }
                        }
                    }

                    ImobiTextField(value = referenceMonth, onValueChange = { referenceMonth = it }, label = "Mês de referência (ex: Janeiro/2025)")
                }
            }

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(SurfaceWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pagamento", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    HorizontalDivider(color = DividerColor)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImobiTextField(value = amount, onValueChange = { amount = it }, label = "Valor (R$) *",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                        ImobiTextField(value = receivedAmount, onValueChange = { receivedAmount = it }, label = "Recebido (R$)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImobiTextField(value = dueDate, onValueChange = { dueDate = it }, label = "Vencimento *",
                            modifier = Modifier.weight(1f))
                        ImobiTextField(value = paymentDate, onValueChange = { paymentDate = it }, label = "Pagamento",
                            modifier = Modifier.weight(1f))
                    }

                    ExposedDropdownMenuBox(expanded = showStatusMenu, onExpandedChange = { showStatusMenu = it }) {
                        OutlinedTextField(
                            value = status, onValueChange = {}, readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showStatusMenu) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                            listOf(PaymentStatus.PENDING, PaymentStatus.PAID, PaymentStatus.OVERDUE).forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { status = s; showStatusMenu = false })
                            }
                        }
                    }
                    ImobiTextField(value = notes, onValueChange = { notes = it }, label = "Observações", singleLine = false)
                }
            }

            if (errorMsg.isNotBlank()) Text(errorMsg, color = ErrorRed, fontSize = 13.sp)

            ImobiButton(
                text = "Registrar pagamento",
                onClick = {
                    if (amount.isBlank() || dueDate.isBlank()) {
                        errorMsg = "Preencha os campos obrigatórios (*)"; return@ImobiButton
                    }
                    val dueDateMs = runCatching { sdf.parse(dueDate)?.time ?: 0L }.getOrDefault(0L)
                    val payDateMs = if (paymentDate.isNotBlank()) runCatching { sdf.parse(paymentDate)?.time }.getOrNull() else null
                    financeViewModel.savePayment(Payment(
                        rentalId = rentalId, propertyId = propertyId,
                        propertyName = propertyName, tenantName = tenantName,
                        dueDate = dueDateMs, paymentDate = payDateMs,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        receivedAmount = receivedAmount.toDoubleOrNull() ?: 0.0,
                        status = status, notes = notes, referenceMonth = referenceMonth
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState is FinanceUiState.Loading
            )
        }
    }
}
