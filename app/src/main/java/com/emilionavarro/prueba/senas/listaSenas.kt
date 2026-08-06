package com.emilionavarro.prueba.senas

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emilionavarro.prueba.senas.data.network.GestureDto
import com.emilionavarro.prueba.senas.viewmodel.GesturesViewModel
import com.emilionavarro.prueba.ui.theme.LocalAppColors

// ── Ya no hay colores fijos aquí: Background, Surface, OnBackground, ────────
// ── Subtle, Accent, BorderColor, GestureIconBg y NavSelected vienen ─────────
// ── todos de LocalAppColors. ─────────────────────────────────────────────────

// ── UI model ──────────────────────────────────────────────────────────────────
data class GestureListItem(
    val id: String,
    val name: String,
    val actionSummary: String,
    val hasLinkedDevice: Boolean,
)

private fun GestureDto.toListItem(): GestureListItem = GestureListItem(
    id = id.orEmpty(),
    name = name,
    actionSummary = if (!linkedAction.isNullOrBlank()) "→ $linkedAction" else "Sin acción vinculada",
    hasLinkedDevice = !linkedDeviceId.isNullOrBlank()
)

// ── Screen (con estado / conectada al backend) ─────────────────────────────────
@Composable
fun GesturesScreen(
    userId: String,
    viewModel: GesturesViewModel,
    onAddGesture: () -> Unit = {},
    onGestureClick: (GestureListItem) -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
) {
    val state = viewModel.uiState

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) viewModel.loadGestures(userId)
    }

    GesturesScreenContent(
        isLoading    = state.isLoading,
        errorMessage = state.error,
        gestures     = state.gestures.map { it.toListItem() },
        onAddGesture = onAddGesture,
        onGestureClick = onGestureClick,
        onNavInicio       = onNavInicio,
        onNavSenas        = {},
        onNavDispositivos = onNavDispositivos,
        onNavSugerencias  = onNavSugerencias,
        onNavPerfil       = onNavPerfil,
    )
}

// ── Screen (sin estado / puramente visual, usada por el Preview) ──────────────
@Composable
fun GesturesScreenContent(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    gestures: List<GestureListItem> = emptyList(),
    onAddGesture: () -> Unit = {},
    onGestureClick: (GestureListItem) -> Unit = {},
    onNavInicio: () -> Unit = {},
    onNavSenas: () -> Unit = {},
    onNavDispositivos: () -> Unit = {},
    onNavSugerencias: () -> Unit = {},
    onNavPerfil: () -> Unit = {},
) {
    val colors = LocalAppColors.current   // 👈 nueva línea
    var searchQuery by remember { mutableStateOf("") }

    val filtered = gestures.filter { g ->
        searchQuery.isEmpty() ||
                g.name.contains(searchQuery, ignoreCase = true) ||
                g.actionSummary.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = colors.background,
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
                        color = colors.onBackground
                    )
                    Text(
                        "${gestures.size} señas guardadas",
                        fontSize = 13.sp,
                        color = colors.subtle
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(colors.accent, RoundedCornerShape(14.dp))
                        .clickable { onAddGesture() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Agregar seña", tint = colors.onAccent)
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
                    Text("Buscar seña o acción", fontSize = 14.sp, color = colors.subtle)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = colors.subtle)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = colors.surface,
                    unfocusedContainerColor  = colors.surface,
                    focusedBorderColor       = colors.onBackground,
                    unfocusedBorderColor     = colors.borderColor,
                    focusedTextColor         = colors.onBackground,
                    unfocusedTextColor       = colors.onBackground,
                    cursorColor              = colors.onBackground
                )
            )

            Spacer(Modifier.height(14.dp))

            when {
                isLoading -> {
                    Box(Modifier.fillMaxWidth().padding(vertical = 50.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.accent)
                    }
                }

                errorMessage != null -> {
                    Text(
                        "No se pudieron cargar tus señas: $errorMessage",
                        color = colors.subtle,
                        fontSize = 13.sp
                    )
                }

                filtered.isEmpty() -> {
                    Text(
                        if (gestures.isEmpty()) "Aún no tienes señas guardadas."
                        else "No encontramos señas con ese criterio.",
                        color = colors.subtle,
                        fontSize = 13.sp
                    )
                }

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { gesture ->
                            GestureCard(
                                gesture = gesture,
                                onClick = { onGestureClick(gesture) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Gesture card ──────────────────────────────────────────────────────────────
@Composable
private fun GestureCard(gesture: GestureListItem, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PanTool,
                contentDescription = null,
                tint = colors.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                gesture.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                gesture.actionSummary,
                fontSize = 12.sp,
                color = colors.subtle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!gesture.hasLinkedDevice) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Sin dispositivo vinculado",
                    fontSize = 11.sp,
                    color = colors.errorColor
                )
            }
        }

        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = colors.subtle,
            modifier = Modifier.size(18.dp)
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
    val colors = LocalAppColors.current
    val items = listOf(
        Triple("Inicio",       Icons.Outlined.Home,            onNavInicio),
        Triple("Señas",        Icons.Outlined.PanTool,         onNavSenas),
        Triple("Dispositivos", Icons.Outlined.LightbulbCircle, onNavDispositivos),
        Triple("Sugerencias",  Icons.Outlined.AutoAwesome,     onNavSugerencias),
        Triple("Perfil",       Icons.Outlined.Person,          onNavPerfil),
    )
    Surface(color = colors.background, tonalElevation = 0.dp) {
        HorizontalDivider(color = colors.borderColor, thickness = 0.5.dp)
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
                        .background(if (isSel) colors.iconBg else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(icon, contentDescription = label, tint = if (isSel) colors.onBackground else colors.subtle, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, fontSize = 10.sp, color = if (isSel) colors.onBackground else colors.subtle, fontWeight = if (isSel) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GesturesScreenPreview() {
    GesturesScreenContent(
        gestures = listOf(
            GestureListItem("1", "Mano abierta", "→ Reproducir música", true),
            GestureListItem("2", "Puño cerrado", "→ Apagar todo", true),
            GestureListItem("3", "Paz", "Sin acción vinculada", false),
        )
    )
}