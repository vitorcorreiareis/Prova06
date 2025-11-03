package com.example.prova06.utils

import android.content.Context
import android.content.SharedPreferences

object AppContext {
    private var _context: Context? = null
    val context: Context get() = _context ?: error("AppContext not initialized")

    fun init(context: Context) {
        _context = context.applicationContext
    }

    fun prefs(name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
}
