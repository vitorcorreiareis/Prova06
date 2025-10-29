package com.example.prova06.ui.rentals

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
import com.example.prova06.data.model.Rental
import com.example.prova06.ui.components.*
import com.example.prova06.ui.properties.PropertyViewModel
import com.example.prova06.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalFormScreen(
    rentalViewModel: RentalViewModel,
    propertyViewModel: PropertyViewModel,
    rentalId: String? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by rentalViewModel.uiState.collectAsStateWithLifecycle()
    val selectedRental by rentalViewModel.selectedRental.collectAsStateWithLifecycle()
    val properties by propertyViewModel.properties.collectAsStateWithLifecycle()

    var propertyId by remember { mutableStateOf("") }
    var propertyName by remember { mutableStateOf("") }
    var tenantName by remember { mutableStateOf("") }
    var tenantCpf by remember { mutableStateOf("") }
    var tenantPhone by remember { mutableStateOf("") }
    var tenantEmail by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf("1") }
    var depositValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ativo") }
    var showPropertyMenu by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    LaunchedEffect(rentalId) {
        if (!rentalId.isNullOrBlank()) rentalViewModel.loadRental(rentalId)
    }

    LaunchedEffect(selectedRental) {
        selectedRental?.let { r ->
            propertyId = r.propertyId; propertyName = r.propertyName
            tenantName = r.tenantName; tenantCpf = r.tenantCpf
            tenantPhone = r.tenantPhone; tenantEmail = r.tenantEmail
            startDate = sdf.format(java.util.Date(r.startDate))
            endDate = sdf.format(java.util.Date(r.endDate))
            monthlyRent = if (r.monthlyRent > 0) r.monthlyRent.toString() else ""
            dueDay = r.dueDay.toString()
            depositValue = if (r.depositValue > 0) r.depositValue.toString() else ""
            notes = r.notes; status = r.status
        }
    }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is RentalUiState.Success -> { rentalViewModel.resetState(); onSaved() }
            is RentalUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    fun parseDate(str: String): Long {
        return runCatching { sdf.parse(str)?.time ?: 0L }.getOrDefault(0L)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (rentalId.isNullOrBlank()) "Nova locação" else "Editar locação",
                        color = SurfaceWhite, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite)
                    }
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
                    Text("Imóvel e Locatário", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    HorizontalDivider(color = DividerColor)

                    ExposedDropdownMenuBox(expanded = showPropertyMenu, onExpandedChange = { showPropertyMenu = it }) {
                        OutlinedTextField(
                            value = propertyName, onValueChange = {}, readOnly = true,
                            label = { Text("Imóvel *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showPropertyMenu) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showPropertyMenu, onDismissRequest = { showPropertyMenu = false }) {
                            properties.forEach { p ->
                                DropdownMenuItem(text = { Text(p.name) }, onClick = {
                                    propertyId = p.id; propertyName = p.name; showPropertyMenu = false
                                })
                            }
                        }
                    }

                    ImobiTextField(value = tenantName, onValueChange = { tenantName = it }, label = "Nome do locatário *")
                    ImobiTextField(value = tenantCpf, onValueChange = { tenantCpf = it }, label = "CPF",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    ImobiTextField(value = tenantPhone, onValueChange = { tenantPhone = it }, label = "Telefone",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    ImobiTextField(value = tenantEmail, onValueChange = { tenantEmail = it }, label = "E-mail",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }
            }

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(SurfaceWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Contrato", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    HorizontalDivider(color = DividerColor)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImobiTextField(value = startDate, onValueChange = { startDate = it }, label = "Início (dd/MM/aaaa) *",
                            modifier = Modifier.weight(1f))
                        ImobiTextField(value = endDate, onValueChange = { endDate = it }, label = "Término (dd/MM/aaaa) *",
                            modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImobiTextField(value = monthlyRent, onValueChange = { monthlyRent = it }, label = "Aluguel (R$) *",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                        ImobiTextField(value = dueDay, onValueChange = { dueDay = it }, label = "Dia venc.",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(0.5f))
                    }
                    ImobiTextField(value = depositValue, onValueChange = { depositValue = it }, label = "Caução (R$)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

                    ExposedDropdownMenuBox(expanded = showStatusMenu, onExpandedChange = { showStatusMenu = it }) {
                        OutlinedTextField(
                            value = status, onValueChange = {}, readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showStatusMenu) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                            listOf("Ativo", "Encerrado", "Próximo do vencimento").forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { status = s; showStatusMenu = false })
                            }
                        }
                    }

                    ImobiTextField(value = notes, onValueChange = { notes = it }, label = "Observações", singleLine = false)
                }
            }

            if (errorMsg.isNotBlank()) Text(errorMsg, color = ErrorRed, fontSize = 13.sp)

            ImobiButton(
                text = if (rentalId.isNullOrBlank()) "Criar contrato" else "Salvar alterações",
                onClick = {
                    if (tenantName.isBlank() || monthlyRent.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                        errorMsg = "Preencha os campos obrigatórios (*)"; return@ImobiButton
                    }
                    val rental = Rental(
                        id = selectedRental?.id ?: "",
                        propertyId = propertyId, propertyName = propertyName,
                        tenantName = tenantName, tenantCpf = tenantCpf,
                        tenantPhone = tenantPhone, tenantEmail = tenantEmail,
                        startDate = parseDate(startDate), endDate = parseDate(endDate),
                        monthlyRent = monthlyRent.toDoubleOrNull() ?: 0.0,
                        dueDay = dueDay.toIntOrNull() ?: 1,
                        depositValue = depositValue.toDoubleOrNull() ?: 0.0,
                        notes = notes, status = status
                    )
                    rentalViewModel.saveRental(rental)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState is RentalUiState.Loading
            )
        }
    }
}
