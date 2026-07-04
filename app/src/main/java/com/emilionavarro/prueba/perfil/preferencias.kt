package com.emilionavarro.prueba.perfil



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
private val BorderColor     = Color(0xFFDDDAD3)
private val IconBg          = Color(0xFFE4E1D9)
private val ToggleOn        = Color(0xFF232320)
private val ToggleOff       = Color(0xFFCBC8C0)
private val SegmentSelected = Color(0xFF232320)
private val ThemeCardBorder = Color(0xFF232320)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun PreferencesScreen(
    onBack: () -> Unit = {},
) {
    var selectedTheme by remember { mutableStateOf("Claro") }
    var selectedEnergy by remember { mutableStateOf("kWh") }
    var selectedTemp by remember { mutableStateOf("°C") }
    var highContrast by remember { mutableStateOf(false) }
    var vibration by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
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
            Text(
                "Preferencias",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        // ══ APARIENCIA ════════════════════════════════════════════════════
        SectionLabel("APARIENCIA")
        Spacer(Modifier.height(8.dp))

        // ── Theme picker ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
                .padding(14.dp)
        ) {
            Text(
                "TEMA",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Subtle
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Claro", "Oscuro", "Sistema").forEach { theme ->
                    ThemeCard(
                        label      = theme,
                        isSelected = theme == selectedTheme,
                        isDark     = theme == "Oscuro",
                        isSystem   = theme == "Sistema",
                        onClick    = { selectedTheme = theme },
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Appearance nav rows ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
        ) {
            NavRow(Icons.Outlined.Translate, "Idioma",         "Español")
            RowDivider()
            NavRow(Icons.Outlined.AttachMoney, "Moneda",       "COP · \$")
            RowDivider()
            NavRow(Icons.Outlined.Schedule,  "Formato de hora","24 horas")
        }

        Spacer(Modifier.height(20.dp))

        // ══ UNIDADES DE MEDIDA ════════════════════════════════════════════
        SectionLabel("UNIDADES DE MEDIDA")
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Energía
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Energía",
                    fontSize = 14.sp,
                    color = OnBackground,
                    modifier = Modifier.weight(1f)
                )
                SegmentedControl(
                    options   = listOf("kWh", "Wh", "J"),
                    selected  = selectedEnergy,
                    onSelect  = { selectedEnergy = it }
                )
            }

            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

            // Temperatura
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Temperatura",
                    fontSize = 14.sp,
                    color = OnBackground,
                    modifier = Modifier.weight(1f)
                )
                SegmentedControl(
                    options  = listOf("°C", "°F"),
                    selected = selectedTemp,
                    onSelect = { selectedTemp = it }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ══ ACCESIBILIDAD ════════════════════════════════════════════════
        SectionLabel("ACCESIBILIDAD")
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Surface)
        ) {
            NavRow(Icons.Outlined.TextFields, "Tamaño de texto", "Mediano")
            RowDivider()
            ToggleRow(
                icon     = Icons.Outlined.Contrast,
                label    = "Alto contraste",
                isOn     = highContrast,
                onToggle = { highContrast = it }
            )
            RowDivider()
            ToggleRow(
                icon     = null,
                label    = "Vibración al detectar seña",
                isOn     = vibration,
                onToggle = { vibration = it }
            )
        }
    }
}

// ── Theme card ────────────────────────────────────────────────────────────────
@Composable
private fun ThemeCard(
    label: String,
    isSelected: Boolean,
    isDark: Boolean,
    isSystem: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Surface else Background)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) ThemeCardBorder else BorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mini preview of the theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isDark) Color(0xFF2E2E2E) else Color(0xFFEAE7E0)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSystem) {
                // Half light / half dark
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color(0xFFEAE7E0))
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color(0xFF2E2E2E))
                )
            } else {
                // Small block indicator
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .width(14.dp)
                        .fillMaxHeight(0.55f)
                        .background(
                            if (isDark) Color(0xFF4A4A4A) else Color(0xFFD4D0C8),
                            RoundedCornerShape(3.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(10.dp)
                        .fillMaxHeight(0.7f)
                        .background(
                            if (isDark) Color(0xFF5A5A5A) else Color(0xFFBBB8B0),
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = OnBackground
        )
    }
}

// ── Segmented control ─────────────────────────────────────────────────────────
@Composable
private fun SegmentedControl(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE0DDD6))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSel = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) SegmentSelected else Color.Transparent)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    fontSize = 13.sp,
                    fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSel) Color.White else Subtle
                )
            }
        }
    }
}

// ── Shared row components ─────────────────────────────────────────────────────
@Composable
private fun NavRow(icon: ImageVector, label: String, trailing: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(IconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = OnBackground, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = OnBackground, modifier = Modifier.weight(1f))
        if (trailing != null) {
            Text(trailing, fontSize = 12.sp, color = Subtle)
            Spacer(Modifier.width(4.dp))
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = Subtle, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ToggleRow(icon: ImageVector?, label: String, isOn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(IconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = OnBackground, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(12.dp))
        }
        Text(label, fontSize = 14.sp, color = OnBackground, modifier = Modifier.weight(1f))
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

@Composable
private fun RowDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = BorderColor, thickness = 0.5.dp)
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen()
}