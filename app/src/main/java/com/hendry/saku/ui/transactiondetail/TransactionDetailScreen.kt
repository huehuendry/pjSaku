package com.hendry.saku.ui.transactiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.utils.format.toReadableDate
import com.hendry.saku.utils.format.toRupiah

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.getTransactionDetail(transactionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {

        Text(
            text = "Detail Transaksi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.transaction == null -> {
                Text(
                    text = "Transaksi tidak ditemukan",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {
                val transaction = uiState.transaction

                if (transaction != null) {
                    TransactionDetailContent(
                        transaction = transaction
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali")
        }
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: Transaction
) {
    val isIncome = transaction.isIncomeTransaction()
    val isTopUp = transaction.type == "TOP_UP"

    val amountColor = if (isIncome) {
        Color(0xFF16A34A)
    } else {
        Color(0xFFDC2626)
    }

    val iconBackgroundColor = if (isIncome) {
        Color(0xFFEAFBF1)
    } else {
        Color(0xFFFEECEC)
    }

    val amountPrefix = if (isIncome) {
        "+ "
    } else {
        "- "
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.clip(CircleShape),
                colors = CardDefaults.cardColors(
                    containerColor = iconBackgroundColor
                )
            ) {
                Text(
                    text = transaction.getTransactionIcon(),
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = amountColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = transaction.getDetailTitle(),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = amountPrefix + transaction.amount.toRupiah(),
                style = MaterialTheme.typography.headlineMedium,
                color = amountColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = transaction.createdAt.toReadableDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            DetailRow(
                label = "Status",
                value = "Berhasil"
            )

            DetailDivider()

            DetailRow(
                label = "Jenis Transaksi",
                value = transaction.getDisplayTransactionType()
            )

            DetailDivider()

            if (isTopUp) {
                DetailRow(
                    label = "Keterangan",
                    value = transaction.description.ifBlank {
                        "Isi saldo Saku"
                    }
                )

                DetailDivider()

                DetailRow(
                    label = "Masuk ke Rekening",
                    value = transaction.receiverAccountNumber.ifBlank {
                        "-"
                    }
                )
            } else {
                DetailRow(
                    label = "Rekening Pengirim",
                    value = transaction.senderAccountNumber.ifBlank {
                        "-"
                    }
                )

                DetailDivider()

                DetailRow(
                    label = "Rekening Tujuan",
                    value = transaction.receiverAccountNumber.ifBlank {
                        "-"
                    }
                )

                DetailDivider()

                DetailRow(
                    label = "Catatan",
                    value = transaction.note.ifBlank {
                        "-"
                    }
                )

                DetailDivider()

                DetailRow(
                    label = "Deskripsi",
                    value = transaction.description.ifBlank {
                        "-"
                    }
                )
            }

            DetailDivider()

            DetailRow(
                label = "Tanggal",
                value = transaction.createdAt.toReadableDate()
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
}

private fun Transaction.isIncomeTransaction(): Boolean {
    return type == "TRANSFER_IN" || type == "TOP_UP"
}

private fun Transaction.getDetailTitle(): String {
    return when (type) {
        "TOP_UP" -> "Top Up Berhasil"
        "TRANSFER_IN" -> "Transfer Masuk"
        "TRANSFER_OUT" -> "Transfer Berhasil"
        else -> "Transaksi Berhasil"
    }
}

private fun Transaction.getDisplayTransactionType(): String {
    return when (type) {
        "TOP_UP" -> "Top Up Saldo"
        "TRANSFER_IN" -> "Transfer Masuk"
        "TRANSFER_OUT" -> "Transfer Keluar"
        else -> title.ifBlank {
            "Transaksi"
        }
    }
}

private fun Transaction.getTransactionIcon(): String {
    return when (type) {
        "TOP_UP" -> "+"
        "TRANSFER_IN" -> "↓"
        "TRANSFER_OUT" -> "↑"
        else -> "✓"
    }
}