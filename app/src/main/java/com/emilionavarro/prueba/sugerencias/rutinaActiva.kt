package com.emilionavarro.prueba.sugerencias



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

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val SuccessGreen = Color(0xFF4A8C62)
private val IconGreenBg  = Color(0xFF3D6B50)
private val ToggleOn     = Color(0xFF232320)
private val TagBg        = Color(0xFFE4E1D9)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun RoutineActivatedScreen(
    routineName: String   = "Apagado anticipado · Aire",
    routineSchedule: String = "Lun-Vie · 07:45",
    savingsTag: String    = "~\$140/mes",
    emissionsTag: String  = "12kg CO₂",
    confirmationText: String = "Vamos a apagar tu aire 30 min antes de salir. Te diremos cuánto ahorraste cada semana.",
    onViewRoutines: () -> Unit = {},
    onKeepExploring: () -> Unit = {},
) {
    var triggered by remember { mutableStateOf(false) }

    val checkScale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "check_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(500, delayMillis = 250),
        label = "content_alpha"
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
                // ── Title ─────────────────────────────────────────────────
                Text(
                    "Rutina activada",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    confirmationText,
                    fontSize = 14.sp,
                    color = Subtle,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(24.dp))

                // ── Routine summary card ──────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IconGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.FlashOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                routineName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnBackground
                            )
                            Text(
                                routineSchedule,
                                fontSize = 12.sp,
                                color = Subtle
                            )
                        }

                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ToggleOn,
                            )
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Tags
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(savingsTag, emissionsTag).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TagBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(tag, fontSize = 12.sp, color = OnBackground, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Primary CTA ───────────────────────────────────────────
                Button(
                    onClick = onViewRoutines,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text(
                        "Ver mis rutinas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Secondary CTA ─────────────────────────────────────────
                OutlinedButton(
                    onClick = onKeepExploring,
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
                        "Seguir explorando",
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
fun RoutineActivatedScreenPreview() {
    RoutineActivatedScreen()
}