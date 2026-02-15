package com.example.stellar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stellar.ui.balance.BalanceScreen
import com.example.stellar.ui.home.HomeScreen
import com.example.stellar.ui.profile.ProfileSelectionScreen
import com.example.stellar.ui.receipts.ReceiptUploadScreen
import com.example.stellar.ui.signup.SignupScreen
import com.example.stellar.ui.tasks.TaskScheduleScreen

@Composable
fun StellarNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Signup.route
    ) {
        composable(Screen.Signup.route) {
            SignupScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.ProfileSelection.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ProfileSelection.route) {
            ProfileSelectionScreen(
                onContinue = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ProfileSelection.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Balance.route) {
            BalanceScreen()
        }
        composable(Screen.Tasks.route) {
            TaskScheduleScreen()
        }
        composable(Screen.Receipts.route) {
            ReceiptUploadScreen()
        }
    }
}
