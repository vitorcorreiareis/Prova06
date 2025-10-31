package com.example.prova06.ui.maintenance

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
import com.example.prova06.data.model.MaintenanceRequest
import com.example.prova06.ui.components.*
import com.example.prova06.ui.properties.FilterChips
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.toCurrencyBR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel,
    onAddMaintenance: () -> Unit,
    onBack: () -> Unit
) {
    val maintenances by viewModel.maintenances.collectAsStateWithLifecycle()
    var filterStatus by remember { mutableStateOf("Todos") }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    val filtered = maintenances.filter { filterStatus == "Todos" || it.status == filterStatus }

    showDeleteDialog?.let { id ->
        ConfirmDeleteDialog(
            "Excluir manutenção",
            "Excluir este registro de manutenção?",
            onConfirm = { viewModel.deleteMaintenance(id); showDeleteDialog = null },
            onDismiss = { showDeleteDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manutenções", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMaintenance, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Nova manutenção", tint = SurfaceWhite)
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
                    options = listOf("Todos", "Aberta", "Em andamento", "Concluída"),
                    onSelect = { filterStatus = it }
                )
            }

            if (filtered.isEmpty()) {
                EmptyState("Nenhuma manutenção encontrada", Icons.Default.Build,
                    if (maintenances.isEmpty()) "Nova manutenção" else null,
                    if (maintenances.isEmpty()) onAddMaintenance else null)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { maintenance ->
                        MaintenanceCard(
                            maintenance = maintenance,
                            onUpdateStatus = { status -> viewModel.updateStatus(maintenance.id, status) },
                            onDelete = { showDeleteDialog = maintenance.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    maintenance: MaintenanceRequest,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    var showStatusMenu by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(WarningOrange.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Build, null, tint = WarningOrange, modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(maintenance.propertyName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                        Text(maintenance.description, fontSize = 13.sp, color = MediumGray, maxLines = 2)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusBadge(maintenance.status)
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Abertura: ${sdf.format(Date(maintenance.openDate))}", fontSize = 12.sp, color = MediumGray)
                    if (maintenance.estimatedCost > 0)
                        Text("Custo estimado: ${maintenance.estimatedCost.toCurrencyBR()}", fontSize = 12.sp, color = MediumGray)
                    if (maintenance.responsible.isNotBlank())
                        Text("Responsável: ${maintenance.responsible}", fontSize = 12.sp, color = MediumGray)
                }
                Box {
                    OutlinedButton(onClick = { showStatusMenu = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Alterar status", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        listOf("Aberta", "Em andamento", "Concluída").forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { onUpdateStatus(s); showStatusMenu = false })
                        }
                    }
                }
            }
        }
    }
}
