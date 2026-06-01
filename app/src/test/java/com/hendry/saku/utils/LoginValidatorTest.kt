package com.hendry.saku.utils

import com.hendry.saku.utils.validator.LoginValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun `validateLogin returns error when email is empty`() {
        val result = LoginValidator.validateLogin(
            email = "",
            password = "password123"
        )

        assertEquals("Email tidak boleh kosong", result)
    }

    @Test
    fun `validateLogin returns error when password is empty`() {
        val result = LoginValidator.validateLogin(
            email = "user@email.com",
            password = ""
        )

        assertEquals("Password tidak boleh kosong", result)
    }

    @Test
    fun `validateLogin returns null when email and password are valid`() {
        val result = LoginValidator.validateLogin(
            email = "user@email.com",
            password = "password123"
        )

        assertNull(result)
    }
}