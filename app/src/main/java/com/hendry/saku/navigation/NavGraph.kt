package com.hendry.saku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hendry.saku.ui.auth.LoginScreen
import com.hendry.saku.ui.auth.RegisterScreen
import com.hendry.saku.ui.dashboard.DashboardScreen
import com.hendry.saku.ui.splash.SplashScreen

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
    }
}