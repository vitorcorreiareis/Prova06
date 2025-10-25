package com.example.prova06.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.ui.components.ImobiButton
import com.example.prova06.ui.components.ImobiTextField
import com.example.prova06.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.PasswordResetSent -> { sent = true; viewModel.resetState() }
            is AuthUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar senha", color = SurfaceWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Voltar", tint = SurfaceWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (sent) {
                        Icon(Icons.Default.Email, null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("E-mail enviado!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkGray)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Verifique sua caixa de entrada e siga as instruções para redefinir sua senha.",
                            color = MediumGray, fontSize = 14.sp, textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        ImobiButton("Voltar ao login", onBack, Modifier.fillMaxWidth())
                    } else {
                        Text("Recuperar senha", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkGray)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Informe seu e-mail para receber as instruções de recuperação.",
                            color = MediumGray, fontSize = 14.sp, textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        ImobiTextField(
                            value = email,
                            onValueChange = { email = it; errorMsg = "" },
                            label = "E-mail",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        if (errorMsg.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(errorMsg, color = ErrorRed, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(24.dp))
                        ImobiButton(
                            text = "Enviar e-mail",
                            onClick = { viewModel.sendPasswordReset(email) },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = uiState is AuthUiState.Loading
                        )
                    }
                }
            }
        }
    }
}
