package com.example.prova06.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.User
import com.example.prova06.data.repository.AuthRepository
import com.example.prova06.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object PasswordResetSent : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn get() = repository.isLoggedIn

    fun signIn(email: String, password: String) {
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("E-mail inválido")
            return
        }
        if (!ValidationUtils.isValidPassword(password)) {
            _uiState.value = AuthUiState.Error("Senha deve ter pelo menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signIn(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Erro ao fazer login") }
        }
    }

    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        if (!ValidationUtils.isValidName(name)) {
            _uiState.value = AuthUiState.Error("Nome deve ter pelo menos 3 caracteres")
            return
        }
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("E-mail inválido")
            return
        }
        if (!ValidationUtils.isValidPassword(password)) {
            _uiState.value = AuthUiState.Error("Senha deve ter pelo menos 6 caracteres")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("As senhas não coincidem")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signUp(name, email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Erro ao cadastrar") }
        }
    }

    fun sendPasswordReset(email: String) {
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("E-mail inválido")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.sendPasswordReset(email)
                .onSuccess { _uiState.value = AuthUiState.PasswordResetSent }
                .onFailure { _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Erro ao enviar e-mail") }
        }
    }

    fun signOut() = repository.signOut()

    fun resetState() { _uiState.value = AuthUiState.Idle }
}
