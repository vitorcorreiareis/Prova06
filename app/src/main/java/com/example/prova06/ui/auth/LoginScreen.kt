package com.example.prova06.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prova06.ui.components.ImobiButton
import com.example.prova06.ui.components.ImobiPasswordField
import com.example.prova06.ui.components.ImobiTextField
import com.example.prova06.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success -> { viewModel.resetState(); onLoginSuccess() }
            is AuthUiState.Error -> { errorMsg = s.message }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.verticalGradient(listOf(NavyBlue, PrimaryBlue))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(SurfaceWhite.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Home, null, tint = SurfaceWhite, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("ImobiControl", color = SurfaceWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Acesse sua conta", color = SurfaceWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Entrar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkGray)
                    Spacer(Modifier.height(24.dp))

                    ImobiTextField(
                        value = email,
                        onValueChange = { email = it; errorMsg = "" },
                        label = "E-mail",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(16.dp))
                    ImobiPasswordField(
                        value = password,
                        onValueChange = { password = it; errorMsg = "" },
                        label = "Senha"
                    )

                    if (errorMsg.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMsg, color = ErrorRed, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Esqueci minha senha",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(onClick = onNavigateToForgotPassword)
                    )
                    Spacer(Modifier.height(24.dp))
                    ImobiButton(
                        text = "Entrar",
                        onClick = { viewModel.signIn(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = uiState is AuthUiState.Loading
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Não tem conta? ", color = MediumGray, fontSize = 14.sp)
                        Text(
                            "Cadastre-se",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onNavigateToRegister)
                        )
                    }
                }
            }
        }
    }
}
