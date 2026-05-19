package com.hendry.saku.ui.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hendry.saku.navigation.Screen
import com.hendry.saku.utils.toRupiah

@Composable
fun ReceiptScreen(
    navController: NavController,
    amount: Long,
    receiverAccount: String,
    note: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Transfer Berhasil",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text("Status")
                Text(
                    text = "Berhasil",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Nominal")
                Text(
                    text = amount.toRupiah(),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rekening Tujuan")
                Text(
                    text = receiverAccount,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Catatan")
                Text(
                    text = note.ifBlank { "-" },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Receipt.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali ke Dashboard")
        }
    }
}