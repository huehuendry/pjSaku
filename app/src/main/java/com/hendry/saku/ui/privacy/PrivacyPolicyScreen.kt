package com.hendry.saku.ui.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PrivacyPolicyScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Kebijakan Privasi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Terakhir diperbarui: Juni 2026",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrivacySectionCard(
            title = "Tentang Saku",
            body = "Saku adalah aplikasi simulasi mobile banking yang dibuat untuk tujuan pembelajaran dan portfolio. Seluruh saldo, nomor rekening, transaksi, transfer, dan top up di dalam aplikasi bersifat simulasi dan tidak merepresentasikan uang asli atau layanan keuangan nyata."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Data yang Disimpan",
            body = "Aplikasi dapat menyimpan data seperti nama, email, UID akun, nomor rekening simulasi, saldo simulasi, riwayat transaksi, rekening tersimpan, token notifikasi Firebase Cloud Messaging, serta waktu pembaruan token notifikasi."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Penggunaan Data",
            body = "Data digunakan untuk menjalankan fitur utama aplikasi, seperti login, register, menampilkan profil, menampilkan saldo, melakukan transfer simulasi, top up saldo simulasi, menampilkan riwayat transaksi, menyimpan rekening tujuan, dan mengirim notifikasi lokal terkait transaksi."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Penyimpanan Data",
            body = "Data aplikasi disimpan menggunakan Firebase Authentication dan Cloud Firestore. Data hanya digunakan untuk kebutuhan fitur aplikasi Saku dan tidak digunakan untuk memproses transaksi keuangan nyata."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Notifikasi",
            body = "Saku dapat menyimpan token Firebase Cloud Messaging untuk kebutuhan pengembangan fitur notifikasi. Saat ini aplikasi juga dapat menampilkan notifikasi lokal setelah transaksi tertentu berhasil dilakukan."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Hapus Akun dan Data",
            body = "Pengguna dapat menghapus akun melalui halaman Profile. Setelah akun dihapus, data profil, riwayat transaksi milik pengguna, dan rekening tersimpan akan dihapus dari database aplikasi. Akun Firebase Authentication juga akan dihapus."
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrivacySectionCard(
            title = "Bukan Layanan Keuangan",
            body = "Saku bukan aplikasi bank, dompet digital, pinjaman, investasi, pembayaran, atau layanan keuangan resmi. Aplikasi ini tidak terhubung dengan bank, payment gateway, atau institusi keuangan mana pun."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PrivacySectionCard(
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }
    }
}