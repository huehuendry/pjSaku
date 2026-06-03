package com.hendry.saku.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Dashboard : Screen("dashboard")

    data object Transfer : Screen("transfer")

    data object History : Screen("history")

    data object Profile : Screen("profile")

    data object Receipt :
        Screen("receipt/{transactionId}") {

        fun createRoute(
            transactionId: String
        ): String {

            return "receipt/$transactionId"
        }
    }

    data object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String): String {
            return "transaction_detail/$transactionId"
        }
    }

}