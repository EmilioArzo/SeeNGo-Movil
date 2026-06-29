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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background    = Color(0xFFEAE7E0)
private val Surface       = Color(0xFFF2EFEA)
private val SurfaceCard   = Color(0xFFEFECE5)
private val OnBackground  = Color(0xFF1C1C1C)
private val Subtle        = Color(0xFF7A7A7A)
private val Accent        = Color(0xFF232320)
private val BorderColor   = Color(0xFFDDDAD3)
private val ToggleOn      = Color(0xFF232320)
private val ToggleOff     = Color(0xFFCBC8C0)
private val IconBg        = Color(0xFFE4E1D9)
private val NavSelected   = Color(0xFFE4E1D9)

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
    onAddDevice: () -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
) {
    val rooms = remember {
        mutableStateListOf(
            RoomSection(
                "Sala", 3, 1.2,
                listOf(
                    DeviceItem("Lámpara de pie",    "Shelly Plug · 12W",          Icons.Outlined.LightMode,    true),
                    DeviceItem("TV Samsung",         "Shelly Plug · 0W",           Icons.Outlined.Tv,           false),
                    DeviceItem("Spotify · Echo",     "API Spotify · Lo-fi para trabajar", Icons.Outlined.GraphicEq, true),
                )
            ),
            RoomSection(
                "Habitación", 2, 0.8,
                listOf(
                    DeviceItem("Aire acondicionado", "Shelly 1PM · 0W",            Icons.Outlined.AcUnit,       false),
                    DeviceItem("Lámpara mesa",       "Shelly Plug · 8W",           Icons.Outlined.Tungsten,     true),
                )
            )
        )
    }

    // Track toggle states independently
    val toggleStates = remember {
        mutableStateMapOf<String, Boolean>().also { map ->
            rooms.forEach { room ->
                room.devices.forEach { device ->
                    map["${room.name}:${device.name}"] = device.isOn
                }
            }
        }
    }

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
                    Text(
                        "Dispositivos",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "5 conectados · 2 cuartos",
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

            Spacer(Modifier.height(20.dp))

            // ── Live consumption card ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(IconBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.FlashOn,
                            contentDescription = null,
                            tint = OnBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "CONSUMO EN VIVO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Subtle
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "340",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnBackground
                            )
                            Text(
                                " W",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = OnBackground,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                "  · 3.4 kWh hoy",
                                fontSize = 13.sp,
                                color = Subtle,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = Subtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Room sections ─────────────────────────────────────────────
            rooms.forEachIndexed { roomIndex, room ->
                RoomHeader(room)
                Spacer(Modifier.height(10.dp))
                DeviceCard(
                    devices = room.devices,
                    toggleStates = toggleStates,
                    roomKey = room.name
                )
                Spacer(Modifier.height(24.dp))
            }
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
        Text(
            room.name,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnBackground
        )
        Text(
            "${room.deviceCount} disp · ${room.kwh} kWh",
            fontSize = 12.sp,
            color = Subtle
        )
    }
}

// ── Device card (grouped) ─────────────────────────────────────────────────────
@Composable
private fun DeviceCard(
    devices: List<DeviceItem>,
    toggleStates: MutableMap<String, Boolean>,
    roomKey: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
    ) {
        devices.forEachIndexed { index, device ->
            val key = "$roomKey:${device.name}"
            val isOn = toggleStates[key] ?: device.isOn

            DeviceRow(
                device = device,
                isOn = isOn,
                onToggle = { toggleStates[key] = it }
            )

            if (index < devices.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

// ── Single device row ─────────────────────────────────────────────────────────
@Composable
private fun DeviceRow(
    device: DeviceItem,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(IconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                device.icon,
                contentDescription = null,
                tint = OnBackground,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                device.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                device.subtitle,
                fontSize = 12.sp,
                color = Subtle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = ToggleOn,
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
        Triple("Inicio",        Icons.Outlined.Home,          onNavInicio),
        Triple("Señas",         Icons.Outlined.PanTool,       onNavSenas),
        Triple("Dispositivos",  Icons.Outlined.LightbulbCircle, onNavDispositivos),
        Triple("Sugerencias",   Icons.Outlined.AutoAwesome,   onNavSugerencias),
        Triple("Perfil",        Icons.Outlined.Person,        onNavPerfil),
    )

    Surface(
        color = Background,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = if (isSelected) OnBackground else Subtle,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (isSelected) OnBackground else Subtle,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DevicesScreenPreview() {
    DevicesScreen()
}