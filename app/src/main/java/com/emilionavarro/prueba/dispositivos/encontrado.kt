package com.emilionavarro.prueba.dispositivos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background = Color(0xFFEAE7E0)
private val Surface = Color(0xFFF2EFEA)
private val SurfaceSelected = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle = Color(0xFF7A7A7A)
private val Accent = Color(0xFF232320)
private val BorderColor = Color(0xFFDDDAD3)
private val BorderSelected = Color(0xFF232320)
private val ProgressActive = Color(0xFF232320)
private val ProgressInact = Color(0xFFD4D0C8)
private val IconBg = Color(0xFFE4E1D9)
private val CheckBg = Color(0xFF232320)
private val PrivacyBg = Color(0xFFE8E5DE)

// ── Data ──────────────────────────────────────────────────────────────────────
data class ScannedDevice(
    val id: String,
    val name: String,
    val model: String,
    val ip: String
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun FoundDevicesScreen(
    userId: String,
    currentStep: Int = 2,
    totalSteps: Int = 3,
    onBack: () -> Unit = {},
    onLinked: () -> Unit = {}, // se llama cuando la vinculación termina bien
    viewModel: FoundDevicesViewModel = viewModel(factory = viewModelFactory { FoundDevicesViewModel(userId = userId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedIds by remember(uiState.devices) {
        mutableStateOf(setOf(uiState.devices.firstOrNull()?.id ?: ""))
    }
    val selectedDevices = uiState.devices.filter { it.id in selectedIds }

    // Navegar de vuelta cuando la vinculación fue exitosa
    LaunchedEffect(uiState.linkSuccess) {
        if (uiState.linkSuccess) onLinked()
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 90.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Surface).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Dispositivos encontrados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                    Text("Paso $currentStep de $totalSteps · Selecciona", fontSize = 12.sp, color = Subtle)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Step progress bars ────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier.weight(1f).height(3.dp).clip(RoundedCornerShape(2.dp))
                            .background(if (index < currentStep) ProgressActive else ProgressInact)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = Color(0xFFA33C3C), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
            }

            // ── Device list / estado de escaneo ─────────────────────────
            when {
                uiState.isScanning -> {
                    Box(Modifier.fillMaxWidth().padding(vertical = 30.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Accent)
                            Spacer(Modifier.height(12.dp))
                            Text("Buscando dispositivos en tu red...", fontSize = 13.sp, color = Subtle)
                        }
                    }
                }
                uiState.devices.isEmpty() -> {
                    Text("No se encontraron dispositivos nuevos.", fontSize = 13.sp, color = Subtle)
                }
                else -> {
                    uiState.devices.forEach { device ->
                        val isSelected = device.id in selectedIds
                        DeviceSelectRow(
                            device = device,
                            isSelected = isSelected,
                            onClick = {
                                selectedIds = if (isSelected) selectedIds - device.id else selectedIds + device.id
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Rescan button ─────────────────────────────────────────────
            OutlinedButton(
                onClick = { viewModel.rescan() },
                enabled = !uiState.isScanning,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface, contentColor = OnBackground)
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Volver a escanear", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(14.dp))

            // ── Privacy notice ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PrivacyBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Subtle, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = OnBackground)) { append("Privado: ") }
                        withStyle(SpanStyle(color = Subtle)) { append("Los dispositivos viven en tu red local. No salen al internet.") }
                    },
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // ── Bottom CTA ────────────────────────────────────────────────────
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { viewModel.linkDevices(selectedDevices) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = selectedIds.isNotEmpty() && !uiState.isLinking
            ) {
                if (uiState.isLinking) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    val count = selectedIds.size
                    Text("Vincular $count dispositivo${if (count != 1) "s" else ""}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

// ── Device selection row ──────────────────────────────────────────────────────
@Composable
private fun DeviceSelectRow(device: ScannedDevice, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SurfaceSelected else Surface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) BorderSelected else BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).background(IconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Memory, contentDescription = null, tint = OnBackground, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
            Text("${device.model} · ${device.ip}", fontSize = 12.sp, color = Subtle)
        }
        Spacer(Modifier.width(10.dp))
        if (isSelected) {
            Box(modifier = Modifier.size(28.dp).background(CheckBg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Check, contentDescription = "Seleccionado", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        } else {
            Box(modifier = Modifier.size(28.dp).border(1.5.dp, BorderColor, CircleShape))
        }
    }
}