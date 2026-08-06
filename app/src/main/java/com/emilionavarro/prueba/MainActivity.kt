package com.emilionavarro.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

import androidx.compose.runtime.collectAsState
import com.emilionavarro.prueba.perfil.data.AppPreferences
import com.emilionavarro.prueba.perfil.data.AppSettings
import com.emilionavarro.prueba.ui.theme.PruebaTheme

import android.content.Intent
import com.emilionavarro.prueba.dispositivos.data.spotify.SpotifyAuthBridge
import com.emilionavarro.prueba.dispositivos.viewModelFactory

import com.emilionavarro.prueba.autenticacion.ForgotPasswordScreen
import com.emilionavarro.prueba.autenticacion.LoginScreen
import com.emilionavarro.prueba.autenticacion.NewPasswordScreen
import com.emilionavarro.prueba.autenticacion.RegisterScreen
import com.emilionavarro.prueba.autenticacion.SplashScreen
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.dispositivos.ConfigureDeviceScreen
import com.emilionavarro.prueba.dispositivos.DeviceDetailScreen
import com.emilionavarro.prueba.dispositivos.DeviceDiscoveryViewModel
import com.emilionavarro.prueba.dispositivos.DeviceSuccessScreen
import com.emilionavarro.prueba.dispositivos.DevicesScreen
import com.emilionavarro.prueba.dispositivos.FoundDevicesScreen
import com.emilionavarro.prueba.dispositivos.ScanNetworkScreen
import com.emilionavarro.prueba.inicio.DeviceDetailSkeleton
import com.emilionavarro.prueba.inicio.DevicesScreenSkeleton
import com.emilionavarro.prueba.inicio.HomeScreenSkeleton
import com.emilionavarro.prueba.perfil.EditProfileScreen
import com.emilionavarro.prueba.perfil.PreferencesScreen
import com.emilionavarro.prueba.perfil.ProfileScreen
import com.emilionavarro.prueba.perfil.SettingsScreen
import com.emilionavarro.prueba.senas.ConfigureGestureScreen
import com.emilionavarro.prueba.senas.GestureDetailScreen
import com.emilionavarro.prueba.senas.GestureSavedScreen
import com.emilionavarro.prueba.senas.GesturesScreen
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModel
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModelFactory
import com.emilionavarro.prueba.sugerencias.RoutineActivatedScreen
import com.emilionavarro.prueba.sugerencias.RoutineDetailScreen
import com.emilionavarro.prueba.sugerencias.SuggestionsScreen
import com.emilionavarro.prueba.sugerencias.viewmodel.SuggestionsViewModel
import com.emilionavarro.prueba.sugerencias.viewmodel.SuggestionsViewModelFactory
import com.emilionavarro.seengo.inicio.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SpotifyAuthBridge.handleIntent(intent)
        setContent {
            val context = LocalContext.current
            val prefs = remember { AppPreferences(context) }
            val settings by prefs.settingsFlow.collectAsState(initial = AppSettings())

            PruebaTheme(themeMode = settings.theme) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        SpotifyAuthBridge.handleIntent(intent)
    }
}

// ── Rutas ─────────────────────────────────────────────────────────────────────
object Routes {
    // Auth
    const val SPLASH            = "splash"
    const val LOGIN             = "login"
    const val REGISTER          = "register"
    const val FORGOT_PASSWORD   = "forgot_password"
    const val NEW_PASSWORD      = "new_password"

    // Main tabs
    const val HOME              = "home"
    const val GESTURES          = "gestures"
    const val DEVICES           = "devices"
    const val SUGGESTIONS       = "suggestions"
    const val PROFILE           = "profile"

    // Device flow
    const val DEVICE_DETAIL     = "device_detail/{deviceId}"
    // Routes: quita el parámetro {scanId}, ya no se necesita
    const val SCAN_NETWORK  = "scan_network"
    const val FOUND_DEVICES = "found_devices"
    const val CONFIGURE_DEVICE  = "configure_device/{scanId}"
    const val DEVICE_SUCCESS    = "device_success/{deviceId}"
    // dentro de object Routes
    const val BLUETOOTH_SCAN = "bluetooth_scan"


    // Gesture flow
    const val GESTURE_DETAIL    = "gesture_detail/{gestureId}"
    const val CONFIGURE_GESTURE = "configure_gesture/{gestureId}"
    const val GESTURE_SAVED     = "gesture_saved/{gestureId}"

    // Profile flow
    const val EDIT_PROFILE      = "edit_profile"
    const val SETTINGS          = "settings"
    const val PREFERENCES       = "preferences"

    // Suggestions flow
    // NOTA: "{routineId}" en realidad ahora recibe el id de la SUGERENCIA
    // (SuggestionDto.id), ya que el backend aún no distingue una rutina
    // "candidata" (sugerencia) de una rutina real creada.
    const val ROUTINE_DETAIL    = "routine_detail/{routineId}"
    const val ROUTINE_ACTIVATED = "routine_activated/{routineId}"

    // Helpers to build routes with args
    fun deviceDetail(deviceId: String)        = "device_detail/$deviceId"
    fun foundDevices(scanId: String)          = "found_devices/$scanId"
    fun configureDevice(scanId: String)       = "configure_device/$scanId"
    fun deviceSuccess(deviceId: String)       = "device_success/$deviceId"
    fun gestureDetail(gestureId: String)      = "gesture_detail/$gestureId"
    fun configureGesture(gestureId: String)   = "configure_gesture/$gestureId"
    fun gestureSaved(gestureId: String)       = "gesture_saved/$gestureId"
    fun routineDetail(routineId: String)      = "routine_detail/$routineId"
    fun routineActivated(routineId: String)   = "routine_activated/$routineId"
}

// ── NavHost ───────────────────────────────────────────────────────────────────
@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    // ViewModel único compartido entre las 3 pantallas del módulo de Sugerencias.
    // Ver SuggestionsViewModel para el porqué (el backend no tiene GET /suggestions/{id}).
    val suggestionsViewModel: SuggestionsViewModel = viewModel(factory = SuggestionsViewModelFactory())

    // En AppNavHost, junto a gesturesViewModel/suggestionsViewModel:
    val discoveryViewModel: DeviceDiscoveryViewModel = viewModel(
        factory = viewModelFactory { DeviceDiscoveryViewModel(userId = session.getUserId() ?: "") }
    )

    // Igual que en Sugerencias: un solo ViewModel compartido entre las 4 pantallas
    // del módulo de Señas, porque el backend tampoco tiene GET /api/gestures/{id}.
    val gesturesViewModel: GesturesViewModel = viewModel(factory = GesturesViewModelFactory())

    // Decide start destination: if already logged in skip auth
    val startDest = if (session.isLoggedIn()) Routes.HOME else Routes.SPLASH

    NavHost(
        navController    = navController,
        startDestination = startDest
    ) {

        // ══ AUTH ════════════════════════════════════════════════════════════

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    val dest = if (session.isLoggedIn()) Routes.HOME else Routes.LOGIN
                    navController.navigate(dest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            // LoginScreen uses its own SessionManager + LoginViewModel internally
            LoginScreen(
                onLogin = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoogleLogin   = { /* TODO: Google OAuth */ },
                onAppleLogin    = { /* TODO: Apple OAuth */ },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onCreateAccount  = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
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
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSend = { navController.navigate(Routes.NEW_PASSWORD) }
            )
        }

        composable(Routes.NEW_PASSWORD) {
            NewPasswordScreen(
                onBack   = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // ══ MAIN TABS ════════════════════════════════════════════════════════

        composable(Routes.HOME) {
            val userId   = session.getUserId() ?: ""
            val userName = session.getUserName() ?: ""
            HomeScreen(
                userId            = userId,
                userName          = userName,
                onNavInicio       = { /* ya estás aquí */ },
                onNavSenas        = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPerfil       = { navController.navigate(Routes.PROFILE) },
                onNotifications   = { navController.navigate(Routes.SUGGESTIONS) },
                onVerTodos        = { navController.navigate(Routes.DEVICES) },
                onRoutineClick    = { routineId -> navController.navigate(Routes.routineDetail(routineId)) }
            )
        }

        composable(Routes.GESTURES) {
            val userId = session.getUserId() ?: ""
            GesturesScreen(
                userId    = userId,
                viewModel = gesturesViewModel,
                onAddGesture   = {
                    // New gesture: use placeholder id "new"
                    navController.navigate(Routes.configureGesture("new"))
                },
                onGestureClick = { gesture ->
                    navController.navigate(Routes.gestureDetail(gesture.id))
                },
                onNavInicio       = { navController.navigate(Routes.HOME) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPerfil       = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.DEVICES) {
            val userId = session.getUserId() ?: ""
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) DevicesScreenSkeleton()
            else
                DevicesScreen(
                    userId            = userId,
                    onAddDevice       = { navController.navigate(Routes.SCAN_NETWORK) },
                    onNavInicio       = { navController.navigate(Routes.HOME) },
                    onNavSenas        = { navController.navigate(Routes.GESTURES) },
                    onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                    onNavPerfil       = { navController.navigate(Routes.PROFILE) },
                    onDeviceClick     = { deviceId -> navController.navigate(Routes.deviceDetail(deviceId)) },
                )
        }

        composable(Routes.SUGGESTIONS) {
            val userId = session.getUserId() ?: ""
            SuggestionsScreen(
                userId             = userId,
                viewModel          = suggestionsViewModel,
                onNavInicio        = { navController.navigate(Routes.HOME) },
                onNavSenas         = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos  = { navController.navigate(Routes.DEVICES) },
                onNavPerfil        = { navController.navigate(Routes.PROFILE) },
                onActivateFeatured = { card ->
                    suggestionsViewModel.selectSuggestion(card.id)
                    navController.navigate(Routes.routineDetail(card.id))
                },
                onSuggestionClick  = { card ->
                    suggestionsViewModel.selectSuggestion(card.id)
                    navController.navigate(Routes.routineDetail(card.id))
                },
                onFilter = { /* TODO: filter sheet */ }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onEdit   = { navController.navigate(Routes.EDIT_PROFILE) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavInicio       = { navController.navigate(Routes.HOME) },
                onNavSenas        = { navController.navigate(Routes.GESTURES) },
                onNavDispositivos = { navController.navigate(Routes.DEVICES) },
                onNavSugerencias  = { navController.navigate(Routes.SUGGESTIONS) },
                onNavPreferences  = { navController.navigate(Routes.PREFERENCES) }
            )
        }

        // ══ DEVICE FLOW ══════════════════════════════════════════════════════

        composable(Routes.DEVICE_DETAIL) { backStack ->
            val deviceId = backStack.arguments?.getString("deviceId") ?: ""
            var ready by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(300) // tiempo mínimo para evitar flash
                ready = true
            }
            if (!ready) DeviceDetailSkeleton()
            else
                DeviceDetailScreen(
                    deviceId          = deviceId,
                    onBack            = { navController.popBackStack() },
                    onDeleted         = { navController.popBackStack() },
                    onEditGestures    = { },
                    onGestureClick    = { },
                )
        }

        composable(Routes.SCAN_NETWORK) {
            ScanNetworkScreen(
                viewModel = discoveryViewModel,
                onBack    = { navController.popBackStack() },
                onCancel  = { navController.popBackStack() },
                onViewFound = { navController.navigate(Routes.FOUND_DEVICES) },
            )
        }

        composable(Routes.FOUND_DEVICES) {
            FoundDevicesScreen(
                userId    = session.getUserId() ?: "",
                viewModel = discoveryViewModel,
                onBack    = { navController.popBackStack() },
                onLinked  = {
                    navController.navigate(Routes.DEVICE_SUCCESS.replace("{deviceId}", "new")) {
                        popUpTo(Routes.SCAN_NETWORK) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.CONFIGURE_DEVICE) { backStack ->
            val scanId = backStack.arguments?.getString("scanId") ?: ""
            val userId = session.getUserId() ?: ""
            ConfigureDeviceScreen(
                deviceRawName = scanId,
                onBack       = { navController.popBackStack() },
                onSave       = { name, room, icon ->
                    navController.navigate(Routes.deviceSuccess("new")) {
                        popUpTo(Routes.SCAN_NETWORK) { inclusive = true }
                    }
                },
                onAddRoom    = { },
            )
        }

        composable(Routes.DEVICE_SUCCESS) { backStack ->
            val deviceId = backStack.arguments?.getString("deviceId") ?: ""
            DeviceSuccessScreen(
                onLinkGesture = {
                    navController.navigate(Routes.configureGesture("new")) {
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

        // ══ GESTURE FLOW ══════════════════════════════════════════════════════

        composable(Routes.GESTURE_DETAIL) { backStack ->
            val gestureId = backStack.arguments?.getString("gestureId") ?: ""
            GestureDetailScreen(
                gestureId      = gestureId,
                viewModel      = gesturesViewModel,
                onBack         = { navController.popBackStack() },
                onMore         = { /* TODO: options bottom sheet */ },
                onChangeAction = {
                    navController.navigate(Routes.configureGesture(gestureId))
                },
                onTest         = { /* Sin endpoint de backend todavía: botón deshabilitado en UI */ },
                onDelete       = {
                    navController.navigate(Routes.GESTURES) {
                        popUpTo(Routes.GESTURES) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.CONFIGURE_GESTURE) { backStack ->
            val gestureId = backStack.arguments?.getString("gestureId") ?: ""
            val userId = session.getUserId() ?: ""
            ConfigureGestureScreen(
                gestureId = gestureId,
                userId    = userId,
                viewModel = gesturesViewModel,
                onBack    = { navController.popBackStack() },
                onSaved   = {
                    navController.navigate(Routes.gestureSaved(gestureId)) {
                        popUpTo(Routes.CONFIGURE_GESTURE) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.GESTURE_SAVED) {
            GestureSavedScreen(
                viewModel = gesturesViewModel,
                onTest = { /* Sin endpoint de backend todavía: botón deshabilitado en UI */ },
                onBack = {
                    navController.navigate(Routes.GESTURES) {
                        popUpTo(Routes.GESTURES) { inclusive = false }
                    }
                }
            )
        }

        // ══ PROFILE FLOW ══════════════════════════════════════════════════════

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSave = { _: String, _: String ->
                    navController.popBackStack()
                },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ══ SUGGESTIONS FLOW ══════════════════════════════════════════════════

        composable(Routes.ROUTINE_DETAIL) { backStack ->
            val suggestionId = backStack.arguments?.getString("routineId") ?: ""
            val userId = session.getUserId() ?: ""
            RoutineDetailScreen(
                suggestionId = suggestionId,
                userId       = userId,
                viewModel    = suggestionsViewModel,
                onBack       = { navController.popBackStack() },
                onShare      = { /* TODO: share intent */ },
                onDiscard    = {
                    suggestionsViewModel.discardSelected()
                    navController.popBackStack()
                },
                onActivated  = { routineId ->
                    navController.navigate(Routes.routineActivated(routineId)) {
                        popUpTo(Routes.ROUTINE_DETAIL) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ROUTINE_ACTIVATED) {
            RoutineActivatedScreen(
                viewModel = suggestionsViewModel,
                onViewRoutines  = {
                    suggestionsViewModel.clearActivation()
                    navController.navigate(Routes.SUGGESTIONS) {
                        popUpTo(Routes.SUGGESTIONS) { inclusive = false }
                    }
                },
                onKeepExploring = {
                    suggestionsViewModel.clearActivation()
                    navController.navigate(Routes.SUGGESTIONS) {
                        popUpTo(Routes.SUGGESTIONS) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.BLUETOOTH_SCAN) {
            val userId = session.getUserId() ?: ""
            com.emilionavarro.prueba.dispositivos.bluetooth.BluetoothScanScreen(
                userId = userId,
                onBack = { navController.popBackStack() },
                onLinked = {
                    navController.navigate(Routes.DEVICE_SUCCESS.replace("{deviceId}", "new")) {
                        popUpTo(Routes.BLUETOOTH_SCAN) { inclusive = true }
                    }
                }
            )
        }
    }
}