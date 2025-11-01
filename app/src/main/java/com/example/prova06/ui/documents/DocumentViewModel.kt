package com.example.prova06.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova06.data.model.Document
import com.example.prova06.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DocumentUiState {
    object Idle : DocumentUiState()
    object Loading : DocumentUiState()
    object Success : DocumentUiState()
    data class Error(val message: String) : DocumentUiState()
}

class DocumentViewModel(
    private val repository: DocumentRepository = DocumentRepository()
) : ViewModel() {

    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents.asStateFlow()

    private val _uiState = MutableStateFlow<DocumentUiState>(DocumentUiState.Idle)
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    init { loadDocuments() }

    fun loadDocuments() {
        viewModelScope.launch {
            repository.getDocumentsFlow().collect { _documents.value = it }
        }
    }

    fun saveDocument(document: Document) {
        viewModelScope.launch {
            _uiState.value = DocumentUiState.Loading
            repository.saveDocument(document)
                .onSuccess { _uiState.value = DocumentUiState.Success }
                .onFailure { _uiState.value = DocumentUiState.Error(it.localizedMessage ?: "Erro ao salvar") }
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            _uiState.value = DocumentUiState.Loading
            repository.deleteDocument(id)
                .onSuccess { _uiState.value = DocumentUiState.Success }
                .onFailure { _uiState.value = DocumentUiState.Error(it.localizedMessage ?: "Erro ao excluir") }
        }
    }

    fun resetState() { _uiState.value = DocumentUiState.Idle }
}
