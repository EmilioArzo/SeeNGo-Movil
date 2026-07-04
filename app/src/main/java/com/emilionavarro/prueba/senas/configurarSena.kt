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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background      = Color(0xFFEAE7E0)
private val Surface         = Color(0xFFF2EFEA)
private val OnBackground    = Color(0xFF1C1C1C)
private val Subtle          = Color(0xFF7A7A7A)
private val Accent          = Color(0xFF232320)
private val BorderColor     = Color(0xFFDDDAD3)
private val GifBadgeBg      = Color(0xFF2E332E)
private val CardSelected    = Color(0xFFE8E5DE)
private val BorderSelected  = Color(0xFF232320)

// ── Data ──────────────────────────────────────────────────────────────────────
data class ActionType(val icon: ImageVector, val label: String)
data class ActionDetail(val icon: ImageVector, val label: String, val value: String)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun ConfigureGestureScreen(
    gestureName: String     = "Mano abierta",
    gestureEmoji: String    = "🤚",
    onBack: () -> Unit      = {},
    onSave: (actionType: String, details: Map<String, String>) -> Unit = { _, _ -> },
    onDetailClick: (String) -> Unit = {},
) {
    val actionTypes = listOf(
        ActionType(Icons.Outlined.LightMode,   "Dispositivo"),
        ActionType(Icons.Outlined.GraphicEq,   "Música"),
        ActionType(Icons.Outlined.Layers,      "Escena"),
        ActionType(Icons.Outlined.Settings,    "Sistema"),
    )
    var selectedType by remember { mutableStateOf("Música") }

    val details = listOf(
        ActionDetail(Icons.Outlined.GraphicEq, "Servicio",  "Spotify · maria.r"),
        ActionDetail(Icons.Outlined.PlayArrow, "Acción",    "Reproducir playlist"),
        ActionDetail(Icons.Outlined.MusicNote, "Playlist",  "Lo-fi para trabajar"),
        ActionDetail(Icons.Outlined.VolumeUp,  "Volumen",   "60%"),
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

            Spacer(Modifier.height(20.dp))

            // ── Gesture header ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // GIF thumbnail
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8E5DE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(gestureEmoji, fontSize = 26.sp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(GifBadgeBg)
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text("GIF", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(3.dp)
                            .size(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null, tint = OnBackground, modifier = Modifier.size(9.dp))
                    }
                }

                Column {
                    Text(gestureName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
                    Text("Vinculada a:", fontSize = 12.sp, color = Subtle)
                }
            }

            Spacer(Modifier.height(22.dp))

            // ── Action type grid ──────────────────────────────────────────
            SectionLabel("TIPO DE ACCIÓN")
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                actionTypes.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { type ->
                            val isSel = type.label == selectedType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) CardSelected else Surface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) BorderSelected else BorderColor,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedType = type.label }
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        type.icon,
                                        contentDescription = null,
                                        tint = OnBackground,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        type.label,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                                        color = OnBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // ── Detail rows ───────────────────────────────────────────────
            SectionLabel("DETALLE")
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
            ) {
                details.forEachIndexed { index, detail ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailClick(detail.label) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(detail.icon, null, tint = Subtle, modifier = Modifier.size(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(detail.label, fontSize = 11.sp, color = Subtle)
                            Text(detail.value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
                        }
                        Icon(Icons.Outlined.ChevronRight, null, tint = Subtle, modifier = Modifier.size(18.dp))
                    }
                    if (index < details.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = BorderColor,
                            thickness = 0.5.dp
                        )
                    }
                }
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
                onClick = { onSave(selectedType, emptyMap()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
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
    ConfigureGestureScreen()
}