package com.emilionavarro.prueba.senas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModel

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background      = Color(0xFFEAE7E0)
private val Surface         = Color(0xFFF2EFEA)
private val OnBackground    = Color(0xFF1C1C1C)
private val Subtle          = Color(0xFF7A7A7A)
private val Accent          = Color(0xFF232320)
private val BorderColor     = Color(0xFFDDDAD3)
private val CardSelected    = Color(0xFFE8E5DE)
private val BorderSelected  = Color(0xFF232320)
private val ErrorColor      = Color(0xFFB3413B)

// ── Screen (con estado / conectada al backend) ─────────────────────────────────
@Composable
fun ConfigureGestureScreen(
    gestureId: String,
    userId: String,
    viewModel: GesturesViewModel,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
) {
    val state = viewModel.uiState
    val isNew = gestureId == "new"

    LaunchedEffect(gestureId) {
        if (isNew) viewModel.startNewGesture()
        else if (state.selectedGesture?.id != gestureId) viewModel.selectGesture(gestureId)
    }

    var devices by remember { mutableStateOf<List<DeviceResponse>>(emptyList()) }
    var devicesError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            when (val result = DeviceRepository().getDevices(userId)) {
                is ApiResult.Success -> devices = result.data
                is ApiResult.Error -> devicesError = result.message
            }
        }
    }

    LaunchedEffect(state.savedGesture) {
        if (state.savedGesture != null) onSaved()
    }

    ConfigureGestureScreenContent(
        gestureName = state.selectedGesture?.name ?: "",
        availableDevices = devices,
        selectedDeviceId = state.selectedGesture?.linkedDeviceId,
        actionText = state.selectedGesture?.linkedAction ?: "",
        isSaving = state.isSaving,
        saveError = state.saveError,
        devicesError = devicesError,
        onBack = onBack,
        onSave = { name, deviceId, action ->
            viewModel.saveGesture(userId = userId, name = name, linkedDeviceId = deviceId, linkedAction = action)
        }
    )
}

// ── Screen (sin estado / puramente visual, usada por el Preview) ──────────────
@Composable
fun ConfigureGestureScreenContent(
    gestureName: String = "",
    availableDevices: List<DeviceResponse> = emptyList(),
    selectedDeviceId: String? = null,
    actionText: String = "",
    isSaving: Boolean = false,
    saveError: String? = null,
    devicesError: String? = null,
    onBack: () -> Unit = {},
    onSave: (name: String, deviceId: String?, action: String) -> Unit = { _, _, _ -> },
) {
    var name by remember(gestureName) { mutableStateOf(gestureName) }
    var deviceId by remember(selectedDeviceId) { mutableStateOf(selectedDeviceId) }
    var action by remember(actionText) { mutableStateOf(actionText) }

    val canSave = name.isNotBlank() && !isSaving

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 100.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ChevronLeft, "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "¿Qué hace esta seña?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            }

            Spacer(Modifier.height(22.dp))

            // ── Nombre ────────────────────────────────────────────────────
            SectionLabel("NOMBRE DE LA SEÑA")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej. Mano abierta", color = Subtle) },
                leadingIcon = { Icon(Icons.Outlined.PanTool, contentDescription = null, tint = Subtle) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = Surface,
                    unfocusedContainerColor  = Surface,
                    focusedBorderColor       = OnBackground,
                    unfocusedBorderColor     = BorderColor,
                    focusedTextColor         = OnBackground,
                    unfocusedTextColor       = OnBackground,
                    cursorColor              = OnBackground
                )
            )

            Spacer(Modifier.height(22.dp))

            // ── Selector de dispositivo (reemplaza la grilla de tipos mock) ──
            SectionLabel("DISPOSITIVO A CONTROLAR")
            Spacer(Modifier.height(10.dp))

            when {
                devicesError != null -> {
                    Text("No se pudieron cargar tus dispositivos: $devicesError", fontSize = 12.sp, color = ErrorColor)
                }
                availableDevices.isEmpty() -> {
                    Text("No tienes dispositivos vinculados todavía.", fontSize = 12.sp, color = Subtle)
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Surface)
                    ) {
                        availableDevices.forEachIndexed { index, device ->
                            val isSelected = device.id == deviceId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { deviceId = device.id }
                                    .background(if (isSelected) CardSelected else Color.Transparent)
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        device.displayName ?: device.deviceType ?: "Dispositivo",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = OnBackground
                                    )
                                    Text(
                                        device.room ?: "Sin cuarto",
                                        fontSize = 12.sp,
                                        color = Subtle
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Outlined.Check, null, tint = Accent, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (index < availableDevices.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = BorderColor, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // ── Acción ────────────────────────────────────────────────────
            SectionLabel("ACCIÓN")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej. Encender, Apagar, Reproducir música", color = Subtle) },
                leadingIcon = { Icon(Icons.Outlined.SettingsRemote, contentDescription = null, tint = Subtle) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = Surface,
                    unfocusedContainerColor  = Surface,
                    focusedBorderColor       = OnBackground,
                    unfocusedBorderColor     = BorderColor,
                    focusedTextColor         = OnBackground,
                    unfocusedTextColor       = OnBackground,
                    cursorColor              = OnBackground
                )
            )

            if (saveError != null) {
                Spacer(Modifier.height(14.dp))
                Text(saveError, fontSize = 12.sp, color = ErrorColor)
            }
        }

        // ── Bottom CTA ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { onSave(name, deviceId, action) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = canSave
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        "Guardar seña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = Subtle
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConfigureGestureScreenPreview() {
    ConfigureGestureScreenContent(gestureName = "Mano abierta")
}