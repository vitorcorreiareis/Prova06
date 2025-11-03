package com.example.prova06.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isValidCpf(cpf: String): Boolean {
        val digits = cpf.replace("[^0-9]".toRegex(), "")
        if (digits.length != 11) return false
        if (digits.all { it == digits[0] }) return false
        var sum = 0
        for (i in 0..8) sum += digits[i].digitToInt() * (10 - i)
        var rem = (sum * 10) % 11
        if (rem == 10 || rem == 11) rem = 0
        if (rem != digits[9].digitToInt()) return false
        sum = 0
        for (i in 0..9) sum += digits[i].digitToInt() * (11 - i)
        rem = (sum * 10) % 11
        if (rem == 10 || rem == 11) rem = 0
        return rem == digits[10].digitToInt()
    }

    fun isValidCep(cep: String): Boolean {
        val digits = cep.replace("[^0-9]".toRegex(), "")
        return digits.length == 8
    }

    fun isValidPhone(phone: String): Boolean {
        val digits = phone.replace("[^0-9]".toRegex(), "")
        return digits.length in 10..11
    }

    fun isValidName(name: String): Boolean = name.trim().length >= 3

    fun formatCep(cep: String): String {
        val digits = cep.replace("[^0-9]".toRegex(), "")
        return if (digits.length >= 8) "${digits.substring(0, 5)}-${digits.substring(5, 8)}" else digits
    }

    fun formatCpf(cpf: String): String {
        val d = cpf.replace("[^0-9]".toRegex(), "")
        return if (d.length == 11)
            "${d.substring(0, 3)}.${d.substring(3, 6)}.${d.substring(6, 9)}-${d.substring(9)}"
        else d
    }

    fun formatPhone(phone: String): String {
        val d = phone.replace("[^0-9]".toRegex(), "")
        return when {
            d.length == 11 -> "(${d.substring(0, 2)}) ${d.substring(2, 7)}-${d.substring(7)}"
            d.length == 10 -> "(${d.substring(0, 2)}) ${d.substring(2, 6)}-${d.substring(6)}"
            else -> d
        }
    }
}
