package com.hendry.saku.utils

object LoginValidator {

    fun validateLogin(
        email: String,
        password: String
    ): String? {
        return when {
            email.isBlank() -> "Email tidak boleh kosong"
            password.isBlank() -> "Password tidak boleh kosong"
            else -> null
        }
    }
}