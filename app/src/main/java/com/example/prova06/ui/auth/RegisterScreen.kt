package com.example.prova06.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success -> { viewModel.resetState(); onRegisterSuccess() }
            is AuthUiState.Error -> errorMsg = s.message
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue, navigationIconContentColor = SurfaceWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Brush.verticalGradient(listOf(NavyBlue, PrimaryBlue))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Criar Conta", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Preencha seus dados para continuar", color = SurfaceWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ImobiTextField(
                        value = name,
                        onValueChange = { name = it; errorMsg = "" },
                        label = "Nome completo",
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(Modifier.height(16.dp))
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
                    Spacer(Modifier.height(16.dp))
                    ImobiPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMsg = "" },
                        label = "Confirmar senha"
                    )

                    if (errorMsg.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMsg, color = ErrorRed, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(24.dp))
                    ImobiButton(
                        text = "Criar conta",
                        onClick = { viewModel.signUp(name, email, password, confirmPassword) },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = uiState is AuthUiState.Loading
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Já tem conta? ", color = MediumGray, fontSize = 14.sp)
                        Text("Fazer login", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onBack))
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
