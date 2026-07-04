package com.emilionavarro.seengo.inicio




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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFDDDAD3)
private val ToggleOn     = Color(0xFF232320)
private val ToggleOff    = Color(0xFFCBC8C0)
private val IconBg       = Color(0xFFE4E1D9)
private val GifBadgeBg   = Color(0xFF2E332E)
private val NavSelected  = Color(0xFFE4E1D9)

// ── Data models ───────────────────────────────────────────────────────────────
data class QuickDevice(
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val isOn: Boolean
)

data class RecentGesture(
    val emoji: String,
    val label: String,
    val time: String
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    userName: String = "María",
    kwhToday: Double = 3.4,
    kwhChange: Int = -18,
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onVerTodos: () -> Unit = {},
) {
    val quickDevices = remember {
        mutableStateListOf(
            QuickDevice("Lámpara Sala",    "Encendida · 12W",    Icons.Outlined.LightMode,      true),
            QuickDevice("Spotify",         "Lo-fi para trabajar", Icons.Outlined.GraphicEq,     true),
            QuickDevice("Aire Habitación", "22° · Auto",          Icons.Outlined.AcUnit,        false),
            QuickDevice("TV Sala",         "Apagada",             Icons.Outlined.Tv,            false),
        )
    }
    val toggleStates = remember {
        mutableStateMapOf<String, Boolean>().also { map ->
            quickDevices.forEach { map[it.name] = it.isOn }
        }
    }

    val gestures = listOf(
        RecentGesture("🤚", "Música",   "09:12"),
        RecentGesture("✊", "Apagar",   "08:40"),
        RecentGesture("✌️", "Siguiente","08:38"),
        RecentGesture("👌", "OK",       "Ayer"),
    )

    Scaffold(
        containerColor = Background,
        bottomBar = {
            HomeBottomNavBar(
                selected = "Inicio",
                onNavInicio       = onNavInicio,
                onNavSenas        = onNavSenas,
                onNavDispositivos = onNavDispositivos,
                onNavSugerencias  = onNavSugerencias,
                onNavPerfil       = onNavPerfil,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Greeting + bell ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Buenos días, $userName",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                    Text(
                        "Tu casa,\nen una seña.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        lineHeight = 34.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .clickable { onNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.NotificationsNone,
                        contentDescription = "Notificaciones",
                        tint = OnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Energy card ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceCard)
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "HOY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Subtle
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, color = OnBackground)) {
                                    append("$kwhToday")
                                }
                                withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = OnBackground)) {
                                    append(" kWh")
                                }
                            }
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "↓${Math.abs(kwhChange)}% vs. ayer — vas bien",
                            fontSize = 12.sp,
                            color = Subtle
                        )
                    }
                    // Mini sparkline chart
                    SparklineChart(
                        modifier = Modifier
                            .size(width = 90.dp, height = 50.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Quick controls ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CONTROLES RÁPIDOS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = Subtle
                )
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    color = OnBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onVerTodos() }
                )
            }

            Spacer(Modifier.height(10.dp))

            // 2x2 grid of quick device cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quickDevices.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { device ->
                            val isOn = toggleStates[device.name] ?: device.isOn
                            QuickDeviceCard(
                                device  = device,
                                isOn    = isOn,
                                onToggle = { toggleStates[device.name] = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty slot if odd number
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Recent gestures ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SEÑAS RECIENTES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = Subtle
                )
                Text("Hoy", fontSize = 12.sp, color = Subtle)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                gestures.forEach { gesture ->
                    GestureCard(gesture)
                }
            }
        }
    }
}

// ── Quick device card ─────────────────────────────────────────────────────────
@Composable
private fun QuickDeviceCard(
    device: QuickDevice,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(IconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    device.icon,
                    contentDescription = null,
                    tint = OnBackground,
                    modifier = Modifier.size(18.dp)
                )
            }
            Switch(
                checked = isOn,
                onCheckedChange = onToggle,
                modifier = Modifier.size(width = 44.dp, height = 26.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor    = Color.White,
                    checkedTrackColor    = ToggleOn,
                    uncheckedThumbColor  = Color.White,
                    uncheckedTrackColor  = ToggleOff,
                    uncheckedBorderColor = ToggleOff
                )
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            device.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnBackground
        )
        Text(
            device.subtitle,
            fontSize = 11.sp,
            color = Subtle,
            maxLines = 1
        )
    }
}

// ── Gesture card ──────────────────────────────────────────────────────────────
@Composable
private fun GestureCard(gesture: RecentGesture) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Text(gesture.emoji, fontSize = 32.sp)
            // GIF badge top-left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GifBadgeBg)
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text("GIF", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            // Play icon bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
                    .size(16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = OnBackground,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(gesture.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Text(gesture.time, fontSize = 11.sp, color = Subtle)
    }
}

// ── Sparkline chart ───────────────────────────────────────────────────────────
@Composable
private fun SparklineChart(modifier: Modifier = Modifier) {
    val points = listOf(0.3f, 0.5f, 0.4f, 0.6f, 0.5f, 0.7f, 0.65f, 0.8f, 0.75f, 0.9f)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path()
        points.forEachIndexed { i, v ->
            val x = (i / (points.size - 1).toFloat()) * w
            val y = h - v * h
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = Color(0xFF4A4A45), style = Stroke(width = 2.5f))
    }
}

// ── Bottom nav bar ────────────────────────────────────────────────────────────
@Composable
private fun HomeBottomNavBar(
    selected: String,
    onNavInicio: () -> Unit,
    onNavSenas: () -> Unit,
    onNavDispositivos: () -> Unit,
    onNavSugerencias: () -> Unit,
    onNavPerfil: () -> Unit,
) {
    val items = listOf(
        Triple("Inicio",       Icons.Outlined.Home,             onNavInicio),
        Triple("Señas",        Icons.Outlined.PanTool,          onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle,  onNavDispositivos),
        Triple("Sugerencias",  Icons.Outlined.AutoAwesome,      onNavSugerencias),
        Triple("Perfil",       Icons.Outlined.Person,           onNavPerfil),
    )

    Surface(color = Background, tonalElevation = 0.dp) {
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { (label, icon, action) ->
                val isSelected = label == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { action() }
                        .background(if (isSelected) NavSelected else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = if (isSelected) OnBackground else Subtle,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (isSelected) OnBackground else Subtle,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}