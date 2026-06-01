package com.hendry.saku.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginButton_whenEmailIsEmpty_showsEmailError() {
        composeTestRule.setContent {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LoginContent(
                email = "",
                password = "password123",
                passwordVisible = false,
                errorMessage = errorMessage,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordVisibilityChange = {},
                onLoginClick = {
                    errorMessage = "Email tidak boleh kosong"
                },
                onRegisterClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("loginButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Email tidak boleh kosong")
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_whenPasswordIsEmpty_showsPasswordError() {
        composeTestRule.setContent {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LoginContent(
                email = "user@email.com",
                password = "",
                passwordVisible = false,
                errorMessage = errorMessage,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordVisibilityChange = {},
                onLoginClick = {
                    errorMessage = "Password tidak boleh kosong"
                },
                onRegisterClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("loginButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Password tidak boleh kosong")
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_whenClicked_callsLoginClick() {
        var isLoginClicked = false

        composeTestRule.setContent {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginContent(
                email = email,
                password = password,
                passwordVisible = false,
                errorMessage = null,
                isLoading = false,
                onEmailChange = {
                    email = it
                },
                onPasswordChange = {
                    password = it
                },
                onPasswordVisibilityChange = {},
                onLoginClick = {
                    isLoginClicked = true
                },
                onRegisterClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("emailField")
            .performTextInput("user@email.com")

        composeTestRule
            .onNodeWithTag("passwordField")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithTag("loginButton")
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(isLoginClicked)
        }
    }

    @Test
    fun registerText_whenClicked_callsRegisterClick() {
        var isRegisterClicked = false

        composeTestRule.setContent {
            LoginContent(
                email = "",
                password = "",
                passwordVisible = false,
                errorMessage = null,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordVisibilityChange = {},
                onLoginClick = {},
                onRegisterClick = {
                    isRegisterClicked = true
                }
            )
        }

        composeTestRule
            .onNodeWithTag("registerText")
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(isRegisterClicked)
        }
    }

    @Test
    fun loginButton_whenLoading_isDisabledAndShowsProcessingText() {
        composeTestRule.setContent {
            LoginContent(
                email = "user@email.com",
                password = "password123",
                passwordVisible = false,
                errorMessage = null,
                isLoading = true,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordVisibilityChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("loginButton")
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("Memproses...")
            .assertIsDisplayed()
    }

    @Test
    fun passwordToggle_whenClicked_changesPasswordVisibilityState() {
        var isPasswordVisible = false

        composeTestRule.setContent {
            var passwordVisible by remember { mutableStateOf(false) }

            LoginContent(
                email = "user@email.com",
                password = "password123",
                passwordVisible = passwordVisible,
                errorMessage = null,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordVisibilityChange = {
                    passwordVisible = !passwordVisible
                    isPasswordVisible = passwordVisible
                },
                onLoginClick = {},
                onRegisterClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("passwordToggle")
            .assertExists()

        composeTestRule
            .onNodeWithTag("passwordToggle")
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(isPasswordVisible)
        }
    }
}