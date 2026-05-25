package com.hendry.saku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hendry.saku.ui.auth.LoginScreen
import com.hendry.saku.ui.auth.RegisterScreen
import com.hendry.saku.ui.dashboard.DashboardScreen
import com.hendry.saku.ui.splash.SplashScreen
import com.hendry.saku.ui.transfer.TransferScreen
import com.hendry.saku.ui.receipt.ReceiptScreen
import com.hendry.saku.ui.history.HistoryScreen
import com.hendry.saku.ui.transactiondetail.TransactionDetailScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(Screen.Transfer.route) {
            TransferScreen(navController)
        }

        composable(Screen.Receipt.route) { backStackEntry ->

            val transactionId =
                backStackEntry.arguments
                    ?.getString("transactionId")
                    ?: ""

            ReceiptScreen(
                navController = navController,
                transactionId = transactionId
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(navController)
        }

        composable(Screen.TransactionDetail.route) { backStackEntry ->

            val transactionId =
                backStackEntry.arguments
                    ?.getString("transactionId")
                    ?: ""

            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId
            )
        }

    }
}