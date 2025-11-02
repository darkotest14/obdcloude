package com.obdcloud.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.obdcloud.feature.adapter.ui.AdapterScreen
import com.obdcloud.feature.diagnostics.ui.DiagnosticCommandScreen
import com.obdcloud.feature.garage.ui.GarageScreen
import com.obdcloud.feature.settings.ui.SettingsScreen

@Composable
fun OBDCloudNavGraph(
    navController: NavHostController,
    startDestination: String = OBDCloudDestinations.GARAGE_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(OBDCloudDestinations.GARAGE_ROUTE) {
            GarageScreen(
                onAddVehicle = { navController.navigate(OBDCloudDestinations.ADD_VEHICLE_ROUTE) },
                onVehicleClick = { vehicle ->
                    // TODO: Navigate to vehicle details
                }
            )
        }
        
        composable(OBDCloudDestinations.ADAPTER_ROUTE) {
            AdapterScreen(
                onConnected = { navController.navigate(OBDCloudDestinations.DIAGNOSTICS_ROUTE) }
            )
        }
        
        composable(OBDCloudDestinations.DIAGNOSTICS_ROUTE) {
            DiagnosticCommandScreen()
        }
        
        composable(OBDCloudDestinations.SETTINGS_ROUTE) {
            SettingsScreen()
        }
        
        // TODO: Add more destinations
    }
}

object OBDCloudDestinations {
    const val GARAGE_ROUTE = "garage"
    const val ADAPTER_ROUTE = "adapter"
    const val DIAGNOSTICS_ROUTE = "diagnostics"
    const val SETTINGS_ROUTE = "settings"
    const val ADD_VEHICLE_ROUTE = "add_vehicle"
}