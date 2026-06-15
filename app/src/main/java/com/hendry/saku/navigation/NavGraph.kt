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
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import com.hendry.saku.ui.topup.TopUpScreen
import com.hendry.saku.ui.privacy.PrivacyPolicyScreen

@Composable
fun NavGraph(
    pendingTransactionId: String? = null,
    onPendingTransactionHandled: () -> Unit = {}
) {

    val navController = rememberNavController()

    LaunchedEffect(pendingTransactionId) {
        val transactionId = pendingTransactionId

        if (!transactionId.isNullOrBlank()) {
            navController.navigate(
                Screen.TransactionDetail.createRoute(transactionId)
            ) {
                launchSingleTop = true
            }

            onPendingTransactionHandled()
        }
    }


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

            composable(Screen.TopUp.route) {
                TopUpScreen(navController)
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(navController)
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
            route = Screen.Dashboard.route,
            icon = Icons.Rounded.Home
        ),
        BottomNavItem(
            label = "History",
            route = Screen.History.route,
            icon = Icons.Rounded.History
        ),
        BottomNavItem(
            label = "Akun",
            route = Screen.Profile.route,
            icon = Icons.Rounded.AccountCircle
        )
    )

    NavigationBar(
        modifier = Modifier.height(64.dp),
        windowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { item ->

            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {

                        if (item.route == Screen.Dashboard.route) {

                            val popped = navController.popBackStack(
                                route = Screen.Dashboard.route,
                                inclusive = false
                            )

                            if (!popped) {
                                navController.navigate(Screen.Dashboard.route) {
                                    launchSingleTop = true
                                }
                            }

                        } else {

                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            )
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)