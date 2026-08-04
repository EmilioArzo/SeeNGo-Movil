package com.emilionavarro.prueba.senas

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModel

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val SuccessGreen = Color(0xFF4A8C62)
private val GestureCardBg= Color(0xFFE8E5DE)

// ── Screen (con estado / conectada al ViewModel) ────────────────────────────────
@Composable
fun GestureSavedScreen(
    viewModel: GesturesViewModel,
    onTest: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val state = viewModel.uiState
    val gesture = state.savedGesture

    GestureSavedScreenContent(
        gestureName = gesture?.name ?: "Tu seña",
        actionSummary = gesture?.linkedAction?.let { "→ $it" } ?: "Sin acción vinculada",
        confirmText = if (gesture?.linkedAction != null)
            "\"${gesture.name}\" ejecutará \"${gesture.linkedAction}\" cuando la detectemos."
        else
            "Guardamos \"${gesture?.name ?: ""}\". Puedes vincularle una acción cuando quieras.",
        onTest = onTest,
        onBack = {
            viewModel.clearSaved()
            onBack()
        },
    )
}

// ── Screen (sin estado / puramente visual, usada por el Preview) ──────────────
@Composable
fun GestureSavedScreenContent(
    gestureName: String    = "Mano abierta",
    actionSummary: String  = "→ Spotify · Lo-fi para trabajar",
    confirmText: String    = "\"Mano abierta\" ejecutará esta acción cuando la detectemos.",
    onTest: () -> Unit     = {},
    onBack: () -> Unit     = {},
) {
    var triggered by remember { mutableStateOf(false) }

    val checkScale by animateFloatAsState(
        targetValue      = if (triggered) 1f else 0f,
        animationSpec    = tween(500, easing = EaseOutBack),
        label            = "check_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue      = if (triggered) 1f else 0f,
        animationSpec    = tween(500, delayMillis = 250),
        label            = "content_alpha"
    )

    LaunchedEffect(Unit) { triggered = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ── Checkmark ─────────────────────────────────────────────────
            Text(
                "✓",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen,
                modifier = Modifier.scale(checkScale)
            )

            Spacer(Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(contentAlpha)
            ) {
                Text(
                    "¡Seña guardada!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    confirmText,
                    fontSize = 14.sp,
                    color = Subtle,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(24.dp))

                // ── Gesture summary card ──────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(GestureCardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PanTool, contentDescription = null, tint = OnBackground, modifier = Modifier.size(26.dp))
                    }

                    Column {
                        Text(
                            gestureName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnBackground
                        )
                        Text(
                            actionSummary,
                            fontSize = 12.sp,
                            color = Subtle
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Primary CTA (deshabilitado: no hay endpoint de prueba en vivo) ──
                Button(
                    onClick = onTest,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Probar seña ahora",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Secondary CTA ─────────────────────────────────────────
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface,
                        contentColor   = OnBackground
                    )
                ) {
                    Text(
                        "Volver a Señas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnBackground
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GestureSavedScreenPreview() {
    GestureSavedScreenContent()
}