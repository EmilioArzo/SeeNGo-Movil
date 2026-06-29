package com.emilionavarro.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emilionavarro.prueba.ui.theme.PruebaTheme
import com.emilionavarro.prueba.dispositivos.ConfigureDeviceScreen
import com.emilionavarro.prueba.dispositivos.DeviceDetailScreen
import com.emilionavarro.prueba.dispositivos.DeviceSuccessScreen
import com.emilionavarro.prueba.dispositivos.DevicesScreen
import com.emilionavarro.prueba.autenticacion.ForgotPasswordScreen
import com.emilionavarro.prueba.dispositivos.FoundDevicesScreen
import com.emilionavarro.prueba.autenticacion.LoginScreen
import com.emilionavarro.prueba.autenticacion.NewPasswordScreen
import com.emilionavarro.prueba.autenticacion.RegisterScreen
import com.emilionavarro.prueba.dispositivos.ScanNetworkScreen
import com.emilionavarro.prueba.autenticacion.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PruebaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // ── Splash ────────────────────────────────────────────────────────
        composable("splash") {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // ── Login ─────────────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                onLogin = {
                    navController.navigate("devices") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onCreateAccount = {
                    navController.navigate("register")
                }
            )
        }

        // ── Register ──────────────────────────────────────────────────────
        composable("register") {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreateAccount = {
                    navController.navigate("devices") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ── Forgot password ───────────────────────────────────────────────
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSend = {
                    navController.navigate("new_password")
                }
            )
        }

        // ── New password ──────────────────────────────────────────────────
        composable("new_password") {
            NewPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSubmit = {
                    navController.navigate("login") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                }
            )
        }

        // ── Devices (home) ────────────────────────────────────────────────
        composable("devices") {
            DevicesScreen(
                onAddDevice = {
                    navController.navigate("scan_network")
                },
                onNavInicio = { /* TODO: navegar a Inicio */ },
                onNavSenas  = { /* TODO: navegar a Señas */ },
                onNavSugerencias = { /* TODO: navegar a Sugerencias */ },
                onNavPerfil = { /* TODO: navegar a Perfil */ }
            )
        }

        // ── Device detail ─────────────────────────────────────────────────
        composable(
            route = "device_detail/{deviceName}/{roomName}",
            arguments = listOf(
                navArgument("deviceName") { type = NavType.StringType },
                navArgument("roomName")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deviceName = backStackEntry.arguments?.getString("deviceName") ?: ""
            val roomName   = backStackEntry.arguments?.getString("roomName")   ?: ""
            DeviceDetailScreen(
                deviceName   = deviceName,
                roomAndPlug  = "$roomName · Shelly Plug",
                onBack = {
                    navController.popBackStack()
                },
                onMore = { /* mostrar menú de opciones */ }
            )
        }

        // ── Add device: Step 1 — Scan network ────────────────────────────
        composable("scan_network") {
            ScanNetworkScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.navigate("devices") {
                        popUpTo("scan_network") { inclusive = true }
                    }
                },
                onViewFound = {
                    navController.navigate("found_devices")
                }
            )
        }

        // ── Add device: Step 2 — Found devices ───────────────────────────
        composable("found_devices") {
            FoundDevicesScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRescan = {
                    navController.popBackStack()
                },
                onLink = {
                    navController.navigate("configure_device")
                }
            )
        }

        // ── Add device: Step 3 — Configure ───────────────────────────────
        composable("configure_device") {
            ConfigureDeviceScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSave = { name, room, icon ->
                    navController.navigate("device_success/$name/$room") {
                        popUpTo("scan_network") { inclusive = true }
                    }
                }
            )
        }

        // ── Add device: Success ───────────────────────────────────────────
        composable(
            route = "device_success/{deviceName}/{roomName}",
            arguments = listOf(
                navArgument("deviceName") { type = NavType.StringType },
                navArgument("roomName")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deviceName = backStackEntry.arguments?.getString("deviceName") ?: "Dispositivo"
            val roomName   = backStackEntry.arguments?.getString("roomName")   ?: "Sala"
            DeviceSuccessScreen(
                deviceName = deviceName,
                roomName   = roomName,
                onLinkGesture = {
                    /* TODO: navegar al flujo de vincular seña */
                },
                onDone = {
                    navController.navigate("devices") {
                        popUpTo("devices") { inclusive = false }
                    }
                }
            )
        }
    }
}