package com.emilionavarro.prueba.dispositivos



import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFEFECE5)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val IconBg       = Color(0xFF3A3F3A)
private val ToggleOn     = Color(0xFF232320)
private val ToggleOff    = Color(0xFFCBC8C0)
private val BarColor     = Color(0xFF2E332E)
private val GifBadgeBg   = Color(0xFF2E332E)

// ── Data models ───────────────────────────────────────────────────────────────
data class LinkedGesture(val label: String, val action: String)
data class DeviceInfo(val key: String, val value: String)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DeviceDetailScreen(
    deviceName: String = "Lámpara de pie",
    roomAndPlug: String = "Sala · Shelly Plug",
    wattsNow: Int = 12,
    kwhToday: Double = 0.42,
    costMonth: Int = 520,
    isOnInitial: Boolean = true,
    hourlyData: List<Float> = sampleHourlyData(),
    gestures: List<LinkedGesture> = listOf(
        LinkedGesture("Puño cerrado", "→ Apagar"),
        LinkedGesture("OK",           "→ Encender"),
    ),
    info: List<DeviceInfo> = listOf(
        DeviceInfo("IP local",   "192.168.1.42"),
        DeviceInfo("Firmware",   "v1.14.3"),
        DeviceInfo("Vinculado",  "hace 3 meses"),
    ),
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onEditGestures: () -> Unit = {},
    onGestureClick: (LinkedGesture) -> Unit = {},
) {
    var isOn by remember { mutableStateOf(isOnInitial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    deviceName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    roomAndPlug,
                    fontSize = 12.sp,
                    color = Subtle
                )
            }
            // More options button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Accent)
                    .clickable { onMore() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    contentDescription = "Más opciones",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Status card ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(IconBg, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.LightMode,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Switch(
                        checked = isOn,
                        onCheckedChange = { isOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor    = Color.White,
                            checkedTrackColor    = ToggleOn,
                            uncheckedThumbColor  = Color.White,
                            uncheckedTrackColor  = ToggleOff,
                            uncheckedBorderColor = ToggleOff
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Watts
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = OnBackground)) {
                            append("$wattsNow")
                        }
                        withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = OnBackground)) {
                            append(" W ahora")
                        }
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$kwhToday kWh hoy · ~\$$costMonth este mes",
                    fontSize = 13.sp,
                    color = Subtle
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Last 24h chart ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ÚLTIMAS 24 H",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = Subtle
                    )
                    Text("W", fontSize = 11.sp, color = Subtle)
                }
                Spacer(Modifier.height(12.dp))
                BarChart(data = hourlyData, modifier = Modifier.fillMaxWidth().height(80.dp))
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("00h", "12h", "24h").forEach { label ->
                        Text(label, fontSize = 11.sp, color = Subtle)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Linked gestures ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SEÑAS VINCULADAS",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Subtle
            )
            Text(
                "Editar",
                fontSize = 13.sp,
                color = OnBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onEditGestures() }
            )
        }

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
        ) {
            gestures.forEachIndexed { index, gesture ->
                GestureRow(gesture = gesture, onClick = { onGestureClick(gesture) })
                if (index < gestures.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = BorderColor,
                        thickness = 0.5.dp
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Info section ──────────────────────────────────────────────────
        Text(
            "INFORMACIÓN",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = Subtle
        )

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
        ) {
            info.forEachIndexed { index, item ->
                InfoRow(item = item)
                if (index < info.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = BorderColor,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

// ── Bar chart ─────────────────────────────────────────────────────────────────
@Composable
private fun BarChart(data: List<Float>, modifier: Modifier = Modifier) {
    val maxVal = data.maxOrNull()?.takeIf { it > 0f } ?: 1f
    Canvas(modifier = modifier) {
        val totalBars = data.size
        val barWidth = (size.width / totalBars) * 0.55f
        val gap      = (size.width / totalBars) * 0.45f
        val maxH     = size.height

        data.forEachIndexed { i, value ->
            val barH = (value / maxVal) * maxH
            val x    = i * (barWidth + gap)
            val y    = maxH - barH
            drawRoundRect(
                color        = BarColor,
                topLeft      = Offset(x, y),
                size         = Size(barWidth, barH),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}

// ── Gesture row ───────────────────────────────────────────────────────────────
@Composable
private fun GestureRow(gesture: LinkedGesture, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // GIF badge placeholder
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(GifBadgeBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Inner play/gif indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("GIF", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
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
private fun InfoRow(item: DeviceInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.key, fontSize = 14.sp, color = Subtle)
        Text(item.value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
    }
}

// ── Sample data ───────────────────────────────────────────────────────────────
fun sampleHourlyData(): List<Float> = listOf(
    8f, 12f, 6f, 14f, 10f, 4f, 7f, 9f, 15f, 11f, 6f, 13f,
    10f, 16f, 12f, 8f, 14f, 18f, 15f, 12f, 16f, 14f, 12f, 10f
)

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DeviceDetailScreenPreview() {
    DeviceDetailScreen()
}