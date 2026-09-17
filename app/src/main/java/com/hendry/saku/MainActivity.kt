package com.hendry.saku

import android.os.Bundle
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
import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hendry.saku.notification.NotificationHelper
import com.hendry.saku.utils.SessionManager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hendry.saku.utils.network.ConnectivityObserver

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    private var pendingTransactionId by mutableStateOf<String?>(null)

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager.startSession()

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
            val networkStatus by connectivityObserver.observe().collectAsState(
                initial = ConnectivityObserver.Status.Available
            )

            SakuTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        pendingTransactionId = pendingTransactionId,
                        onPendingTransactionHandled = {
                            pendingTransactionId = null
                        }
                    )

                    if (networkStatus != ConnectivityObserver.Status.Available) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ComposeColor.Red)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak Ada Koneksi Internet",
                                color = ComposeColor.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        sessionManager.resetTimer()
        return super.dispatchTouchEvent(ev)
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