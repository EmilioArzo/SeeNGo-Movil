package com.emilionavarro.prueba.sugerencias

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
import com.emilionavarro.prueba.sugerencias.viewmodel.SuggestionsViewModel
import java.util.Locale

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val ErrorColor   = Color(0xFFB3413B)

// ── Data ──────────────────────────────────────────────────────────────────────
data class RoutineTrigger(val icon: ImageVector, val label: String, val value: String)
data class RoutineStat(val value: String, val unit: String, val description: String)

// ── Screen (con estado / conectada al backend) ─────────────────────────────────
@Composable
fun RoutineDetailScreen(
    suggestionId: String,
    userId: String,
    viewModel: SuggestionsViewModel,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onActivated: (String) -> Unit = {},
) {
    val state = viewModel.uiState

    // Por si se entra directo a esta ruta (deep link / recomposición) sin pasar por la lista.
    LaunchedEffect(suggestionId) {
        if (state.selectedSuggestion?.id != suggestionId) {
            viewModel.selectSuggestion(suggestionId)
        }
    }

    // Cuando el ViewModel confirma que la rutina se creó, navegamos automáticamente.
    LaunchedEffect(state.activatedRoutine) {
        state.activatedRoutine?.id?.let(onActivated)
    }

    val suggestion = state.selectedSuggestion
    val kwh = suggestion?.let { String.format(Locale.getDefault(), "%.1f", it.projectedKwhSaving) }

    RoutineDetailScreenContent(
        title = suggestion?.recommendationText ?: "Sugerencia",
        description = "Sugerencia basada en el análisis de tu consumo reciente.",
        stats = if (suggestion != null) {
            listOf(RoutineStat(value = kwh ?: "0.0", unit = "kWh", description = "Ahorro\nestimado"))
        } else emptyList(),
        triggers = if (suggestion != null) {
            listOf(RoutineTrigger(Icons.Outlined.AutoAwesome, "Categoría detectada", suggestion.assignedCluster))
        } else emptyList(),
        whyReason = suggestion?.let {
            "Tu patrón de consumo fue clasificado en la categoría \"${it.assignedCluster}\". Activar esta rutina podría ahorrarte aproximadamente $kwh kWh."
        } ?: "No encontramos el detalle de esta sugerencia.",
        isActivating = state.isActivating,
        activateError = state.activateError,
        onBack = onBack,
        onShare = onShare,
        onDiscard = onDiscard,
        onActivate = { viewModel.activateSelectedSuggestion(userId) },
    )
}

// ── Screen (sin estado / puramente visual, usada por el Preview) ──────────────
@Composable
fun RoutineDetailScreenContent(
    title: String       = "Apaga el aire 30 min antes de salir",
    description: String = "Detectamos que sales entre 8:15 y 8:45. La habitación mantiene la temperatura 12 min después de apagarlo.",
    stats: List<RoutineStat> = listOf(
        RoutineStat("0.6",  "kWh/día",  "Ahorro\nestimado"),
    ),
    triggers: List<RoutineTrigger> = listOf(
        RoutineTrigger(Icons.Outlined.AutoAwesome, "Categoría detectada", "Clima"),
    ),
    whyReason: String = "Detectamos una oportunidad de ahorro en tu patrón de consumo reciente.",
    isActivating: Boolean = false,
    activateError: String? = null,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onActivate: () -> Unit = {},
) {
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
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        contentDescription = "Volver",
                        tint = OnBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    "Detalle de rutina",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                        .clickable { onShare() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = "Compartir",
                        tint = OnBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Hero dark card ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Accent)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFE8D87A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "SUGERENCIA INTELIGENTE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFB0B8B2)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        description,
                        fontSize = 13.sp,
                        color = Color(0xFFB0B8B2),
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Stats row ─────────────────────────────────────────────────
            if (stats.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    stats.forEach { stat ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Surface)
                                .padding(vertical = 14.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    stat.value,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBackground
                                )
                                Text(
                                    stat.unit,
                                    fontSize = 11.sp,
                                    color = Subtle,
                                    modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                stat.description,
                                fontSize = 10.sp,
                                color = Subtle,
                                lineHeight = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))
            }

            // ── CUÁNDO SE ACTIVA ──────────────────────────────────────────
            if (triggers.isNotEmpty()) {
                SectionLabel("CUÁNDO SE ACTIVA")
                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface)
                ) {
                    triggers.forEachIndexed { index, trigger ->
                        TriggerRow(trigger)
                        if (index < triggers.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = BorderColor,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))
            }

            // ── ¿POR QUÉ TE LO SUGERIMOS? ────────────────────────────────
            SectionLabel("¿POR QUÉ TE LO SUGERIMOS?")
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Outlined.TrendingDown,
                    contentDescription = null,
                    tint = Subtle,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 1.dp)
                )
                Text(
                    whyReason,
                    fontSize = 13.sp,
                    color = Subtle,
                    lineHeight = 19.sp
                )
            }

            if (activateError != null) {
                Spacer(Modifier.height(14.dp))
                Text(
                    activateError,
                    fontSize = 12.sp,
                    color = ErrorColor
                )
            }
        }

        // ── Bottom action bar ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDiscard,
                enabled = !isActivating,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Surface,
                    contentColor   = OnBackground
                )
            ) {
                Text("Descartar", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onActivate,
                enabled = !isActivating,
                modifier = Modifier
                    .weight(2f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                if (isActivating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Activar rutina",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Trigger row ───────────────────────────────────────────────────────────────
@Composable
private fun TriggerRow(trigger: RoutineTrigger) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            trigger.icon,
            contentDescription = null,
            tint = Subtle,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(trigger.label, fontSize = 11.sp, color = Subtle)
            Text(
                trigger.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground
            )
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
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
fun RoutineDetailScreenPreview() {
    RoutineDetailScreenContent()
}