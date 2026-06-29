package com.emilionavarro.prueba.dispositivos


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background      = Color(0xFFEAE7E0)
private val Surface         = Color(0xFFF2EFEA)
private val OnBackground    = Color(0xFF1C1C1C)
private val Subtle          = Color(0xFF7A7A7A)
private val Accent          = Color(0xFF232320)
private val BorderColor     = Color(0xFFDDDAD3)
private val RingColor       = Color(0xFFDDDAD3)
private val ProgressActive  = Color(0xFF232320)
private val ProgressInact   = Color(0xFFD4D0C8)
private val GreenDot        = Color(0xFF4A9B6F)
private val DeviceBadgeBg   = Color(0xFFF2EFEA)

// ── Data ──────────────────────────────────────────────────────────────────────
data class FoundDevice(val id: String, val name: String)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun ScanNetworkScreen(
    currentStep: Int = 1,
    totalSteps: Int = 3,
    foundDevices: List<FoundDevice> = listOf(
        FoundDevice("1", "shellyplug-S-3F2A"),
        FoundDevice("2", "shelly1pm-A87B"),
    ),
    scannedCount: Int = 14,
    totalCount: Int = 254,
    subnet: String = "192.168.1.0/24",
    onBack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onViewFound: (List<FoundDevice>) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        contentDescription = "Volver",
                        tint = OnBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Buscando en tu red",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "Paso $currentStep de $totalSteps · WiFi local",
                        fontSize = 12.sp,
                        color = Subtle
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Step progress bars ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (index < currentStep) ProgressActive else ProgressInact)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Radar + floating device badges ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RadarAnimation()

                // Device badges - positioned around the radar
                if (foundDevices.isNotEmpty()) {
                    // Top-right badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 4.dp)
                    ) {
                        DeviceBadge(name = foundDevices[0].name)
                    }
                }
                if (foundDevices.size > 1) {
                    // Bottom-left badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 40.dp, start = 4.dp)
                    ) {
                        DeviceBadge(name = foundDevices[1].name)
                    }
                }
            }

            // ── Status text ───────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Escaneando mDNS...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${foundDevices.size} dispositivos encontrados · sigue buscando",
                    fontSize = 13.sp,
                    color = Subtle
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "$subnet  ·  $scannedCount / $totalCount",
                    fontSize = 11.sp,
                    color = Color(0xFFAAAAAA)
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Background,
                    contentColor = OnBackground
                )
            ) {
                Text("Cancelar", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            // View found
            Button(
                onClick = { onViewFound(foundDevices) },
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = foundDevices.isNotEmpty()
            ) {
                Text(
                    "Ver encontrados (${foundDevices.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Radar animation ───────────────────────────────────────────────────────────
@Composable
private fun RadarAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    // 3 concentric rings pulsing outward
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(0)
        ), label = "r1"
    )
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(600)
        ), label = "r2"
    )
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1200)
        ), label = "r3"
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(0)
        ), label = "ra1"
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(600)
        ), label = "ra2"
    )
    val ring3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1200)
        ), label = "ra3"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
        // Animated rings
        listOf(
            ring1Scale to ring1Alpha,
            ring2Scale to ring2Alpha,
            ring3Scale to ring3Alpha,
        ).forEach { (scale, alpha) ->
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .border(1.5.dp, RingColor, CircleShape)
            )
        }

        // Static inner rings
        Box(
            modifier = Modifier
                .size(170.dp)
                .border(1.dp, RingColor, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(110.dp)
                .border(1.dp, RingColor.copy(alpha = 0.5f), CircleShape)
        )

        // Center WiFi button
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Wifi,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

// ── Device badge ──────────────────────────────────────────────────────────────
@Composable
private fun DeviceBadge(name: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DeviceBadgeBg)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(GreenDot, CircleShape)
        )
        Text(
            name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = OnBackground
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ScanNetworkScreenPreview() {
    ScanNetworkScreen()
}