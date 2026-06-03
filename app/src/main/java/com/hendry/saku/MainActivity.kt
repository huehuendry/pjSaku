package com.hendry.saku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hendry.saku.navigation.NavGraph
import com.hendry.saku.ui.theme.SakuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.graphics.Color
import androidx.core.view.WindowCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hendry.saku.navigation.NavGraph
import com.hendry.saku.notification.NotificationHelper
import com.hendry.saku.ui.theme.SakuTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingTransactionId by mutableStateOf<String?>(null)

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.parseColor("#0F172A")
        window.navigationBarColor = Color.WHITE

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = true

        pendingTransactionId = getTransactionIdFromIntent(intent)

        requestNotificationPermission()

        setContent {
            SakuTheme {
                NavGraph(
                    pendingTransactionId = pendingTransactionId,
                    onPendingTransactionHandled = {
                        pendingTransactionId = null
                    }
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            val isGranted = ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                requestNotificationPermissionLauncher.launch(permission)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        pendingTransactionId = getTransactionIdFromIntent(intent)
    }

    private fun getTransactionIdFromIntent(intent: Intent?): String? {
        if (intent?.action != NotificationHelper.ACTION_OPEN_TRANSACTION_DETAIL) {
            return null
        }

        return intent.getStringExtra(NotificationHelper.EXTRA_TRANSACTION_ID)
    }
}