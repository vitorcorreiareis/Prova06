package com.example.prova06.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.User
import com.example.prova06.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadUser() }

    fun loadUser() {
        viewModelScope.launch {
            _user.value = repository.getCurrentUser()
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.updateUserProfile(user)
                .onSuccess {
                    _user.value = user
                    _uiState.value = ProfileUiState.Success
                }
                .onFailure { _uiState.value = ProfileUiState.Error(it.localizedMessage ?: "Erro ao atualizar") }
        }
    }

    fun signOut() = repository.signOut()

    fun resetState() { _uiState.value = ProfileUiState.Idle }
}
