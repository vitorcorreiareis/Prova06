package com.example.prova06.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalSession {

    private val _userId = MutableStateFlow("")
    val userIdFlow: StateFlow<String> = _userId.asStateFlow()

    var userId: String
        get() = _userId.value
        set(value) { _userId.value = value }

    var userEmail: String = ""
    var userName: String = ""

    val isLoggedIn get() = userId.isNotBlank()

    fun save(context: Context, id: String, email: String, name: String) {
        userId = id
        userEmail = email
        userName = name
        context.getSharedPreferences("session", Context.MODE_PRIVATE).edit()
            .putString("userId", id)
            .putString("userEmail", email)
            .putString("userName", name)
            .apply()
    }

    fun restore(context: Context) {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val id = prefs.getString("userId", "") ?: ""
        val email = prefs.getString("userEmail", "") ?: ""
        val name = prefs.getString("userName", "") ?: ""
        if (id.isNotBlank()) { userId = id; userEmail = email; userName = name }
    }

    fun clear(context: Context) {
        userId = ""
        userEmail = ""
        userName = ""
        context.getSharedPreferences("session", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
