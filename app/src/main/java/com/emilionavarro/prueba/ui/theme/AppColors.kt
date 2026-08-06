package com.emilionavarro.prueba.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val onBackground: Color,
    val subtle: Color,
    val accent: Color,
    val onAccent: Color,
    val borderColor: Color,
    val iconBg: Color,
    val toggleOn: Color,
    val toggleOff: Color,
    val errorColor: Color,
    val successColor: Color,
)

val LightAppColors = AppColors(
    background   = Color(0xFFEAE7E0),
    surface      = Color(0xFFF2EFEA),
    surfaceCard  = Color(0xFFE8E5DE),
    onBackground = Color(0xFF1C1C1C),
    subtle       = Color(0xFF7A7A7A),
    accent       = Color(0xFF232320),
    onAccent     = Color.White,
    borderColor  = Color(0xFFDDDAD3),
    iconBg       = Color(0xFFE4E1D9),
    toggleOn     = Color(0xFF232320),
    toggleOff    = Color(0xFFCBC8C0),
    errorColor   = Color(0xFFD94F3D),
    successColor = Color(0xFF4A8C62),
)

val DarkAppColors = AppColors(
    background   = Color(0xFF16171A),
    surface      = Color(0xFF1F2023),
    surfaceCard  = Color(0xFF26282C),
    onBackground = Color(0xFFEDEDED),
    subtle       = Color(0xFF9A9A9A),
    accent       = Color(0xFFE8E5DE),
    onAccent     = Color(0xFF16171A),
    borderColor  = Color(0xFF34363A),
    iconBg       = Color(0xFF2E3034),
    toggleOn     = Color(0xFFE8E5DE),
    toggleOff    = Color(0xFF44464A),
    errorColor   = Color(0xFFE0685A),
    successColor = Color(0xFF6BB98A),
)

// CompositionLocal: cualquier @Composable puede leer LocalAppColors.current
val LocalAppColors = staticCompositionLocalOf { LightAppColors }
