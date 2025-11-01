package com.example.prova06.ui.documents

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
import com.example.prova06.data.model.Document
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    viewModel: DocumentViewModel,
    onBack: () -> Unit
) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is DocumentUiState.Success) viewModel.resetState()
    }

    showDeleteDialog?.let { id ->
        ConfirmDeleteDialog(
            "Excluir documento",
            "Excluir este documento?",
            onConfirm = { viewModel.deleteDocument(id); showDeleteDialog = null },
            onDismiss = { showDeleteDialog = null }
        )
    }

    if (showAddDialog) {
        AddDocumentDialog(
            onDismiss = { showAddDialog = false },
            onSave = { doc -> viewModel.saveDocument(doc); showAddDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documentos", color = SurfaceWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Adicionar documento", tint = SurfaceWhite)
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("Nenhum documento encontrado", Icons.Default.Folder,
                    "Adicionar documento") { showAddDialog = true }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(BackgroundGray).padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(documents, key = { it.id }) { doc ->
                    DocumentCard(doc = doc, onDelete = { showDeleteDialog = doc.id })
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(doc: Document, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Description, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp)) }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                Text(doc.type, fontSize = 13.sp, color = MediumGray)
                Text(sdf.format(Date(doc.uploadedAt)), fontSize = 12.sp, color = SecondaryGray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.6f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDocumentDialog(onDismiss: () -> Unit, onSave: (Document) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var showTypeMenu by remember { mutableStateOf(false) }
    val types = listOf("Contrato de locação", "Laudo de vistoria", "Documento do imóvel",
        "Documento do locatário", "Comprovante de pagamento", "Outro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar documento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ImobiTextField(value = name, onValueChange = { name = it }, label = "Nome do documento")
                ExposedDropdownMenuBox(expanded = showTypeMenu, onExpandedChange = { showTypeMenu = it }) {
                    OutlinedTextField(
                        value = type, onValueChange = {}, readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTypeMenu) },
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(text = { Text(t) }, onClick = { type = t; showTypeMenu = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onSave(Document(name = name, type = type))
            }) { Text("Adicionar", color = PrimaryBlue, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        shape = RoundedCornerShape(16.dp)
    )
}
