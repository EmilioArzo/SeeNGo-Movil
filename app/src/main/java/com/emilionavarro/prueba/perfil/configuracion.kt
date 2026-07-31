package com.emilionavarro.prueba.perfil


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.perfil.data.AppPreferences
import com.emilionavarro.prueba.perfil.viewmodel.SettingsViewModel
import com.emilionavarro.prueba.perfil.viewmodel.SettingsViewModelFactory

private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val BorderColor  = Color(0xFFDDDAD3)
private val IconBg       = Color(0xFFE4E1D9)
private val ToggleOn     = Color(0xFF232320)
private val ToggleOff    = Color(0xFFCBC8C0)

@Composable
fun SettingsScreen(
    onBack: () -> Unit           = {},
    onNavPreferences: () -> Unit = {},
) {
    val context = LocalContext.current
    val prefs   = remember { AppPreferences(context) }
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(prefs))
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Surface).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ChevronLeft, "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text("Configuración", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
        }

        Spacer(Modifier.height(24.dp))

        // ══ DETECCIÓN ════════════════════════════════════════════════════════
        SectionLabel("DETECCIÓN")
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            NavRow(Icons.Outlined.CameraAlt, "Cámara del dispositivo", "Vinculada · Sala")
            RowDivider()
            NavRow(Icons.Outlined.TravelExplore, "Sensibilidad", "Media")
            RowDivider()
            ToggleRow(
                icon     = Icons.Outlined.Home,
                label    = "Solo cuando estoy en casa",
                isOn     = state.vibration,            // reutilizamos hasta tener campo propio
                onToggle = { vm.setVibration(it) }
            )
            RowDivider()
            ToggleRow(
                icon     = Icons.Outlined.VolumeOff,
                label    = "Confirmar con sonido",
                isOn     = false,
                onToggle = { }
            )
        }

        Spacer(Modifier.height(20.dp))

        // ══ NOTIFICACIONES ════════════════════════════════════════════════════
        SectionLabel("NOTIFICACIONES")
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            ToggleRow(
                icon     = Icons.Outlined.NotificationsNone,
                label    = "Notificaciones push",
                isOn     = state.pushNotifications,
                onToggle = { vm.setPushNotifications(it) }
            )
            RowDivider()
            NavRow(Icons.Outlined.Email, "Resumen semanal", "Lunes 9 am")
            RowDivider()
            ToggleRow(
                icon     = Icons.Outlined.Warning,
                label    = "Avisos de consumo alto",
                isOn     = state.consumptionAlert,
                onToggle = { vm.setConsumptionAlert(it) }
            )
        }

        Spacer(Modifier.height(20.dp))

        // ══ PRIVACIDAD ════════════════════════════════════════════════════════
        SectionLabel("PRIVACIDAD")
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            ToggleRow(
                icon     = Icons.Outlined.Shield,
                label    = "Procesar señas en el dispositivo",
                isOn     = true,
                onToggle = { }
            )
            RowDivider()
            NavRow(Icons.Outlined.DeleteOutline, "Borrar datos de entrenamiento")
            RowDivider()
            NavRow(Icons.Outlined.Description, "Política de privacidad")
        }

        Spacer(Modifier.height(20.dp))

        // ══ SISTEMA ══════════════════════════════════════════════════════════
        SectionLabel("SISTEMA")
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            // Preferences shortcut — navigates to PreferencesScreen
            NavRow(
                icon     = Icons.Outlined.DarkMode,
                label    = "Tema y preferencias",
                trailing = state.theme,
                onClick  = onNavPreferences
            )
            RowDivider()
            NavRow(Icons.Outlined.Refresh, "Buscar actualizaciones", "v1.4.2")
            RowDivider()
            NavRow(Icons.Outlined.HelpOutline, "Ayuda y soporte")
            RowDivider()
            NavRow(Icons.Outlined.Info, "Acerca de")
        }
    }
}

// ── Shared row components ──────────────────────────────────────────────────────
@Composable
private fun NavRow(
    icon: ImageVector, label: String, trailing: String? = null, onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(34.dp).background(IconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = OnBackground, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = OnBackground, modifier = Modifier.weight(1f))
        if (trailing != null) { Text(trailing, fontSize = 12.sp, color = Subtle); Spacer(Modifier.width(4.dp)) }
        Icon(Icons.Outlined.ChevronRight, null, tint = Subtle, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ToggleRow(icon: ImageVector, label: String, isOn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(34.dp).background(IconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = OnBackground, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = OnBackground, modifier = Modifier.weight(1f))
        Switch(
            checked = isOn, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = ToggleOn,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = ToggleOff, uncheckedBorderColor = ToggleOff
            )
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = BorderColor, thickness = 0.5.dp)
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SettingsScreenPreview() { SettingsScreen() }