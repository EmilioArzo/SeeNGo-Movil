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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val BorderColor  = Color(0xFFDDDAD3)
private val IconBg       = Color(0xFFE4E1D9)
private val ToggleOn     = Color(0xFF232320)
private val ToggleOff    = Color(0xFFCBC8C0)

// ── Row types ─────────────────────────────────────────────────────────────────
sealed class SettingItem {
    data class Toggle(val icon: ImageVector, val label: String, val initialOn: Boolean) : SettingItem()
    data class Nav(val icon: ImageVector, val label: String, val trailing: String? = null, val onClick: () -> Unit = {}) : SettingItem()
}

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
) {
    // Toggle states
    val toggleStates = remember {
        mutableStateMapOf(
            "Solo cuando estoy en casa" to true,
            "Confirmar con sonido"       to false,
            "Notificaciones push"        to true,
            "Avisos de consumo alto"     to true,
            "Procesar señas en el dispositivo" to true,
        )
    }

    val sections = listOf(
        "DETECCIÓN" to listOf(
            SettingItem.Nav(Icons.Outlined.CameraAlt,       "Cámara del dispositivo",  "Vinculada · Sala"),
            SettingItem.Nav(Icons.Outlined.TravelExplore,   "Sensibilidad",            "Media"),
            SettingItem.Toggle(Icons.Outlined.Home,          "Solo cuando estoy en casa", true),
            SettingItem.Toggle(Icons.Outlined.VolumeOff,     "Confirmar con sonido",    false),
        ),
        "NOTIFICACIONES" to listOf(
            SettingItem.Toggle(Icons.Outlined.NotificationsNone, "Notificaciones push",      true),
            SettingItem.Nav(Icons.Outlined.Email,               "Resumen semanal",          "Lunes 9 am"),
            SettingItem.Toggle(Icons.Outlined.Warning,          "Avisos de consumo alto",   true),
        ),
        "PRIVACIDAD" to listOf(
            SettingItem.Toggle(Icons.Outlined.Shield,         "Procesar señas en el dispositivo", true),
            SettingItem.Nav(Icons.Outlined.DeleteOutline,     "Borrar datos de entrenamiento"),
            SettingItem.Nav(Icons.Outlined.Description,       "Política de privacidad"),
        ),
        "SISTEMA" to listOf(
            SettingItem.Nav(Icons.Outlined.Refresh,           "Buscar actualizaciones", "v1.4.2"),
            SettingItem.Nav(Icons.Outlined.HelpOutline,       "Ayuda y soporte"),
            SettingItem.Nav(Icons.Outlined.Info,              "Acerca de"),
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ChevronLeft,
                    contentDescription = "Volver",
                    tint = OnBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Configuración",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Sections ──────────────────────────────────────────────────────
        sections.forEachIndexed { sIndex, (sectionTitle, items) ->
            Text(
                sectionTitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Subtle
            )
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
            ) {
                items.forEachIndexed { index, item ->
                    when (item) {
                        is SettingItem.Toggle -> {
                            val isOn = toggleStates[item.label] ?: item.initialOn
                            ToggleRow(
                                icon    = item.icon,
                                label   = item.label,
                                isOn    = isOn,
                                onToggle = { toggleStates[item.label] = it }
                            )
                        }
                        is SettingItem.Nav -> {
                            NavRow(
                                icon     = item.icon,
                                label    = item.label,
                                trailing = item.trailing,
                                onClick  = item.onClick
                            )
                        }
                    }
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = BorderColor,
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            if (sIndex < sections.lastIndex) Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Toggle row ────────────────────────────────────────────────────────────────
@Composable
private fun ToggleRow(
    icon: ImageVector,
    label: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(icon)
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = OnBackground,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor    = Color.White,
                checkedTrackColor    = ToggleOn,
                uncheckedThumbColor  = Color.White,
                uncheckedTrackColor  = ToggleOff,
                uncheckedBorderColor = ToggleOff
            )
        )
    }
}

// ── Nav row ───────────────────────────────────────────────────────────────────
@Composable
private fun NavRow(
    icon: ImageVector,
    label: String,
    trailing: String? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(icon)
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = OnBackground,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(trailing, fontSize = 12.sp, color = Subtle)
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Subtle,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Icon box ──────────────────────────────────────────────────────────────────
@Composable
private fun RowIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(IconBg, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = OnBackground, modifier = Modifier.size(17.dp))
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
