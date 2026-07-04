package com.emilionavarro.prueba.perfil



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
private val AvatarBg     = Color(0xFFDDDAD3)
private val NavSelected  = Color(0xFFE4E1D9)
private val LogoutRed    = Color(0xFFB85C38)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    userName: String  = "María Restrepo",
    userEmail: String = "maria.restrepo@correo.com",
    location: String  = "Hogar · Medellín",
    gestures: Int     = 5,
    devices: Int      = 7,
    savings: Int      = 18,
    onEdit: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onConfiguration: () -> Unit = {},
    onPreferences: () -> Unit = {},

) {
    val initials = userName.split(" ")
        .take(2).joinToString("") { it.first().uppercase() }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            ProfileBottomNavBar(
                selected          = "Perfil",
                onNavInicio       = onNavInicio,
                onNavSenas        = onNavSenas,
                onNavDispositivos = onNavDispositivos,
                onNavSugerencias  = onNavSugerencias,
                onNavPerfil       = {},
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Perfil",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "Tu cuenta y preferencias",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Editar perfil",
                        tint = OnBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── User card ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground
                    )
                    Text(
                        userEmail,
                        fontSize = 12.sp,
                        color = Subtle
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AvatarBg)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(location, fontSize = 11.sp, color = OnBackground)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Stats row ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Pair("$gestures", "Señas"),
                    Pair("$devices",  "Dispositivos"),
                    Pair("$savings%", "Ahorro"),
                ).forEach { (value, label) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Surface)
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            value,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )
                        Text(
                            label,
                            fontSize = 11.sp,
                            color = Subtle
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── CUENTA section ────────────────────────────────────────────
            SectionLabel("CUENTA")
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsRow(
                    icon    = Icons.Outlined.AccountCircle,
                    label   = "Editar perfil",
                    onClick = onEdit
                )
                RowDivider()
                SettingsRow(
                    icon     = Icons.Outlined.Shield,
                    label    = "Seguridad",
                    trailing = "2FA activado",
                    onClick  = onConfiguration
                )
                RowDivider()
                SettingsRow(
                    icon     = Icons.Outlined.NotificationsNone,
                    label    = "Notificaciones",
                    trailing = "Todas",
                    onClick  = onConfiguration
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── PREFERENCIAS section ──────────────────────────────────────
            SectionLabel("PREFERENCIAS")
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsRow(
                    icon     = Icons.Outlined.Translate,
                    label    = "Idioma",
                    trailing = "Español",
                    onClick  = onPreferences
                )
                RowDivider()
                SettingsRow(
                    icon     = Icons.Outlined.DarkMode,
                    label    = "Tema",
                    trailing = "Cálido · Claro",
                    onClick  = onPreferences
                )
                RowDivider()
                SettingsRow(
                    icon     = Icons.Outlined.CameraAlt,
                    label    = "Cámara del dispositivo",
                    trailing = "Vinculada",
                    onClick  = onPreferences
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Logout ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface)
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = LogoutRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Cerrar sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = LogoutRed
                    )
                }
            }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

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

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface),
        content = content
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = BorderColor,
        thickness = 0.5.dp
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    trailing: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFE4E1D9), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = OnBackground, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = OnBackground,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(trailing, fontSize = 13.sp, color = Subtle)
            Spacer(Modifier.width(6.dp))
        }
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
private fun ProfileBottomNavBar(
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
fun ProfileScreenPreview() {
    ProfileScreen()
}