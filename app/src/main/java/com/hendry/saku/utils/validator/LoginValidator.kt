package com.hendry.saku.utils.validator

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