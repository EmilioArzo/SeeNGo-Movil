package com.emilionavarro.prueba.dispositivos


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
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
private val IconBg       = Color(0xFFE4E1D9)
private val SuccessGreen = Color(0xFF4A8C62)
private val OnlineBg     = Color(0xFFECEBE6)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DeviceSuccessScreen(
    deviceName: String = "Lámpara de pie",
    roomName: String = "Sala",
    deviceModel: String = "Shelly Plug S",
    deviceIcon: ImageVector = Icons.Outlined.LightMode,
    onLinkGesture: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    // Entrance animations
    val animSpec = tween<Float>(durationMillis = 500, easing = EaseOutBack)
    var triggered by remember { mutableStateOf(false) }

    val checkScale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = animSpec,
        label = "check_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(600, delayMillis = 200),
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
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ── Checkmark ─────────────────────────────────────────────────
            Text(
                text = "✓",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen,
                modifier = Modifier.scale(checkScale)
            )

            Spacer(Modifier.height(24.dp))

            // ── Title & subtitle ──────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha)
            ) {
                Text(
                    "¡Listo!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "\"$deviceName\" ya está en $roomName.\nConéctalo a una seña para empezar a\ncontrolarlo.",
                    fontSize = 15.sp,
                    color = Subtle,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(28.dp))

                // ── Device card ───────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(IconBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            deviceIcon,
                            contentDescription = null,
                            tint = OnBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            deviceName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnBackground
                        )
                        Text(
                            "$roomName · $deviceModel",
                            fontSize = 12.sp,
                            color = Subtle
                        )
                    }
                    // Online badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(OnlineBg)
                            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "Online",
                            fontSize = 12.sp,
                            color = OnBackground,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Link gesture CTA ──────────────────────────────────────
                Button(
                    onClick = onLinkGesture,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("✋", fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Vincular una seña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Secondary "done" button ───────────────────────────────
                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface,
                        contentColor = OnBackground
                    )
                ) {
                    Text(
                        "Listo, volver",
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
fun DeviceSuccessScreenPreview() {
    DeviceSuccessScreen()
}