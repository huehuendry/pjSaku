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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var appName: String

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

        setContent {
            SakuTheme {
                NavGraph()
            }
        }
    }
}