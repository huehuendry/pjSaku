package com.hendry.saku.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Dashboard : Screen("dashboard")

    data object Transfer : Screen("transfer")

    data object Receipt : Screen(
        "receipt/{amount}/{receiverAccount}/{note}"
    ) {
        fun createRoute(
            amount: Long,
            receiverAccount: String,
            note: String
        ): String {
            return "receipt/$amount/$receiverAccount/$note"
        }
    }

    data object History : Screen("history")
}