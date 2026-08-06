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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.dispositivos.data.isWifiConnected

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background = Color(0xFFEAE7E0)
private val Surface = Color(0xFFF2EFEA)
private val SurfaceCard = Color(0xFFEFECE5)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle = Color(0xFF7A7A7A)
private val Accent = Color(0xFF232320)
private val BorderColor = Color(0xFFDDDAD3)
private val IconBg = Color(0xFF3A3F3A)
private val ToggleOn = Color(0xFF232320)
private val ToggleOff = Color(0xFFCBC8C0)

// ── Data models (para gestos, que no vienen del backend de dispositivos) ─────
data class LinkedGesture(val label: String, val action: String)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DeviceDetailScreen(
    deviceId: String,
    gestures: List<LinkedGesture> = listOf(
        LinkedGesture("Puño cerrado", "→ Apagar"),
        LinkedGesture("OK", "→ Encender"),
    ),
    onBack: () -> Unit = {},
    onDeleted: () -> Unit = {}, // navega hacia atrás cuando se elimina el dispositivo
    onEditGestures: () -> Unit = {},
    onGestureClick: (LinkedGesture) -> Unit = {},
    viewModel: DeviceDetailViewModel = viewModel(factory = viewModelFactory { DeviceDetailViewModel(deviceId = deviceId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.wasDeleted) {
        if (uiState.wasDeleted) onDeleted()
    }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    if (uiState.errorMessage != null && uiState.device == null) {
        Box(Modifier.fillMaxSize().background(Background).padding(20.dp), contentAlignment = Alignment.Center) {
            Text("No se pudo cargar el dispositivo: ${uiState.errorMessage}", color = Subtle, fontSize = 14.sp)
        }
        return
    }

    val device = uiState.device ?: return
    val deviceName = device.displayName ?: device.deviceType ?: "Dispositivo"
    val roomAndPlug = "${device.room ?: "Sin cuarto"} · ${device.deviceType ?: "Shelly"}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Surface).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deviceName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                Text(roomAndPlug, fontSize = 12.sp, color = Subtle)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Accent).clickable { showOptionsMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.MoreHoriz, contentDescription = "Más opciones", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            DropdownMenu(expanded = showOptionsMenu, onDismissRequest = { showOptionsMenu = false }) {
                DropdownMenuItem(text = { Text("Renombrar / mover de cuarto") }, onClick = {
                    showOptionsMenu = false
                    showRenameDialog = true
                })
                DropdownMenuItem(text = { Text("Eliminar dispositivo") }, onClick = {
                    showOptionsMenu = false
                    viewModel.deleteDevice()
                })
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Status card ───────────────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(SurfaceCard).padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(52.dp).background(IconBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.LightMode, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Switch(
                        checked = uiState.isOn,
                        onCheckedChange = { viewModel.setOn(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ToggleOn,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = ToggleOff,
                            uncheckedBorderColor = ToggleOff
                        )
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = OnBackground)) {
                            append(if (uiState.isOn) "—" else "0")
                        }
                        withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = OnBackground)) {
                            append(" W ahora")
                        }
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when {
                        !device.isOnline -> "Dispositivo desconectado"
                        device.isWifiConnected() -> "Conectado por WiFi"
                        else -> "Conectado por Bluetooth"
                    },
                    fontSize = 13.sp,
                    color = Subtle
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Linked gestures ───────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("SEÑAS VINCULADAS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
            Text("Editar", fontSize = 13.sp, color = OnBackground, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onEditGestures() })
        }
        Spacer(Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            gestures.forEachIndexed { index, gesture ->
                GestureRow(gesture = gesture, onClick = { onGestureClick(gesture) })
                if (index < gestures.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor, thickness = 0.5.dp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Info section (datos reales del dispositivo) ─────────────────
        Text("INFORMACIÓN", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
        Spacer(Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
            val info = listOfNotNull(
                if (device.isWifiConnected()) device.localIp?.let { "IP local" to it } else "Conexión" to "Bluetooth",
                device.macAddress?.let { "MAC" to it },
                device.deviceType?.let { "Tipo" to it },
                device.createdAt?.let { "Vinculado" to it }
            )
            info.forEachIndexed { index, (key, value) ->
                InfoRow(key = key, value = value)
                if (index < info.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor, thickness = 0.5.dp)
                }
            }
        }
    }

    if (showRenameDialog) {
        RenameDeviceDialog(
            initialName = deviceName,
            initialRoom = device.room ?: "",
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName, newRoom ->
                showRenameDialog = false
                viewModel.updateDevice(newName, newRoom, device.icon ?: "lightbulb")
            }
        )
    }
}

// ── Diálogo simple para renombrar / mover de cuarto (usa PUT /api/devices/{id}) ─
@Composable
private fun RenameDeviceDialog(
    initialName: String,
    initialRoom: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var room by remember { mutableStateOf(initialRoom) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar dispositivo") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Cuarto") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, room) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ── Gesture row ───────────────────────────────────────────────────────────────
@Composable
private fun GestureRow(gesture: LinkedGesture, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(42.dp).background(Color(0xFF2E332E), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("GIF", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(gesture.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            Text(gesture.action, fontSize = 12.sp, color = Subtle)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Subtle, modifier = Modifier.size(18.dp))
    }
}

// ── Info row ──────────────────────────────────────────────────────────────────
@Composable
private fun InfoRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, fontSize = 14.sp, color = Subtle)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
    }
}