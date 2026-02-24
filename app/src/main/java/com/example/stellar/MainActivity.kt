package com.example.stellar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stellar.navigation.Screen
import com.example.stellar.navigation.StellarNavGraph
import com.example.stellar.ui.components.StellarBottomNavBar
import com.example.stellar.ui.theme.StellarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StellarTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Balance.route,
                    Screen.Tasks.route,
                    Screen.Profile.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            StellarBottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        StellarNavGraph(navController)
                    }
                }
            }
        }
    }
}
