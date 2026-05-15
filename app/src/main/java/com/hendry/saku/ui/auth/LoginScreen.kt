package com.hendry.saku.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hendry.saku.navigation.Screen
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            navController.navigate(Screen.Dashboard.route) {

                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }

            viewModel.resetState()
        }
    }

    val annotatedText = buildAnnotatedString {

        append("Belum punya akun? ")

        pushStringAnnotation(
            tag = "REGISTER",
            annotation = "register"
        )

        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Register")
        }

        pop()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to your Saku account",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),

            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            modifier = Modifier.fillMaxWidth(),

            visualTransformation =
            PasswordVisualTransformation(),

            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(

            onClick = {

                viewModel.login(
                    email = email,
                    password = password
                )
            },

            modifier = Modifier.fillMaxWidth(),

            enabled = !uiState.isLoading

        ) {

            Text(
                text =
                if (uiState.isLoading)
                    "Loading..."
                else
                    "Login"
            )
        }

        uiState.errorMessage?.let {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = annotatedText,

            onClick = { offset ->

                annotatedText
                    .getStringAnnotations(
                        tag = "REGISTER",
                        start = offset,
                        end = offset
                    )
                    .firstOrNull()
                    ?.let {

                        navController.navigate(
                            Screen.Register.route
                        )
                    }
            }
        )
    }
}