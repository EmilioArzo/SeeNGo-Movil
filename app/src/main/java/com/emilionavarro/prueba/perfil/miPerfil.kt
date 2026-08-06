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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.inicio.ProfileScreenSkeleton
import com.emilionavarro.prueba.perfil.viewmodel.ProfileUiState
import com.emilionavarro.prueba.perfil.viewmodel.ProfileViewModel
import com.emilionavarro.prueba.perfil.viewmodel.ProfileViewModelFactory
import com.emilionavarro.prueba.ui.theme.LocalAppColors

// ── Ya NO se declaran colores fijos aquí (Background, Surface, OnBackground, ──
// ── Subtle, Accent, BorderColor, AvatarBg, NavSelected, LogoutRed, ErrorRed) ──
// ── Todos vienen ahora de LocalAppColors.current dentro de cada Composable. ──

@Composable
fun ProfileScreen(
    onEdit: () -> Unit            = {},
    onLogout: () -> Unit          = {},
    onNavInicio: () -> Unit       = {},
    onNavSenas: () -> Unit        = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit  = {},
    onNavPreferences: () -> Unit  = {},
) {
    val colors = LocalAppColors.current   // 👈 nueva línea

    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(session))
    val uiState by vm.uiState.collectAsState()

    when (val state = uiState) {
        is ProfileUiState.Loading -> ProfileScreenSkeleton()

        is ProfileUiState.Error -> {
            Box(Modifier.fillMaxSize().background(colors.background), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(state.message, color = colors.errorColor, fontSize = 14.sp)
                    Button(onClick = { vm.loadProfile() }, colors = ButtonDefaults.buttonColors(containerColor = colors.accent)) {
                        Text("Reintentar", color = colors.onAccent)
                    }
                }
            }
        }

        is ProfileUiState.Success -> {
            val profile = state.profile
            val initials = profile.name.split(" ").take(2).joinToString("") { it.first().uppercase() }

            Scaffold(
                containerColor = colors.background,
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
                    // Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Text("Perfil", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                            Text("Tu cuenta y preferencias", fontSize = 13.sp, color = colors.subtle)
                        }
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(colors.surface).clickable { onEdit() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Edit, "Editar", tint = colors.onBackground, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // User card
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(colors.iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(profile.name,  fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.onBackground)
                            Text(profile.email, fontSize = 12.sp, color = colors.subtle)
                        }
                    }

                    Spacer(Modifier.height(12.dp))


                    // Stats — Señas/Dispositivos desde GET /api/gestures y /api/devices,
                    // kWh desde GET /api/analytics/consumption/summary (mes en curso)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            state.gestureCount.toString() to "Señas",
                            state.deviceCount.toString() to "Dispositivos",
                            String.format("%.1f", state.totalKwh) to "kWh (mes)"
                        ).forEach { (v, l) ->
                            Column(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(colors.surface).padding(vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(v, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                                Text(l, fontSize = 11.sp, color = colors.subtle)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // CUENTA
                    SectionLabel("CUENTA")
                    Spacer(Modifier.height(8.dp))
                    SettingsCard {
                        SettingsRow(Icons.Outlined.AccountCircle, "Editar perfil", onClick = onEdit)
                    }

                    Spacer(Modifier.height(20.dp))

                    // PREFERENCIAS
                    SectionLabel("PREFERENCIAS")
                    Spacer(Modifier.height(8.dp))
                    SettingsCard {
                        SettingsRow(Icons.Outlined.DarkMode, "Tema", trailing = "Cálido · Claro", onClick = onNavPreferences)
                    }

                    Spacer(Modifier.height(20.dp))

                    // Logout
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surface)
                            .clickable {
                                session.clearSession()
                                onLogout()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Logout, null, tint = colors.errorColor, modifier = Modifier.size(18.dp))
                            Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.errorColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val colors = LocalAppColors.current
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = colors.subtle)
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface), content = content)
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, trailing: String? = null, onClick: () -> Unit = {}) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(colors.iconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = colors.onBackground, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = colors.onBackground, modifier = Modifier.weight(1f))
        if (trailing != null) { Text(trailing, fontSize = 13.sp, color = colors.subtle); Spacer(Modifier.width(6.dp)) }
        Icon(Icons.Outlined.ChevronRight, null, tint = colors.subtle, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ProfileBottomNavBar(
    selected: String,
    onNavInicio: () -> Unit, onNavSenas: () -> Unit,
    onNavDispositivos: () -> Unit, onNavSugerencias: () -> Unit, onNavPerfil: () -> Unit,
) {
    val colors = LocalAppColors.current
    val items = listOf(
        Triple("Inicio", Icons.Outlined.Home, onNavInicio),
        Triple("Señas", Icons.Outlined.PanTool, onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle, onNavDispositivos),
        Triple("Sugerencias", Icons.Outlined.AutoAwesome, onNavSugerencias),
        Triple("Perfil", Icons.Outlined.Person, onNavPerfil),
    )
    Surface(color = colors.background, tonalElevation = 0.dp) {
        HorizontalDivider(color = colors.borderColor, thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            items.forEach { (label, icon, action) ->
                val isSel = label == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { action() }
                        .background(if (isSel) colors.surfaceCard else Color.Transparent).padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(icon, label, tint = if (isSel) colors.onBackground else colors.subtle, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, fontSize = 10.sp, color = if (isSel) colors.onBackground else colors.subtle, fontWeight = if (isSel) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ProfileScreenPreview() { ProfileScreen() }