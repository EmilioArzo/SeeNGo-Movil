package com.emilionavarro.prueba.dispositivos

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background = Color(0xFFEAE7E0)
private val Surface = Color(0xFFF2EFEA)
private val SurfaceCard = Color(0xFFEFECE5)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle = Color(0xFF7A7A7A)
private val Accent = Color(0xFF232320)
private val BorderColor = Color(0xFFDDDAD3)
private val ToggleOn = Color(0xFF232320)
private val ToggleOff = Color(0xFFCBC8C0)
private val IconBg = Color(0xFFE4E1D9)
private val NavSelected = Color(0xFFE4E1D9)
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyLinkedBg = Color(0xFFD6EAD8)
private val ErrorRed = Color(0xFFD94F3D)

// ── Data models ───────────────────────────────────────────────────────────────
data class DeviceItem(
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val isOn: Boolean
)

data class RoomSection(
    val name: String,
    val deviceCount: Int,
    val kwh: Double,
    val devices: List<DeviceItem>
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DevicesScreen(
    userId: String,
    onAddDevice: () -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
    onDeviceClick: (String) -> Unit = {},
    viewModel: DevicesViewModel = run {
        val context = LocalContext.current
        val session = remember { SessionManager(context) }
        viewModel(factory = viewModelFactory { DevicesViewModel(userId = userId, session = session) })
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BottomNavBar(
                selected = "Dispositivos",
                onNavInicio = onNavInicio,
                onNavSenas = onNavSenas,
                onNavDispositivos = {},
                onNavSugerencias = onNavSugerencias,
                onNavPerfil = onNavPerfil
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Dispositivos", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                    val totalDevices = uiState.rooms.sumOf { it.deviceCount }
                    Text(
                        "$totalDevices conectados · ${uiState.rooms.size} cuartos",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Accent, RoundedCornerShape(14.dp))
                        .clickable { onAddDevice() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Agregar", tint = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Vincular Spotify ──────────────────────────────────────────
            SpotifyLinkCard(
                isLinked = uiState.isSpotifyLinked,
                isLinking = uiState.isLinkingSpotify,
                errorMessage = uiState.spotifyError,
                onLinkClick = { viewModel.startSpotifyLink(context) }
            )

            Spacer(Modifier.height(20.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Accent)
                    }
                }
                uiState.errorMessage != null -> {
                    Text(
                        "No se pudieron cargar tus dispositivos: ${uiState.errorMessage}",
                        color = Subtle,
                        fontSize = 13.sp
                    )
                }
                uiState.rooms.isEmpty() -> {
                    Text("Aún no tienes dispositivos vinculados.", color = Subtle, fontSize = 13.sp)
                }
                else -> {
                    uiState.rooms.forEach { room ->
                        RoomHeader(room)
                        Spacer(Modifier.height(10.dp))
                        DeviceCard(
                            devices = room.devices,
                            onToggle = { device, isOn -> viewModel.toggleDevice(room.name, device, isOn) }
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// ── Spotify link card ─────────────────────────────────────────────────────────
@Composable
private fun SpotifyLinkCard(
    isLinked: Boolean,
    isLinking: Boolean,
    errorMessage: String?,
    onLinkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isLinked) SpotifyLinkedBg else Surface)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SpotifyGreen.copy(alpha = if (isLinked) 1f else 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.GraphicEq,
                    contentDescription = null,
                    tint = if (isLinked) Color.White else SpotifyGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isLinked) "Spotify vinculado" else "Conecta tu Spotify",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground
                )
                Text(
                    if (isLinked) "Ya puedes controlar tu música con señas" else "Vincula tu cuenta para reproducir música con gestos",
                    fontSize = 12.sp, color = Subtle
                )
            }
            when {
                isLinking -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Accent)
                isLinked -> Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF4A8C62), modifier = Modifier.size(20.dp))
                else -> Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Accent)
                        .clickable { onLinkClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Vincular", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
        if (errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(errorMessage, fontSize = 12.sp, color = ErrorRed)
        }
    }
}

// ── Room header ───────────────────────────────────────────────────────────────
@Composable
private fun RoomHeader(room: RoomSection) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(room.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
        Text("${room.deviceCount} disp · ${room.kwh} kWh", fontSize = 12.sp, color = Subtle)
    }
}

// ── Device card (grouped) ─────────────────────────────────────────────────────
@Composable
private fun DeviceCard(
    devices: List<DeviceItem>,
    onToggle: (DeviceItem, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
    ) {
        devices.forEachIndexed { index, device ->
            DeviceRow(device = device, onToggle = { onToggle(device, it) })
            if (index < devices.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor, thickness = 0.5.dp)
            }
        }
    }
}

// ── Single device row ─────────────────────────────────────────────────────────
@Composable
private fun DeviceRow(device: DeviceItem, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(IconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(device.icon, contentDescription = null, tint = OnBackground, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(device.subtitle, fontSize = 12.sp, color = Subtle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = device.isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ToggleOn,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = ToggleOff,
                uncheckedBorderColor = ToggleOff
            )
        )
    }
}

// ── Bottom navigation bar ─────────────────────────────────────────────────────
@Composable
private fun BottomNavBar(
    selected: String,
    onNavInicio: () -> Unit,
    onNavSenas: () -> Unit,
    onNavDispositivos: () -> Unit,
    onNavSugerencias: () -> Unit,
    onNavPerfil: () -> Unit,
) {
    val items = listOf(
        Triple("Inicio", Icons.Outlined.Home, onNavInicio),
        Triple("Señas", Icons.Outlined.PanTool, onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle, onNavDispositivos),
        Triple("Sugerencias", Icons.Outlined.AutoAwesome, onNavSugerencias),
        Triple("Perfil", Icons.Outlined.Person, onNavPerfil),
    )
    Surface(color = Background, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { (label, icon, action) ->
                val isSelected = label == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { action() }
                        .background(if (isSelected) NavSelected else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(icon, contentDescription = label, tint = if (isSelected) OnBackground else Subtle, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, fontSize = 10.sp, color = if (isSelected) OnBackground else Subtle, fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}