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

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val PreviewBg    = Color(0xFFE8E5DE)
private val GifBadgeBg   = Color(0xFF2E332E)
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyBg    = Color(0xFFD6EED8)
private val DeleteRed    = Color(0xFFB85C38)

// ── Data ──────────────────────────────────────────────────────────────────────
data class GestureDetail(
    val name: String          = "Mano abierta",
    val duration: String      = "1.4s",
    val emoji: String         = "🤚",
    val actionName: String    = "Reproducir música",
    val actionSource: String  = "Spotify · \"Lo-fi para trabajar\"",
    val category: String      = "Música",
    val status: String        = "Activa",
    val detections: String    = "124 totales",
    val lastSeen: String      = "Hoy · 09:12",
    val confidence: String    = "94% promedio",
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun GestureDetailScreen(
    detail: GestureDetail = GestureDetail(),
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onChangeAction: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onTest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val detailRows = listOf(
        "Categoría"   to detail.category,
        "Estado"      to detail.status,
        "Detecciones" to detail.detections,
        "Última vez"  to detail.lastSeen,
        "Confianza"   to detail.confidence,
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

            // ── GIF preview ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PreviewBg),
                contentAlignment = Alignment.Center
            ) {
                // Emoji placeholder (replace with actual GIF/video player)
                Text(detail.emoji, fontSize = 90.sp)

                // GIF badge top-left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GifBadgeBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        "GIF · ${detail.duration}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Play button bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onTest() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "Reproducir",
                        tint = OnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                    .clickable { onActionClick() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spotify icon box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpotifyBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.GraphicEq,
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(detail.actionName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
                    Text(detail.actionSource, fontSize = 12.sp, color = Subtle)
                }
                Icon(Icons.Outlined.ChevronRight, null, tint = Subtle, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.height(20.dp))

            // ── Details table ─────────────────────────────────────────────
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
                // Test button
                OutlinedButton(
                    onClick = onTest,
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
                    Icon(Icons.Outlined.DeleteOutline, null, tint = DeleteRed, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeleteRed)
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GestureDetailScreenPreview() {
    GestureDetailScreen()
}