package com.example.stellar.navigation

sealed class Screen(val route: String) {
    object Signup : Screen("signup")
    object ProfileSelection : Screen("profile_selection")
    object Home : Screen("home")
    object Balance : Screen("balance")
    object AddExpense : Screen("add_expense")
    object Tasks : Screen("tasks")
    object AddTask : Screen("add_task")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Receipts : Screen("receipts")
}
