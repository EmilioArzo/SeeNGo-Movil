package com.emilionavarro.prueba

import com.emilionavarro.prueba.autenticacion.ForgotPasswordScreen
import com.emilionavarro.prueba.autenticacion.LoginScreen
import com.emilionavarro.prueba.autenticacion.NewPasswordScreen
import com.emilionavarro.prueba.autenticacion.RegisterScreen
import com.emilionavarro.prueba.autenticacion.SplashScreen
import com.emilionavarro.prueba.dispositivos.ConfigureDeviceScreen
import com.emilionavarro.prueba.dispositivos.DeviceDetailScreen
import com.emilionavarro.prueba.dispositivos.DeviceSuccessScreen
import com.emilionavarro.prueba.dispositivos.DevicesScreen
import com.emilionavarro.prueba.dispositivos.FoundDevicesScreen
import com.emilionavarro.prueba.dispositivos.ScanNetworkScreen
import com.emilionavarro.prueba.perfil.EditProfileScreen
import com.emilionavarro.prueba.perfil.PreferencesScreen
import com.emilionavarro.prueba.perfil.ProfileScreen
import com.emilionavarro.prueba.perfil.SettingsScreen
import com.emilionavarro.prueba.senas.GesturesScreen
import com.emilionavarro.prueba.sugerencias.SuggestionsScreen
import com.emilionavarro.seengo.inicio.HomeScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emilionavarro.prueba.inicio.DeviceDetailSkeleton
import com.emilionavarro.prueba.inicio.DevicesScreenSkeleton
import com.emilionavarro.prueba.inicio.GenericDetailSkeleton
import com.emilionavarro.prueba.inicio.GesturesScreenSkeleton
import com.emilionavarro.prueba.inicio.HomeScreenSkeleton
import com.emilionavarro.prueba.inicio.ProfileScreenSkeleton
import com.emilionavarro.prueba.inicio.SuggestionsScreenSkeleton
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}

// ── Rutas ─────────────────────────────────────────────────────────────────────
object Routes {
    const val SPLASH           = "splash"
    const val LOGIN            = "login"
    const val REGISTER         = "register"
    const val FORGOT_PASSWORD  = "forgot_password"
    const val NEW_PASSWORD     = "new_password"

    const val HOME             = "home"
    const val GESTURES         = "gestures"
    const val DEVICES          = "devices"
    const val SUGGESTIONS      = "suggestions"
    const val PROFILE          = "profile"

    const val DEVICE_DETAIL    = "device_detail"
    const val SCAN_NETWORK     = "scan_network"
    const val FOUND_DEVICES    = "found_devices"
    const val CONFIGURE_DEVICE = "configure_device"
    const val DEVICE_SUCCESS   = "device_success"

    const val EDIT_PROFILE     = "edit_profile"
    const val SETTINGS         = "settings"
    const val PREFERENCES      = "preferences"
}

// ── NavHost ───────────────────────────────────────────────────────────────────
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── Auth ──────────────────────────────────────────────────────────

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            LoginScreen(
                onLogin = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoogleLogin = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onAppleLogin = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onCreateAccount  = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            RegisterScreen(
                onBack          = { navController.popBackStack() },
                onCreateAccount = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSend = { navController.navigate(Routes.NEW_PASSWORD) }
            )
        }

        composable(Routes.NEW_PASSWORD) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            NewPasswordScreen(
                onBack   = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // ── Main tabs ─────────────────────────────────────────────────────

        composable(Routes.HOME) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300)
                ready = true
            }
            if (!ready) HomeScreenSkeleton()
            else
            HomeScreen(
                onNavInicio       = {},
                onNavSenas        = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPerfil       = { navController.navigate(Routes.PROFILE) },
                onNotifications   = {},
                onVerTodos        = { navController.navigate(Routes.DEVICES) }
            )
        }

        composable(Routes.GESTURES) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GesturesScreenSkeleton()
            else
            GesturesScreen(
                onAddGesture      = {},
                onGestureClick    = { navController.navigate(Routes.DEVICE_DETAIL) },
                onNavInicio       = { navController.navigate(Routes.HOME) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPerfil       = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.DEVICES) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) DevicesScreenSkeleton()
            else
            DevicesScreen(
                onAddDevice       = { navController.navigate(Routes.SCAN_NETWORK) },
                onNavInicio       = { navController.navigate(Routes.HOME) },
                onNavSenas        = { navController.navigate(Routes.GESTURES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPerfil       = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.SUGGESTIONS) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) SuggestionsScreenSkeleton()
            else
            SuggestionsScreen(
                onNavInicio        = { navController.navigate(Routes.HOME) },
                onNavSenas         = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos  = { navController.navigate(Routes.DEVICES) },
                onNavPerfil        = { navController.navigate(Routes.PROFILE) },
                onActivateFeatured = {},
                onSuggestionClick  = {},
                onFilter           = {}
            )
        }

        composable(Routes.PROFILE) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) ProfileScreenSkeleton()
            else
            ProfileScreen(
                onEdit   = { navController.navigate(Routes.EDIT_PROFILE) },
                onConfiguration = { navController.navigate(Routes.SETTINGS) },
                onPreferences = { navController.navigate(Routes.PREFERENCES)},
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavInicio       = { navController.navigate(Routes.HOME) },
                onNavSenas        = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) }
            )
        }

        // ── Device flow ───────────────────────────────────────────────────

        composable(Routes.DEVICE_DETAIL) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) DeviceDetailSkeleton()
            else
            DeviceDetailScreen(
                onBack = { navController.popBackStack() },
                onMore = {}
            )
        }

        composable(Routes.SCAN_NETWORK) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            ScanNetworkScreen(
                onBack      = { navController.popBackStack() },
                onCancel    = { navController.popBackStack() },
                onViewFound = { navController.navigate(Routes.FOUND_DEVICES) }
            )
        }

        composable(Routes.FOUND_DEVICES) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            FoundDevicesScreen(
                onBack   = { navController.popBackStack() },
                onRescan = { navController.popBackStack() },
                onLink   = { navController.navigate(Routes.CONFIGURE_DEVICE) }
            )
        }

        composable(Routes.CONFIGURE_DEVICE) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            ConfigureDeviceScreen(
                onBack    = { navController.popBackStack() },
                onSave    = { _, _, _ ->
                    navController.navigate(Routes.DEVICE_SUCCESS) {
                        popUpTo(Routes.SCAN_NETWORK) { inclusive = true }
                    }
                },
                onAddRoom = {}
            )
        }

        composable(Routes.DEVICE_SUCCESS) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GesturesScreenSkeleton()
            else
            DeviceSuccessScreen(
                onLinkGesture = {
                    navController.navigate(Routes.GESTURES) {
                        popUpTo(Routes.DEVICES) { inclusive = false }
                    }
                },
                onDone = {
                    navController.navigate(Routes.DEVICES) {
                        popUpTo(Routes.DEVICES) { inclusive = false }
                    }
                }
            )
        }

        // ── Profile flow ──────────────────────────────────────────────────

        composable(Routes.EDIT_PROFILE) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSave = { _, _, _, _, _ -> navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PREFERENCES) {
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) GenericDetailSkeleton()
            else
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}