package com.hendry.saku.ui.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hendry.saku.data.model.SavedRecipient
import com.hendry.saku.navigation.Screen
import com.hendry.saku.notification.NotificationHelper
import com.hendry.saku.utils.format.toRupiah

@Composable
fun TransferScreen(
    navController: NavController,
    viewModel: TransferViewModel = hiltViewModel()
) {
    var accountNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess, uiState.transactionId) {
        val transactionId = uiState.transactionId

        if (uiState.isSuccess && !transactionId.isNullOrBlank()) {
            val amountText = amount.toLongOrNull()?.toRupiah() ?: "Rp0"

            NotificationHelper.showTransferSuccessNotification(
                context = context,
                amountText = amountText,
                transactionId = transactionId
            )

            navController.navigate(
                Screen.Receipt.createRoute(transactionId)
            ) {
                popUpTo(Screen.Transfer.route) {
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
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {

        Text(
            text = "Transfer",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Kirim uang ke rekening tujuan dengan aman.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.savedRecipients.isNotEmpty()) {
            SavedRecipientsSection(
                savedRecipients = uiState.savedRecipients,
                onRecipientClick = { savedRecipient ->
                    accountNumber = savedRecipient.recipientAccountNumber
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Informasi Transfer",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                SakuTextField(
                    value = accountNumber,
                    onValueChange = {
                        accountNumber = it
                    },
                    label = "Nomor Rekening Tujuan",
                    placeholder = "Contoh: 1234567890",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                SakuTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = "Nominal Transfer",
                    placeholder = "Contoh: 50000",
                    keyboardType = KeyboardType.Number,
                    prefixText = "Rp "
                )

                Spacer(modifier = Modifier.height(16.dp))

                SakuTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    label = "Catatan",
                    placeholder = "Opsional",
                    keyboardType = KeyboardType.Text
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        uiState.errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                viewModel.transfer(
                    accountNumber = accountNumber,
                    amountText = amount,
                    note = note
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(
                text = if (uiState.isLoading) {
                    "Memproses..."
                } else {
                    "Transfer Sekarang"
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Kembali")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SavedRecipientsSection(
    savedRecipients: List<SavedRecipient>,
    onRecipientClick: (SavedRecipient) -> Unit
) {
    Column {
        Text(
            text = "Rekening Tersimpan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = savedRecipients,
                key = { savedRecipient ->
                    savedRecipient.recipientAccountNumber
                }
            ) { savedRecipient ->
                SavedRecipientCard(
                    savedRecipient = savedRecipient,
                    onClick = {
                        onRecipientClick(savedRecipient)
                    }
                )
            }
        }
    }
}

@Composable
private fun SavedRecipientCard(
    savedRecipient: SavedRecipient,
    onClick: () -> Unit
) {
    val initial = savedRecipient.recipientName
        .trim()
        .take(1)
        .uppercase()
        .ifBlank { "?" }

    Card(
        onClick = onClick,
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = savedRecipient.recipientName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = savedRecipient.recipientAccountNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SakuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType,
    prefixText: String? = null
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
        prefix = if (prefixText != null) {
            {
                Text(prefixText)
            }
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}