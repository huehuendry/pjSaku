package com.hendry.saku.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hendry.saku.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var localError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) {
                    inclusive = true
                }
            }

            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E3A8A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.padding(start = 14.dp)
            ) {
                Text(
                    text = "SAKU",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Aman • Mudah • Terpercaya",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Buat Akun Baru",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Daftar untuk mulai menggunakan layanan Saku.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "Register",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Lengkapi data akun kamu dengan benar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(22.dp))

                SakuRegisterTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        localError = null
                    },
                    label = "Nama Lengkap",
                    placeholder = "Masukkan nama lengkap",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(18.dp))

                SakuRegisterTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        localError = null
                    },
                    label = "Email",
                    placeholder = "contoh@email.com",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(18.dp))

                SakuRegisterTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        localError = null
                    },
                    label = "Password",
                    placeholder = "Minimal 6 karakter",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = {
                        passwordVisible = !passwordVisible
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                SakuRegisterTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = "Konfirmasi Password",
                    placeholder = "Ulangi password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = confirmPasswordVisible,
                    onPasswordVisibilityChange = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        val errorMessage = localError ?: uiState.errorMessage

        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFEECEC)
                )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                localError = when {
                    name.isBlank() -> "Nama tidak boleh kosong"
                    email.isBlank() -> "Email tidak boleh kosong"
                    password.isBlank() -> "Password tidak boleh kosong"
                    confirmPassword.isBlank() -> "Konfirmasi password tidak boleh kosong"
                    password.length < 6 -> "Password minimal 6 karakter"
                    password != confirmPassword -> "Password dan konfirmasi password tidak sama"
                    else -> null
                }

                if (localError == null) {
                    viewModel.register(
                        name = name,
                        email = email,
                        password = password
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.45f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Memproses..."
                } else {
                    "Register"
                },
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Sudah punya akun? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )

            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SakuRegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(
                    onClick = {
                        onPasswordVisibilityChange?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = "Toggle password visibility",
                        tint = Color(0xFF64748B)
                    )
                }
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,

            focusedIndicatorColor = Color(0xFF10B981),
            unfocusedIndicatorColor = Color(0xFFE2E8F0),

            focusedLabelColor = Color(0xFF10B981),
            unfocusedLabelColor = Color(0xFF64748B),

            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),

            cursorColor = Color(0xFF10B981)
        )
    )
}