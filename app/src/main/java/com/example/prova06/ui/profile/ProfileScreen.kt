package com.example.prova06.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.data.model.User
import com.example.prova06.ui.components.*
import com.example.prova06.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var editMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }

    LaunchedEffect(user) { editName = user?.name ?: "" }
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            successMsg = "Perfil atualizado com sucesso!"
            editMode = false
            viewModel.resetState()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair da conta") },
            text = { Text("Deseja realmente sair da sua conta?") },
            confirmButton = {
                TextButton(onClick = { viewModel.signOut(); onLogout() }) {
                    Text("Sair", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundGray).verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(NavyBlue, PrimaryBlue))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = SurfaceWhite, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(user?.name ?: "Usuário", color = SurfaceWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(user?.email ?: "", color = SurfaceWhite.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset(y = (-8).dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (successMsg.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
                ) {
                    Text(successMsg, color = SuccessGreen, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Informações pessoais", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                        TextButton(onClick = { editMode = !editMode }) {
                            Text(if (editMode) "Cancelar" else "Editar", color = PrimaryBlue)
                        }
                    }
                    HorizontalDivider(color = DividerColor)

                    if (editMode) {
                        ImobiTextField(value = editName, onValueChange = { editName = it }, label = "Nome completo")
                        ImobiTextField(value = user?.email ?: "", onValueChange = {}, label = "E-mail", enabled = false)
                        ImobiButton(
                            text = "Salvar",
                            onClick = {
                                user?.let { u ->
                                    viewModel.updateProfile(u.copy(name = editName))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = uiState is ProfileUiState.Loading
                        )
                    } else {
                        ProfileInfoRow(Icons.Default.Person, "Nome", user?.name ?: "")
                        ProfileInfoRow(Icons.Default.Email, "E-mail", user?.email ?: "")
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Conta", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkGray)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                    ProfileMenuItem(Icons.Default.Lock, "Alterar senha") {}
                    ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Sair da conta", tint = ErrorRed) { showLogoutDialog = true }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MediumGray)
            Text(value, fontSize = 15.sp, color = DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = DarkGray,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, color = tint, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
        }
    }
}
