package com.example.prova06.ui.properties

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
import com.example.prova06.data.model.Property
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.PropertyStatus
import com.example.prova06.utils.PropertyType
import com.example.prova06.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormScreen(
    viewModel: PropertyViewModel,
    propertyId: String? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedProperty by viewModel.selectedProperty.collectAsStateWithLifecycle()
    val cepLoading by viewModel.cepLoading.collectAsStateWithLifecycle()
    val addressFromCep by viewModel.addressFromCep.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("Residencial") }
    var cep by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var rentValue by remember { mutableStateOf("") }
    var iptuValue by remember { mutableStateOf("") }
    var condoValue by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var parkingSpots by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PropertyStatus.AVAILABLE) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(propertyId) {
        if (!propertyId.isNullOrBlank()) viewModel.loadProperty(propertyId)
    }

    LaunchedEffect(selectedProperty) {
        selectedProperty?.let { p ->
            name = p.name; type = p.type; purpose = p.purpose
            cep = p.cep; street = p.street; number = p.number
            complement = p.complement; neighborhood = p.neighborhood
            city = p.city; state = p.state
            rentValue = if (p.rentValue > 0) p.rentValue.toString() else ""
            iptuValue = if (p.iptuValue > 0) p.iptuValue.toString() else ""
            condoValue = if (p.condoValue > 0) p.condoValue.toString() else ""
            description = p.description
            bedrooms = if (p.bedrooms > 0) p.bedrooms.toString() else ""
            bathrooms = if (p.bathrooms > 0) p.bathrooms.toString() else ""
            parkingSpots = if (p.parkingSpots > 0) p.parkingSpots.toString() else ""
            status = p.status
        }
    }

    LaunchedEffect(addressFromCep) {
        if (addressFromCep.isNotEmpty()) {
            street = addressFromCep["street"] ?: street
            neighborhood = addressFromCep["neighborhood"] ?: neighborhood
            city = addressFromCep["city"] ?: city
            state = addressFromCep["state"] ?: state
            if (complement.isBlank()) complement = addressFromCep["complement"] ?: ""
            viewModel.clearAddress()
        }
    }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is PropertyUiState.Success -> { viewModel.resetState(); onSaved() }
            is PropertyUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (propertyId.isNullOrBlank()) "Novo imóvel" else "Editar imóvel",
                        color = SurfaceWhite, fontWeight = FontWeight.Bold
                    )
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
            FormSection("Informações básicas") {
                ImobiTextField(value = name, onValueChange = { name = it }, label = "Nome / Identificação *")

                ExposedDropdownMenuBox(expanded = showTypeMenu, onExpandedChange = { showTypeMenu = it }) {
                    OutlinedTextField(
                        value = type, onValueChange = {}, readOnly = true,
                        label = { Text("Tipo de imóvel *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTypeMenu) },
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                        PropertyType.types.forEach { t ->
                            DropdownMenuItem(text = { Text(t) }, onClick = { type = t; showTypeMenu = false })
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Residencial", "Comercial").forEach { p ->
                        FilterChip(
                            selected = purpose == p, onClick = { purpose = p },
                            label = { Text(p, fontSize = 13.sp) }
                        )
                    }
                }
            }

            FormSection("Endereço") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImobiTextField(
                        value = cep,
                        onValueChange = {
                            if (it.length <= 9) {
                                cep = it
                                if (it.replace("[^0-9]".toRegex(), "").length == 8)
                                    viewModel.fetchAddressByCep(it)
                            }
                        },
                        label = "CEP *",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    if (cepLoading) {
                        Box(modifier = Modifier.size(56.dp).padding(top = 8.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryBlue)
                        }
                    }
                }
                ImobiTextField(value = street, onValueChange = { street = it }, label = "Rua / Logradouro *")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImobiTextField(value = number, onValueChange = { number = it }, label = "Número *",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.4f))
                    ImobiTextField(value = complement, onValueChange = { complement = it }, label = "Complemento",
                        modifier = Modifier.weight(0.6f))
                }
                ImobiTextField(value = neighborhood, onValueChange = { neighborhood = it }, label = "Bairro *")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImobiTextField(value = city, onValueChange = { city = it }, label = "Cidade *", modifier = Modifier.weight(1f))
                    ImobiTextField(value = state, onValueChange = { state = it }, label = "UF *", modifier = Modifier.weight(0.3f))
                }
            }

            FormSection("Valores") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImobiTextField(value = rentValue, onValueChange = { rentValue = it }, label = "Aluguel (R$)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    ImobiTextField(value = iptuValue, onValueChange = { iptuValue = it }, label = "IPTU (R$)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }
                ImobiTextField(value = condoValue, onValueChange = { condoValue = it }, label = "Condomínio (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }

            FormSection("Características") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImobiTextField(value = bedrooms, onValueChange = { bedrooms = it }, label = "Quartos",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    ImobiTextField(value = bathrooms, onValueChange = { bathrooms = it }, label = "Banheiros",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    ImobiTextField(value = parkingSpots, onValueChange = { parkingSpots = it }, label = "Vagas",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                ImobiTextField(value = description, onValueChange = { description = it }, label = "Descrição", singleLine = false)

                ExposedDropdownMenuBox(expanded = showStatusMenu, onExpandedChange = { showStatusMenu = it }) {
                    OutlinedTextField(
                        value = status, onValueChange = {}, readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showStatusMenu) },
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        listOf(PropertyStatus.AVAILABLE, PropertyStatus.RENTED, PropertyStatus.MAINTENANCE).forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { status = s; showStatusMenu = false })
                        }
                    }
                }
            }

            if (errorMsg.isNotBlank()) {
                Text(errorMsg, color = ErrorRed, fontSize = 13.sp)
            }

            ImobiButton(
                text = if (propertyId.isNullOrBlank()) "Cadastrar imóvel" else "Salvar alterações",
                onClick = {
                    if (name.isBlank() || type.isBlank() || street.isBlank()) {
                        errorMsg = "Preencha os campos obrigatórios (*)"; return@ImobiButton
                    }
                    val property = Property(
                        id = selectedProperty?.id ?: "",
                        name = name, type = type, purpose = purpose,
                        cep = cep, street = street, number = number, complement = complement,
                        neighborhood = neighborhood, city = city, state = state,
                        rentValue = rentValue.toDoubleOrNull() ?: 0.0,
                        iptuValue = iptuValue.toDoubleOrNull() ?: 0.0,
                        condoValue = condoValue.toDoubleOrNull() ?: 0.0,
                        description = description,
                        bedrooms = bedrooms.toIntOrNull() ?: 0,
                        bathrooms = bathrooms.toIntOrNull() ?: 0,
                        parkingSpots = parkingSpots.toIntOrNull() ?: 0,
                        status = status
                    )
                    viewModel.saveProperty(property)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState is PropertyUiState.Loading
            )
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
            HorizontalDivider(color = DividerColor)
            content()
        }
    }
}
