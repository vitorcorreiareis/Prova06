package com.example.prova06.ui.maintenance

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
import com.example.prova06.data.model.MaintenanceRequest
import com.example.prova06.ui.components.*
import com.example.prova06.ui.properties.PropertyViewModel
import com.example.prova06.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    maintenanceViewModel: MaintenanceViewModel,
    propertyViewModel: PropertyViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by maintenanceViewModel.uiState.collectAsStateWithLifecycle()
    val properties by propertyViewModel.properties.collectAsStateWithLifecycle()

    var propertyId by remember { mutableStateOf("") }
    var propertyName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf("") }
    var responsible by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Aberta") }
    var notes by remember { mutableStateOf("") }
    var showPropertyMenu by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is MaintenanceUiState.Success -> { maintenanceViewModel.resetState(); onSaved() }
            is MaintenanceUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova manutenção", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
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
                    Text("Detalhes da manutenção", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
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

                    ImobiTextField(value = description, onValueChange = { description = it },
                        label = "Descrição do problema *", singleLine = false)
                    ImobiTextField(value = estimatedCost, onValueChange = { estimatedCost = it },
                        label = "Custo estimado (R$)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    ImobiTextField(value = responsible, onValueChange = { responsible = it }, label = "Responsável")

                    ExposedDropdownMenuBox(expanded = showStatusMenu, onExpandedChange = { showStatusMenu = it }) {
                        OutlinedTextField(
                            value = status, onValueChange = {}, readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showStatusMenu) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                            listOf("Aberta", "Em andamento", "Concluída").forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { status = s; showStatusMenu = false })
                            }
                        }
                    }

                    ImobiTextField(value = notes, onValueChange = { notes = it }, label = "Observações", singleLine = false)
                }
            }

            if (errorMsg.isNotBlank()) Text(errorMsg, color = ErrorRed, fontSize = 13.sp)

            ImobiButton(
                text = "Registrar manutenção",
                onClick = {
                    if (description.isBlank()) { errorMsg = "Descreva o problema (*)"; return@ImobiButton }
                    maintenanceViewModel.saveMaintenance(MaintenanceRequest(
                        propertyId = propertyId, propertyName = propertyName,
                        description = description,
                        estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                        responsible = responsible, status = status, notes = notes
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState is MaintenanceUiState.Loading
            )
        }
    }
}
