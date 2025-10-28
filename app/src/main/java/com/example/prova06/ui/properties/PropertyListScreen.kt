package com.example.prova06.ui.properties

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
import com.example.prova06.data.model.Property
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*
import com.example.prova06.utils.toCurrencyBR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListScreen(
    viewModel: PropertyViewModel,
    onAddProperty: () -> Unit,
    onPropertyDetail: (String) -> Unit
) {
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("Todos") }

    val filtered = properties.filter { p ->
        (searchQuery.isBlank() || p.name.contains(searchQuery, true) || p.city.contains(searchQuery, true)) &&
        (filterStatus == "Todos" || p.status == filterStatus)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Imóveis", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProperty, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Adicionar imóvel", tint = SurfaceWhite)
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar imóvel...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                FilterChips(
                    selected = filterStatus,
                    options = listOf("Todos", "Disponível", "Alugado", "Em manutenção"),
                    onSelect = { filterStatus = it }
                )
            }

            if (filtered.isEmpty()) {
                EmptyState(
                    message = "Nenhum imóvel encontrado",
                    icon = Icons.Default.Home,
                    actionLabel = if (properties.isEmpty()) "Adicionar imóvel" else null,
                    onAction = if (properties.isEmpty()) onAddProperty else null
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { property ->
                        PropertyCard(property = property, onClick = { onPropertyDetail(property.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChips(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit) {
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
                            .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Home, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(property.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = DarkGray)
                        Text(property.type, fontSize = 13.sp, color = MediumGray)
                    }
                }
                StatusBadge(property.status)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = SecondaryGray, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${property.city} - ${property.state}", fontSize = 13.sp, color = MediumGray)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Aluguel", fontSize = 11.sp, color = MediumGray)
                    Text(property.rentValue.toCurrencyBR(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryBlue)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (property.bedrooms > 0) InfoChip("${property.bedrooms} qtos", Icons.Default.Bed)
                    if (property.bathrooms > 0) InfoChip("${property.bathrooms} ban", Icons.Default.Shower)
                    if (property.parkingSpots > 0) InfoChip("${property.parkingSpots} vg", Icons.Default.DirectionsCar)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SecondaryGray, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(2.dp))
        Text(text, fontSize = 12.sp, color = MediumGray)
    }
}
