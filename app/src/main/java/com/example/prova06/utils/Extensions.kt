package com.example.prova06.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.toCurrencyBR(): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(this)

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    return sdf.format(Date(this))
}

fun Date.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
    return sdf.format(this)
}

fun String.onlyDigits(): String = replace("[^0-9]".toRegex(), "")

fun String.maskCep(): String {
    val d = onlyDigits()
    return if (d.length >= 8) "${d.substring(0, 5)}-${d.substring(5, minOf(8, d.length))}"
    else d
}

fun String.maskCpf(): String {
    val d = onlyDigits()
    return when {
        d.length >= 11 -> "${d.substring(0, 3)}.${d.substring(3, 6)}.${d.substring(6, 9)}-${d.substring(9, 11)}"
        d.length >= 9 -> "${d.substring(0, 3)}.${d.substring(3, 6)}.${d.substring(6)}-"
        d.length >= 6 -> "${d.substring(0, 3)}.${d.substring(3, 6)}."
        d.length >= 3 -> "${d.substring(0, 3)}."
        else -> d
    }
}

fun String.maskPhone(): String {
    val d = onlyDigits()
    return when {
        d.length >= 11 -> "(${d.substring(0, 2)}) ${d.substring(2, 7)}-${d.substring(7, 11)}"
        d.length >= 7 -> "(${d.substring(0, 2)}) ${d.substring(2, 6)}-${d.substring(6)}"
        d.length >= 2 -> "(${d.substring(0, 2)}) ${d.substring(2)}"
        else -> d
    }
}
