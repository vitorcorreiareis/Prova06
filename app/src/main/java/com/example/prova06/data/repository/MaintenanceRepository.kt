package com.example.prova06.data.repository

import com.example.prova06.data.model.MaintenanceRequest
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class MaintenanceRepository {

    private val prefs = AppContext.prefs("imobicontrol_maintenances")
    private val gson = Gson()
    private val type = object : TypeToken<List<MaintenanceRequest>>() {}.type
    private val _refresh = MutableStateFlow(0)

    private fun load(uid: String): List<MaintenanceRequest> {
        if (uid.isBlank()) return emptyList()
        val json = prefs.getString("data_$uid", "[]") ?: "[]"
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private fun persist(uid: String, items: List<MaintenanceRequest>) {
        prefs.edit().putString("data_$uid", gson.toJson(items)).apply()
        _refresh.value++
    }

    fun getMaintenancesFlow(): Flow<List<MaintenanceRequest>> =
        LocalSession.userIdFlow.combine(_refresh) { uid, _ -> load(uid) }

    suspend fun saveMaintenance(maintenance: MaintenanceRequest): Result<String> = runCatching {
        val uid = LocalSession.userId
        val list = load(uid).toMutableList()
        if (maintenance.id.isBlank()) {
            val id = UUID.randomUUID().toString()
            list.add(0, maintenance.copy(id = id, userId = uid, createdAt = System.currentTimeMillis()))
            persist(uid, list)
            id
        } else {
            val idx = list.indexOfFirst { it.id == maintenance.id }
            val item = maintenance.copy(userId = uid)
            if (idx >= 0) list[idx] = item else list.add(0, item)
            persist(uid, list)
            maintenance.id
        }
    }

    suspend fun updateStatus(id: String, status: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).map { if (it.id == id) it.copy(status = status) else it })
    }

    suspend fun deleteMaintenance(id: String): Result<Unit> = runCatching {
        val uid = LocalSession.userId
        persist(uid, load(uid).filter { it.id != id })
    }
}
