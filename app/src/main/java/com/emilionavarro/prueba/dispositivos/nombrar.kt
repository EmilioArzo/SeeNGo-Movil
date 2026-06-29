package com.emilionavarro.prueba.dispositivos


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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background      = Color(0xFFEAE7E0)
private val Surface         = Color(0xFFF2EFEA)
private val OnBackground    = Color(0xFF1C1C1C)
private val Subtle          = Color(0xFF7A7A7A)
private val Accent          = Color(0xFF232320)
private val BorderColor     = Color(0xFFDDDAD3)
private val ProgressActive  = Color(0xFF232320)
private val ProgressInact   = Color(0xFFD4D0C8)
private val ChipSelected    = Color(0xFF232320)
private val ChipUnselected  = Color(0xFFF2EFEA)
private val IconBgSelected  = Color(0xFF232320)
private val IconBgUnselected= Color(0xFFE4E1D9)
private val DeviceIconBg    = Color(0xFFE4E1D9)

// ── Icon options ──────────────────────────────────────────────────────────────
data class IconOption(val icon: ImageVector, val label: String)

private val iconOptions = listOf(
    IconOption(Icons.Outlined.LightMode,    "lámpara"),
    IconOption(Icons.Outlined.Tungsten,     "mesa"),
    IconOption(Icons.Outlined.Tv,           "tv"),
    IconOption(Icons.Outlined.SettingsRemote, "control"),
    IconOption(Icons.Outlined.Power,        "enchufe"),
    IconOption(Icons.Outlined.Add,          "otro"),
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun ConfigureDeviceScreen(
    currentStep: Int = 3,
    totalSteps: Int = 3,
    deviceRawName: String = "shellyplug-S-3F2A",
    availableRooms: List<String> = listOf("Sala", "Habitación", "Cocina", "Estudio"),
    onBack: () -> Unit = {},
    onSave: (name: String, room: String, iconLabel: String) -> Unit = { _, _, _ -> },
    onAddRoom: () -> Unit = {},
) {
    var deviceName   by remember { mutableStateOf("Lámpara de pie") }
    var selectedRoom by remember { mutableStateOf(availableRooms.firstOrNull() ?: "") }
    var selectedIcon by remember { mutableStateOf(iconOptions.first().label) }

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
                .padding(top = 24.dp, bottom = 100.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
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
                Column {
                    Text(
                        "Casi listo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "Paso $currentStep de $totalSteps · Nombre",
                        fontSize = 12.sp,
                        color = Subtle
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Progress bars ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (index < currentStep) ProgressActive else ProgressInact)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Device preview icon ───────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(DeviceIconBg, RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val currentIcon = iconOptions.find { it.label == selectedIcon }?.icon
                        ?: Icons.Outlined.LightMode
                    Icon(
                        currentIcon,
                        contentDescription = null,
                        tint = OnBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    deviceRawName,
                    fontSize = 11.sp,
                    color = Subtle,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Name field ────────────────────────────────────────────────
            FieldLabel("NOMBRE")
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Label, contentDescription = null, tint = Subtle)
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

            Spacer(Modifier.height(20.dp))

            // ── Room selector ─────────────────────────────────────────────
            FieldLabel("CUARTO")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableRooms.forEach { room ->
                    val isSelected = room == selectedRoom
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ChipSelected else ChipUnselected)
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = BorderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedRoom = room }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            room,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else OnBackground
                        )
                    }
                }

                // + Nuevo chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ChipUnselected)
                        .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                        .clickable { onAddRoom() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+ Nuevo",
                        fontSize = 13.sp,
                        color = Subtle
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Icon selector ─────────────────────────────────────────────
            FieldLabel("ÍCONO")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                iconOptions.forEach { option ->
                    val isSelected = option.label == selectedIcon
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) IconBgSelected else IconBgUnselected
                            )
                            .clickable { selectedIcon = option.label },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            option.icon,
                            contentDescription = option.label,
                            tint = if (isSelected) Color.White else OnBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // ── Bottom CTA ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { onSave(deviceName, selectedRoom, selectedIcon) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = deviceName.isNotBlank() && selectedRoom.isNotBlank()
            ) {
                Text(
                    "Guardar dispositivo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Helper ────────────────────────────────────────────────────────────────────
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = Subtle
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConfigureDeviceScreenPreview() {
    ConfigureDeviceScreen()
}