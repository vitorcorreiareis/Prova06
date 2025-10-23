package com.example.prova06.data.repository

import com.example.prova06.data.model.Document
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class DocumentRepository {

    private val prefs = AppContext.prefs("imobicontrol_documents")
    private val gson = Gson()
    private val type = object : TypeToken<List<Document>>() {}.type
    private val _refresh = MutableStateFlow(0)

    private fun load(uid: String): List<Document> {
        if (uid.isBlank()) return emptyList()
        val json = prefs.getString("data_$uid", "[]") ?: "[]"
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private fun persist(uid: String, items: List<Document>) {
        prefs.edit().putString("data_$uid", gson.toJson(items)).apply()
        _refresh.value++
    }

    fun getDocumentsFlow(): Flow<List<Document>> =
        LocalSession.userIdFlow.combine(_refresh) { uid, _ -> load(uid) }

    suspend fun saveDocument(document: Document): Result<String> = runCatching {
        val uid = LocalSession.userId
        val list = load(uid).toMutableList()
        if (document.id.isBlank()) {
            val id = UUID.randomUUID().toString()
            list.add(0, document.copy(id = id, userId = uid, uploadedAt = System.currentTimeMillis()))
            persist(uid, list)
            id
        } else {
            val idx = list.indexOfFirst { it.id == document.id }
            val item = document.copy(userId = uid)
            if (idx >= 0) list[idx] = item else list.add(0, item)
            persist(uid, list)
            document.id
        }
    }

    suspend fun deleteDocument(id: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).filter { it.id != id })
    }
}
