package com.example.projetmobile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetmobile.ui.BottomNavigationBar
import com.example.projetmobile.ui.screens.HistoryScreen
import com.example.projetmobile.viewmodel.MainViewModel
import com.example.projetmobile.ui.screens.CameraScreen

@Composable
fun LicensePlateScanApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "camera",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("camera") {
                CameraScreen(
                    viewModel = viewModel,
                    permissionsGranted = permissionsGranted
                )
            }
            composable("history") {
                HistoryScreen(viewModel = viewModel)
            }
        }
    }
}