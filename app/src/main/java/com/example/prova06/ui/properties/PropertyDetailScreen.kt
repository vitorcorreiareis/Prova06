package com.example.prova06.ui.properties

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    viewModel: PropertyViewModel,
    propertyId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit
) {
    val property by viewModel.selectedProperty.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(propertyId) { viewModel.loadProperty(propertyId) }

    LaunchedEffect(uiState) {
        if (uiState is PropertyUiState.Success) { viewModel.resetState(); onDeleted() }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = "Excluir imóvel",
            message = "Tem certeza que deseja excluir o imóvel \"${property?.name}\"? Esta ação não pode ser desfeita.",
            onConfirm = { showDeleteDialog = false; viewModel.deleteProperty(propertyId) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(property?.name ?: "Imóvel", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite) }
                },
                actions = {
                    IconButton(onClick = { onEdit(propertyId) }) {
                        Icon(Icons.Default.Edit, "Editar", tint = SurfaceWhite)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Excluir", tint = SurfaceWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { padding ->
        if (property == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            val p = property!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGray)
                                Text(p.type, fontSize = 14.sp, color = MediumGray)
                                Text(p.purpose, fontSize = 13.sp, color = SecondaryGray)
                            }
                            StatusBadge(p.status)
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(Modifier.height(12.dp))
                        DetailRow(Icons.Default.LocationOn, p.fullAddress)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (p.bedrooms > 0) DetailChip("${p.bedrooms} quartos", Icons.Default.Bed)
                            if (p.bathrooms > 0) DetailChip("${p.bathrooms} banheiros", Icons.Default.Shower)
                            if (p.parkingSpots > 0) DetailChip("${p.parkingSpots} vagas", Icons.Default.DirectionsCar)
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Valores", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        ValueRow("Aluguel mensal", p.rentValue.toCurrencyBR(), PrimaryBlue)
                        if (p.iptuValue > 0) ValueRow("IPTU", p.iptuValue.toCurrencyBR(), MediumGray)
                        if (p.condoValue > 0) ValueRow("Condomínio", p.condoValue.toCurrencyBR(), MediumGray)
                    }
                }

                if (p.description.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Descrição", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                            Spacer(Modifier.height(8.dp))
                            Text(p.description, fontSize = 14.sp, color = MediumGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = SecondaryGray, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = MediumGray)
    }
}

@Composable
private fun DetailChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 13.sp, color = DarkGray)
    }
}

@Composable
private fun ValueRow(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MediumGray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}
