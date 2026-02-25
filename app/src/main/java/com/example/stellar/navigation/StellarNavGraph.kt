package com.example.stellar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stellar.ui.balance.AddExpenseScreen
import com.example.stellar.ui.balance.BalanceScreen
import com.example.stellar.ui.home.HomeScreen
import com.example.stellar.ui.profile.HouseholdRepository
import com.example.stellar.ui.profile.ProfileRepository
import com.example.stellar.ui.profile.ProfileSelectionScreen
import com.example.stellar.ui.profile.EditProfileScreen
import com.example.stellar.ui.profile.ProfileScreen
import com.example.stellar.ui.signup.SignupScreen
import com.example.stellar.ui.tasks.AddTaskScreen
import com.example.stellar.ui.tasks.TaskScheduleScreen

@Composable
fun StellarNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Signup.route
    ) {
        composable(Screen.Signup.route) {
            SignupScreen(
                onAuthSuccess = { name, email ->
                    ProfileRepository.setFromSignup(name, email)
                    navController.navigate(Screen.ProfileSelection.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ProfileSelection.route) {
            ProfileSelectionScreen(
                onContinue = { householdName ->
                    // Only set household name, not user's display name (which comes from signup)
                    HouseholdRepository.setFirstHousehold(householdName)
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
            BalanceScreen(
                onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) }
            )
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Screen.Tasks.route) {
            TaskScheduleScreen(
                onAddTaskClick = { navController.navigate(Screen.AddTask.route) }
            )
        }
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EditProfile.route) }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
