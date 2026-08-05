package com.emilionavarro.seengo.inicio

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.dispositivos.viewModelFactory
import com.emilionavarro.prueba.inicio.HomeScreenSkeleton
import com.emilionavarro.prueba.inicio.viewmodel.DashboardViewModel
import com.emilionavarro.prueba.inicio.viewmodel.QuickDeviceUi
import com.emilionavarro.prueba.inicio.viewmodel.RoutineUi
import com.emilionavarro.prueba.inicio.viewmodel.SuggestionUi

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val ToggleOn     = Color(0xFF232320)
private val ToggleOff    = Color(0xFFCBC8C0)
private val IconBg       = Color(0xFFE4E1D9)
private val NavSelected  = Color(0xFFE4E1D9)
private val ErrorRed     = Color(0xFFD94F3D)
private val BadgeRed     = Color(0xFFD94F3D)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    userId: String,
    userName: String = "",
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onVerTodos: () -> Unit = {},
    onRoutineClick: (String) -> Unit = {},
    viewModel: DashboardViewModel = viewModel(factory = viewModelFactory { DashboardViewModel(userId = userId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        HomeScreenSkeleton()
        return
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            HomeBottomNavBar(
                selected = "Inicio",
                onNavInicio       = onNavInicio,
                onNavSenas        = onNavSenas,
                onNavDispositivos = onNavDispositivos,
                onNavSugerencias  = onNavSugerencias,
                onNavPerfil       = onNavPerfil,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 16.dp)
        ) {
            // ── Greeting + bell con badge de sugerencias sin ver ────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    if (userName.isNotBlank()) {
                        Text("Buenos días, $userName", fontSize = 13.sp, color = Subtle)
                    }
                    Text(
                        "Tu casa,\nen una seña.",
                        fontSize = 28.sp, fontWeight = FontWeight.Bold,
                        color = OnBackground, lineHeight = 34.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .clickable { onNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.NotificationsNone, "Notificaciones", tint = OnBackground, modifier = Modifier.size(20.dp))
                    if (uiState.suggestions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(RoundedCornerShape(50))
                                .background(BadgeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${uiState.suggestions.size}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(ErrorRed.copy(alpha = 0.1f)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(uiState.errorMessage!!, fontSize = 12.sp, color = ErrorRed, modifier = Modifier.weight(1f))
                    Text(
                        "Reintentar", fontSize = 12.sp, color = ErrorRed, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.loadDashboard() }
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Resumen del hogar (reemplaza el kWh estático) ───────────────
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SurfaceCard).padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("AHORA", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${uiState.devicesOn} de ${uiState.totalDevices} encendidos",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnBackground
                    )
                    Text("${uiState.devicesOnline} en línea", fontSize = 12.sp, color = Subtle)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RUTINAS ACTIVAS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
                    Spacer(Modifier.height(4.dp))
                    Text("${uiState.routines.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Controles rápidos (dispositivos reales) ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CONTROLES RÁPIDOS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
                Text("Ver todos", fontSize = 13.sp, color = OnBackground, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onVerTodos() })
            }

            Spacer(Modifier.height(10.dp))

            if (uiState.quickDevices.isEmpty()) {
                Text("Aún no tienes dispositivos vinculados.", fontSize = 13.sp, color = Subtle)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.quickDevices.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { device ->
                                QuickDeviceCard(
                                    device   = device,
                                    onToggle = { viewModel.toggleDevice(device.id, it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Rutinas activas (reales, desde /api/client/dashboard) ───────
            Text("RUTINAS ACTIVAS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
            Spacer(Modifier.height(10.dp))

            if (uiState.routines.isEmpty()) {
                Text("No tienes rutinas activas todavía.", fontSize = 13.sp, color = Subtle)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)
                ) {
                    uiState.routines.forEachIndexed { index, routine ->
                        RoutineRow(
                            routine  = routine,
                            onClick  = { onRoutineClick(routine.id) },
                            onToggleOff = { viewModel.deactivateRoutine(routine.id) }
                        )
                        if (index < uiState.routines.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // ── Sugerencias sin ver ──────────────────────────────────────────
            if (uiState.suggestions.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SUGERENCIAS PARA TI", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
                    Text("Ver todas", fontSize = 13.sp, color = OnBackground, fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavSugerencias() })
                }
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.suggestions.take(3).forEach { suggestion ->
                        SuggestionRow(
                            suggestion = suggestion,
                            onDismiss  = { viewModel.dismissSuggestion(suggestion.id) },
                            onClick    = { onNavSugerencias() }
                        )
                    }
                }
            }
        }
    }
}

// ── Quick device card ─────────────────────────────────────────────────────────
@Composable
private fun QuickDeviceCard(device: QuickDeviceUi, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Surface).padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(IconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(device.icon, contentDescription = null, tint = OnBackground, modifier = Modifier.size(18.dp))
            }
            Switch(
                checked = device.isOn, onCheckedChange = onToggle,
                modifier = Modifier.size(width = 44.dp, height = 26.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White, checkedTrackColor = ToggleOn,
                    uncheckedThumbColor = Color.White, uncheckedTrackColor = ToggleOff, uncheckedBorderColor = ToggleOff
                )
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(device.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnBackground, maxLines = 1)
        Text(device.subtitle, fontSize = 11.sp, color = Subtle, maxLines = 1)
    }
}

// ── Routine row ────────────────────────────────────────────────────────────────
@Composable
private fun RoutineRow(routine: RoutineUi, onClick: () -> Unit, onToggleOff: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(38.dp).background(IconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.FlashOn, contentDescription = null, tint = OnBackground, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(routine.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            Text("${routine.triggerType} · ${routine.triggerValue}", fontSize = 12.sp, color = Subtle)
        }
        Switch(
            checked = true, onCheckedChange = { if (!it) onToggleOff() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ToggleOn)
        )
    }
}

// ── Suggestion row ─────────────────────────────────────────────────────────────
@Composable
private fun SuggestionRow(suggestion: SuggestionUi, onDismiss: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceCard)
            .clickable { onClick() }.padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Subtle, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(suggestion.text, fontSize = 13.sp, color = OnBackground, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("↓ ~${suggestion.kwhSaving} kWh · ${suggestion.cluster}", fontSize = 11.sp, color = Subtle)
        }
        Text(
            "Descartar", fontSize = 12.sp, color = Subtle,
            modifier = Modifier.clickable { onDismiss() }
        )
    }
}

// ── Bottom nav bar ────────────────────────────────────────────────────────────
@Composable
private fun HomeBottomNavBar(
    selected: String,
    onNavInicio: () -> Unit, onNavSenas: () -> Unit,
    onNavDispositivos: () -> Unit, onNavSugerencias: () -> Unit, onNavPerfil: () -> Unit,
) {
    val items = listOf(
        Triple("Inicio",       Icons.Outlined.Home,             onNavInicio),
        Triple("Señas",        Icons.Outlined.PanTool,          onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle,  onNavDispositivos),
        Triple("Sugerencias",  Icons.Outlined.AutoAwesome,      onNavSugerencias),
        Triple("Perfil",       Icons.Outlined.Person,           onNavPerfil),
    )
    Surface(color = Background, tonalElevation = 0.dp) {
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            items.forEach { (label, icon, action) ->
                val isSelected = label == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { action() }
                        .background(if (isSelected) NavSelected else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(icon, contentDescription = label, tint = if (isSelected) OnBackground else Subtle, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, fontSize = 10.sp, color = if (isSelected) OnBackground else Subtle,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}