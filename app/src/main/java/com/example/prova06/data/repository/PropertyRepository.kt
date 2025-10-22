package com.example.prova06.data.repository

import com.example.prova06.data.model.Property
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class PropertyRepository {

    private val prefs = AppContext.prefs("imobicontrol_properties")
    private val gson = Gson()
    private val type = object : TypeToken<List<Property>>() {}.type
    private val _refresh = MutableStateFlow(0)

    private fun load(uid: String): List<Property> {
        if (uid.isBlank()) return emptyList()
        val json = prefs.getString("data_$uid", "[]") ?: "[]"
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private fun persist(uid: String, items: List<Property>) {
        prefs.edit().putString("data_$uid", gson.toJson(items)).apply()
        _refresh.value++
    }

    fun getPropertiesFlow(): Flow<List<Property>> =
        LocalSession.userIdFlow.combine(_refresh) { uid, _ -> load(uid) }

    suspend fun getProperty(id: String): Property? =
        load(LocalSession.userId).find { it.id == id }

    suspend fun saveProperty(property: Property): Result<String> = runCatching {
        val uid = LocalSession.userId
        val list = load(uid).toMutableList()
        if (property.id.isBlank()) {
            val id = UUID.randomUUID().toString()
            list.add(0, property.copy(id = id, userId = uid, createdAt = System.currentTimeMillis()))
            persist(uid, list)
            id
        } else {
            val idx = list.indexOfFirst { it.id == property.id }
            val item = property.copy(userId = uid)
            if (idx >= 0) list[idx] = item else list.add(0, item)
            persist(uid, list)
            property.id
        }
    }

    suspend fun deleteProperty(id: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).filter { it.id != id })
    }

    suspend fun updateStatus(id: String, status: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).map { if (it.id == id) it.copy(status = status) else it })
    }
}
