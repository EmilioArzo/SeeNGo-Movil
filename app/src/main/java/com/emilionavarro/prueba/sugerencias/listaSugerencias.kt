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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
private val NavSelected  = Color(0xFFE4E1D9)
private val TagBg        = Color(0xFFE4E1D9)
private val GreenText    = Color(0xFF4A8C62)

// Icon background tints per card
private val IconBgGreen  = Color(0xFFD6EAD8)
private val IconBgOrange = Color(0xFFF5E0C8)
private val IconBgGray   = Color(0xFFE4E1D9)

// ── Data ──────────────────────────────────────────────────────────────────────
data class SuggestionCard(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val description: String,
    val tag: String,
    val metric: String,
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun SuggestionsScreen(
    onFilter: () -> Unit = {},
    onActivateFeatured: () -> Unit = {},
    onSuggestionClick: (SuggestionCard) -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
) {
    val suggestions = listOf(
        SuggestionCard(
            icon        = Icons.Outlined.DarkMode,
            iconBg      = IconBgGreen,
            iconTint    = Color(0xFF4A8C62),
            title       = "Eficiencia nocturna",
            description = "Apaga luces y TV cuando no hay movimiento por 20 min.",
            tag         = "Iluminación",
            metric      = "↓ ~0.4 kWh/noche"
        ),
        SuggestionCard(
            icon        = Icons.Outlined.WbSunny,
            iconBg      = IconBgOrange,
            iconTint    = Color(0xFFD4823A),
            title       = "Despertar suave",
            description = "Sube luces y pon tu playlist matutina con la seña 🤚.",
            tag         = "Mañana",
            metric      = "↓ Confort"
        ),
        SuggestionCard(
            icon        = Icons.Outlined.Tv,
            iconBg      = IconBgGray,
            iconTint    = OnBackground,
            title       = "Modo cine",
            description = "Atenúa luces de Sala y silencia notificaciones con 🤚.",
            tag         = "Escena",
            metric      = "↓ Confort"
        ),
    )

    Scaffold(
        containerColor = Background,
        bottomBar = {
            SuggestionsBottomNavBar(
                selected          = "Sugerencias",
                onNavInicio       = onNavInicio,
                onNavSenas        = onNavSenas,
                onNavDispositivos = onNavDispositivos,
                onNavSugerencias  = {},
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
                        "Sugerencias",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "3 rutinas pueden ahorrarte energía",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Surface)
                        .clickable { onFilter() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Tune,
                        contentDescription = "Filtrar",
                        tint = OnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Featured suggestion (dark card) ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Accent)
                    .padding(20.dp)
            ) {
                Column {
                    // Badge
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
                            "SUGERENCIA DEL DÍA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFB0B8B2)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Apaga el aire 30 min antes de salir",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Detectamos que sales de casa entre 8:15 y 8:45. Apagar el aire antes podría ahorrarte ~0.6 kWh diarios.",
                        fontSize = 13.sp,
                        color = Color(0xFFB0B8B2),
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = onActivateFeatured,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor   = Accent
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "Activar rutina",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            "~\$140/mes",
                            fontSize = 13.sp,
                            color = Color(0xFFB0B8B2)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Section label ─────────────────────────────────────────────
            Text(
                "OTRAS RUTINAS PARA TI",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Subtle
            )

            Spacer(Modifier.height(10.dp))

            // ── Suggestion cards ──────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                suggestions.forEach { suggestion ->
                    SuggestionRow(
                        suggestion = suggestion,
                        onClick    = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
    }
}

// ── Suggestion row card ───────────────────────────────────────────────────────
@Composable
private fun SuggestionRow(suggestion: SuggestionCard, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(suggestion.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                suggestion.icon,
                contentDescription = null,
                tint = suggestion.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                suggestion.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                suggestion.description,
                fontSize = 12.sp,
                color = Subtle,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TagBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(suggestion.tag, fontSize = 11.sp, color = OnBackground, fontWeight = FontWeight.Medium)
                }
                Text(suggestion.metric, fontSize = 11.sp, color = GreenText, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Subtle,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Bottom nav ────────────────────────────────────────────────────────────────
@Composable
private fun SuggestionsBottomNavBar(
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
fun SuggestionsScreenPreview() {
    SuggestionsScreen()
}

