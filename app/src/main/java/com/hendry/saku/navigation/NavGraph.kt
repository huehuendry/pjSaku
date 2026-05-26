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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hendry.saku.ui.profile.ProfileScreen
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.History.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                SakuBottomNavigation(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
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

            composable(Screen.History.route) {
                HistoryScreen(navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController)
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
}

@Composable
private fun SakuBottomNavigation(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem(
            label = "Home",
            route = Screen.Dashboard.route
        ),
        BottomNavItem(
            label = "History",
            route = Screen.History.route
        ),
        BottomNavItem(
            label = "Akun",
            route = Screen.Profile.route
        )
    )

    NavigationBar(
        modifier = Modifier.height(64.dp),
        windowInsets = WindowInsets(0.dp)
    ) {
        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {},
                label = {
                    Text(item.label)
                }
            )
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val route: String
)