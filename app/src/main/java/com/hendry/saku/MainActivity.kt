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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

        requestNotificationPermission()

        setContent {
            SakuTheme {
                NavGraph()
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
}