package com.emilionavarro.prueba.senas



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
private val Background    = Color(0xFFEAE7E0)
private val Surface       = Color(0xFFF2EFEA)
private val OnBackground  = Color(0xFF1C1C1C)
private val Subtle        = Color(0xFF7A7A7A)
private val Accent        = Color(0xFF232320)
private val BorderColor   = Color(0xFFDDDAD3)
private val ToggleOn      = Color(0xFF232320)
private val ToggleOff     = Color(0xFFCBC8C0)
private val GifBadgeBg    = Color(0xFF2E332E)
private val GestureCardBg = Color(0xFFE8E5DE)
private val NavSelected   = Color(0xFFE4E1D9)
private val ChipSelected  = Color(0xFF232320)
private val TagBg         = Color(0xFFE4E1D9)

// ── Data ──────────────────────────────────────────────────────────────────────
data class GestureEntry(
    val emoji: String,
    val name: String,
    val action: String,
    val tag: String,
    val uses: Int,
    val isOn: Boolean,
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun GesturesScreen(
    onAddGesture: () -> Unit = {},
    onGestureClick: (GestureEntry) -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
) {
    val allGestures = remember {
        mutableStateListOf(
            GestureEntry("🤚", "Mano abierta", "→ Reproducir música · Spotify", "Música",  124, true),
            GestureEntry("✊", "Puño cerrado", "→ Apagar todo · 4 dispositivos", "Escena",   86, true),
            GestureEntry("✌️", "Paz",          "→ Siguiente canción · Spotify",  "Música",   67, true),
            GestureEntry("👌", "OK",           "→ Confirmar rutina",             "Sistema",  42, true),
            GestureEntry("🤙", "Llámame",      "→ Modo lectura · Sala",          "Escenas",  21, false),
        )
    }

    val toggleStates = remember {
        mutableStateMapOf<String, Boolean>().also { map ->
            allGestures.forEach { map[it.name] = it.isOn }
        }
    }

    val categories = listOf("Todas · ${allGestures.size}", "Música", "Luces", "Escenas")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = allGestures.filter { g ->
        val matchesSearch = searchQuery.isEmpty() ||
                g.name.contains(searchQuery, ignoreCase = true) ||
                g.action.contains(searchQuery, ignoreCase = true)
        val matchesCat = selectedCategory.startsWith("Todas") ||
                g.tag.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCat
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            GesturesBottomNavBar(
                selected          = "Señas",
                onNavInicio       = onNavInicio,
                onNavSenas        = {},
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
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Señas",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "${allGestures.count { toggleStates[it.name] == true }} activas · 340 detecciones esta semana",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Accent, RoundedCornerShape(14.dp))
                        .clickable { onAddGesture() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Agregar seña", tint = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Search bar ────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("Buscar seña o acción", fontSize = 14.sp, color = Subtle)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Subtle)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = Surface,
                    unfocusedContainerColor  = Surface,
                    focusedBorderColor       = OnBackground,
                    unfocusedBorderColor     = BorderColor,
                    focusedTextColor         = OnBackground,
                    unfocusedTextColor       = OnBackground,
                    cursorColor              = OnBackground
                )
            )

            Spacer(Modifier.height(12.dp))

            // ── Category filter chips ─────────────────────────────────────
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSel) ChipSelected else Surface)
                            .border(
                                width = if (isSel) 0.dp else 1.dp,
                                color = BorderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSel) Color.White else OnBackground
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Gesture list ──────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filtered.forEach { gesture ->
                    val isOn = toggleStates[gesture.name] ?: gesture.isOn
                    GestureCard(
                        gesture  = gesture,
                        isOn     = isOn,
                        onToggle = { toggleStates[gesture.name] = it },
                        onClick  = { onGestureClick(gesture) }
                    )
                }
            }
        }
    }
}

// ── Gesture card ──────────────────────────────────────────────────────────────
@Composable
private fun GestureCard(
    gesture: GestureEntry,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // GIF thumbnail
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GestureCardBg),
            contentAlignment = Alignment.Center
        ) {
            Text(gesture.emoji, fontSize = 28.sp)
            // GIF badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GifBadgeBg)
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text("GIF", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            // Play icon
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
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

        Spacer(Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                gesture.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground
            )
            Text(
                gesture.action,
                fontSize = 12.sp,
                color = Subtle
            )
            Spacer(Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TagBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(gesture.tag, fontSize = 11.sp, color = OnBackground, fontWeight = FontWeight.Medium)
                }
                Text("${gesture.uses} usos", fontSize = 11.sp, color = Subtle)
            }
        }

        Spacer(Modifier.width(8.dp))

        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor    = Color.White,
                checkedTrackColor    = ToggleOn,
                uncheckedThumbColor  = Color.White,
                uncheckedTrackColor  = ToggleOff,
                uncheckedBorderColor = ToggleOff
            )
        )
    }
}

// ── Bottom nav ────────────────────────────────────────────────────────────────
@Composable
private fun GesturesBottomNavBar(
    selected: String,
    onNavInicio: () -> Unit,
    onNavSenas: () -> Unit,
    onNavDispositivos: () -> Unit,
    onNavSugerencias: () -> Unit,
    onNavPerfil: () -> Unit,
) {
    val items = listOf(
        Triple("Inicio",       Icons.Outlined.Home,            onNavInicio),
        Triple("Señas",        Icons.Outlined.PanTool,         onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle, onNavDispositivos),
        Triple("Sugerencias",  Icons.Outlined.AutoAwesome,     onNavSugerencias),
        Triple("Perfil",       Icons.Outlined.Person,          onNavPerfil),
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
                val isSel = label == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { action() }
                        .background(if (isSel) NavSelected else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(icon, contentDescription = label, tint = if (isSel) OnBackground else Subtle, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, fontSize = 10.sp, color = if (isSel) OnBackground else Subtle, fontWeight = if (isSel) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GesturesScreenPreview() {
    GesturesScreen()
}

