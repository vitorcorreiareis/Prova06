package com.example.prova06.data.repository

import com.example.prova06.data.model.User
import com.example.prova06.utils.AppContext
import com.example.prova06.utils.LocalSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class AuthRepository {

    private val prefs = AppContext.prefs("imobicontrol_auth")
    private val gson = Gson()
    private val userListType = object : TypeToken<List<User>>() {}.type

    val isLoggedIn get() = LocalSession.isLoggedIn

    private fun loadUsers(): List<User> {
        val json = prefs.getString("users", "[]") ?: "[]"
        return try { gson.fromJson(json, userListType) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private fun saveUsers(users: List<User>) {
        prefs.edit().putString("users", gson.toJson(users)).apply()
    }

    suspend fun signIn(email: String, password: String): Result<User> = runCatching {
        val users = loadUsers()
        val user = users.find { it.email.equals(email, ignoreCase = true) }
            ?: error("Usuário não encontrado")
        val stored = prefs.getString("pwd_${user.id}", "") ?: ""
        if (stored != password) error("Senha incorreta")
        LocalSession.save(AppContext.context, user.id, user.email, user.name)
        user
    }

    suspend fun signUp(name: String, email: String, password: String): Result<User> = runCatching {
        val users = loadUsers().toMutableList()
        if (users.any { it.email.equals(email, ignoreCase = true) }) error("E-mail já cadastrado")
        val user = User(id = UUID.randomUUID().toString(), name = name, email = email)
        users.add(user)
        saveUsers(users)
        prefs.edit().putString("pwd_${user.id}", password).apply()
        LocalSession.save(AppContext.context, user.id, user.email, user.name)
        user
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        val users = loadUsers()
        users.find { it.email.equals(email, ignoreCase = true) }
            ?: error("E-mail não encontrado")
        // Local mode: simula envio — senha pode ser redefinida na tela de login
    }

    suspend fun getCurrentUser(): User? {
        if (!LocalSession.isLoggedIn) return null
        return loadUsers().find { it.id == LocalSession.userId }
            ?: User(id = LocalSession.userId, name = LocalSession.userName, email = LocalSession.userEmail)
    }

    suspend fun updateUserProfile(user: User): Result<Unit> = runCatching {
        val users = loadUsers().toMutableList()
        val idx = users.indexOfFirst { it.id == user.id }
        if (idx >= 0) users[idx] = user else users.add(user)
        saveUsers(users)
        LocalSession.save(AppContext.context, user.id, user.email, user.name)
    }

    fun signOut() = LocalSession.clear(AppContext.context)
}
