package com.emilionavarro.prueba.senas

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModel

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val PreviewBg    = Color(0xFFE8E5DE)
private val SpotifyBg    = Color(0xFFD6EED8)
private val DeleteRed    = Color(0xFFB85C38)

// ── Data ──────────────────────────────────────────────────────────────────────
data class GestureDetail(
    val name: String,
    val actionSummary: String,
    val deviceName: String?,
    val createdAt: String?,
)

// ── Screen (con estado / conectada al backend) ─────────────────────────────────
@Composable
fun GestureDetailScreen(
    gestureId: String,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onChangeAction: () -> Unit = {},
    onTest: () -> Unit = {},
    onDelete: () -> Unit = {},
    viewModel: GesturesViewModel,
) {
    val state = viewModel.uiState

    LaunchedEffect(gestureId) {
        if (state.selectedGesture?.id != gestureId) {
            viewModel.selectGesture(gestureId)
        }
    }

    val gesture = state.selectedGesture

    // El backend no manda el nombre del dispositivo vinculado, solo su id.
    // Lo resolvemos aquí reutilizando el DeviceRepository del módulo de dispositivos.
    var deviceName by remember(gesture?.linkedDeviceId) { mutableStateOf<String?>(null) }
    LaunchedEffect(gesture?.linkedDeviceId) {
        val deviceId = gesture?.linkedDeviceId
        if (!deviceId.isNullOrBlank()) {
            when (val result = DeviceRepository().getDeviceById(deviceId)) {
                is ApiResult.Success -> deviceName = result.data.displayName ?: result.data.deviceType
                is ApiResult.Error -> deviceName = null
            }
        } else {
            deviceName = null
        }
    }

    LaunchedEffect(state.wasDeleted) {
        if (state.wasDeleted) onDelete()
    }

    if (state.isLoading && gesture == null) {
        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    if (gesture == null) {
        Box(Modifier.fillMaxSize().background(Background).padding(20.dp), contentAlignment = Alignment.Center) {
            Text("No se encontró esta seña.", color = Subtle, fontSize = 14.sp)
        }
        return
    }

    GestureDetailScreenContent(
        detail = GestureDetail(
            name = gesture.name,
            actionSummary = gesture.linkedAction?.let { "→ $it" } ?: "Sin acción vinculada",
            deviceName = deviceName,
            createdAt = gesture.createdAt
        ),
        isDeleting = state.isDeleting,
        onBack = onBack,
        onMore = onMore,
        onChangeAction = onChangeAction,
        onTest = onTest,
        onDelete = { viewModel.deleteSelected(onDeleted = onDelete) },
    )
}

// ── Screen (sin estado / puramente visual, usada por el Preview) ──────────────
@Composable
fun GestureDetailScreenContent(
    detail: GestureDetail = GestureDetail(
        name = "Mano abierta",
        actionSummary = "→ Reproducir música",
        deviceName = "Bocina Sala",
        createdAt = null
    ),
    isDeleting: Boolean = false,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onChangeAction: () -> Unit = {},
    onTest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val detailRows = listOfNotNull(
        detail.deviceName?.let { "Dispositivo vinculado" to it },
        "Acción" to (if (detail.actionSummary == "Sin acción vinculada") detail.actionSummary else detail.actionSummary.removePrefix("→ ")),
        detail.createdAt?.let { "Creada" to it },
    )

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
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(detail.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Accent)
                        .clickable { onMore() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.MoreHoriz, "Más opciones", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Preview placeholder ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PreviewBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PanTool,
                    contentDescription = null,
                    tint = OnBackground,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Linked action ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ACCIÓN VINCULADA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = Subtle
                )
                Text(
                    "Cambiar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnBackground,
                    modifier = Modifier.clickable { onChangeAction() }
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .clickable { onChangeAction() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpotifyBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.SettingsRemote,
                        contentDescription = null,
                        tint = OnBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(detail.actionSummary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
                    Text(detail.deviceName ?: "Sin dispositivo vinculado", fontSize = 12.sp, color = Subtle)
                }
                Icon(Icons.Outlined.ChevronRight, null, tint = Subtle, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.height(20.dp))

            // ── Details table ─────────────────────────────────────────────
            if (detailRows.isNotEmpty()) {
                Text(
                    "DETALLES",
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
                    detailRows.forEachIndexed { index, (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 14.sp, color = Subtle)
                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
                        }
                        if (index < detailRows.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = BorderColor,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // ── Shortcuts ─────────────────────────────────────────────────
            Text(
                "ATAJOS",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Subtle
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Test button — deshabilitado: el backend aún no expone un endpoint
                // para probar/ejecutar un gesto en vivo (a diferencia de las rutinas).
                OutlinedButton(
                    onClick = onTest,
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface,
                        contentColor   = OnBackground
                    )
                ) {
                    Icon(Icons.Outlined.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Probar", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                // Delete button
                OutlinedButton(
                    onClick = onDelete,
                    enabled = !isDeleting,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface,
                        contentColor   = DeleteRed
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DeleteRed, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.DeleteOutline, null, tint = DeleteRed, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeleteRed)
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GestureDetailScreenPreview() {
    GestureDetailScreenContent()
}