package com.hendry.saku.ui.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.hendry.saku.navigation.Screen
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {

        delay(2000)

        if (viewModel.isUserLoggedIn()) {

            navController.navigate(
                Screen.Dashboard.route
            ) {

                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }

        } else {

            navController.navigate(
                Screen.Login.route
            ) {

                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),

        verticalArrangement =
        Arrangement.Center,

        horizontalAlignment =
        Alignment.CenterHorizontally
    ) {

        Text(
            text = "SAKU",

            style =
            MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Mobile Banking App"
        )
    }
}