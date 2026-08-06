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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.perfil.data.AppPreferences
import com.emilionavarro.prueba.perfil.viewmodel.SettingsViewModel
import com.emilionavarro.prueba.perfil.viewmodel.SettingsViewModelFactory

private val Background      = Color(0xFFEAE7E0)
private val Surface         = Color(0xFFF2EFEA)
private val OnBackground    = Color(0xFF1C1C1C)
private val Subtle          = Color(0xFF7A7A7A)
private val BorderColor     = Color(0xFFDDDAD3)
private val ThemeCardBorder = Color(0xFF232320)

@Composable
fun PreferencesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val prefs   = remember { AppPreferences(context) }
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(prefs))
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Surface).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ChevronLeft, "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text("Preferencias", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
        }

        Spacer(Modifier.height(24.dp))

        // ══ APARIENCIA ════════════════════════════════════════════════════════
        SectionLabel("APARIENCIA")
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface).padding(14.dp)
        ) {
            Text("TEMA", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Claro", "Oscuro", "Sistema").forEach { theme ->
                    ThemeCard(
                        label      = theme,
                        isSelected = state.theme == theme,
                        isDark     = theme == "Oscuro",
                        isSystem   = theme == "Sistema",
                        onClick    = { vm.setTheme(theme) },
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ── Theme card ─────────────────────────────────────────────────────────────────
@Composable
private fun ThemeCard(
    label: String, isSelected: Boolean, isDark: Boolean, isSystem: Boolean,
    onClick: () -> Unit, modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Surface else Background)
            .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) ThemeCardBorder else BorderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(6.dp))
                .background(if (isDark) Color(0xFF2E2E2E) else Color(0xFFEAE7E0)),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSystem) {
                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFFEAE7E0)))
                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFF2E2E2E)))
            } else {
                Box(modifier = Modifier.padding(end = 6.dp).width(14.dp).fillMaxHeight(0.55f)
                    .background(if (isDark) Color(0xFF4A4A4A) else Color(0xFFD4D0C8), RoundedCornerShape(3.dp)))
                Box(modifier = Modifier.padding(end = 8.dp).width(10.dp).fillMaxHeight(0.7f)
                    .background(if (isDark) Color(0xFF5A5A5A) else Color(0xFFBBB8B0), RoundedCornerShape(3.dp)))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = OnBackground)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferencesScreenPreview() { PreferencesScreen() }