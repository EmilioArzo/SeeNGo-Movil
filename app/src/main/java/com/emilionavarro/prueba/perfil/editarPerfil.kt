package com.emilionavarro.prueba.perfil



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFD4D0C8)
private val AvatarBg     = Color(0xFFDDDAD3)
private val IconBg       = Color(0xFFE4E1D9)

// ── Data ──────────────────────────────────────────────────────────────────────
data class HomeRoom(val name: String, val deviceCount: Int)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun EditProfileScreen(
    initialName: String     = "María Restrepo",
    initialEmail: String    = "maria.restrepo@correo.com",
    initialPhone: String    = "+57 300 123 4567",
    initialLocation: String = "Medellín, Colombia",
    initialRooms: List<HomeRoom> = listOf(
        HomeRoom("Sala",       3),
        HomeRoom("Habitación", 2),
        HomeRoom("Cocina",     0),
    ),
    onBack: () -> Unit = {},
    onSave: (name: String, email: String, phone: String, location: String, rooms: List<HomeRoom>) -> Unit = { _, _, _, _, _ -> },
    onChangePhoto: () -> Unit = {},
) {
    var name     by remember { mutableStateOf(initialName) }
    var email    by remember { mutableStateOf(initialEmail) }
    var phone    by remember { mutableStateOf(initialPhone) }
    var location by remember { mutableStateOf(initialLocation) }
    val rooms    = remember { mutableStateListOf(*initialRooms.toTypedArray()) }

    val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
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
                Text(
                    "Editar perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Avatar ────────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(AvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                }
                // Camera badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Accent)
                        .clickable { onChangePhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── NOMBRE ────────────────────────────────────────────────────
            FieldLabel("NOMBRE")
            Spacer(Modifier.height(6.dp))
            StyledTextField(
                value = name,
                onValueChange = { name = it },
                leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Subtle) }
            )

            Spacer(Modifier.height(14.dp))

            // ── CORREO ────────────────────────────────────────────────────
            FieldLabel("CORREO")
            Spacer(Modifier.height(6.dp))
            StyledTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Subtle) },
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(14.dp))

            // ── TELÉFONO ──────────────────────────────────────────────────
            FieldLabel("TELÉFONO")
            Spacer(Modifier.height(6.dp))
            StyledTextField(
                value = phone,
                onValueChange = { phone = it },
                leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = Subtle) },
                keyboardType = KeyboardType.Phone
            )

            Spacer(Modifier.height(14.dp))

            // ── UBICACIÓN DEL HOGAR ───────────────────────────────────────
            FieldLabel("UBICACIÓN DEL HOGAR")
            Spacer(Modifier.height(6.dp))
            StyledTextField(
                value = location,
                onValueChange = { location = it },
                leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = Subtle) }
            )

            Spacer(Modifier.height(6.dp))

            // ── CUARTOS DEL HOGAR ─────────────────────────────────────────
            FieldLabel("CUARTOS DEL HOGAR")
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
            ) {
                rooms.forEachIndexed { index, room ->
                    RoomRow(
                        room    = room,
                        onEdit  = { /* open room editor */ }
                    )
                    if (index < rooms.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = BorderColor,
                            thickness = 0.5.dp
                        )
                    }
                }

                // Divider before add
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = BorderColor,
                    thickness = 0.5.dp
                )

                // Add room row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { rooms.add(HomeRoom("Nuevo cuarto", 0)) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Agregar cuarto",
                        tint = Subtle,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Agregar cuarto",
                        fontSize = 14.sp,
                        color = Subtle
                    )
                }
            }
        }

        // ── Bottom CTA ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { onSave(name, email, phone, location, rooms.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(
                    "Guardar cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Room row ──────────────────────────────────────────────────────────────────
@Composable
private fun RoomRow(room: HomeRoom, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Home,
            contentDescription = null,
            tint = Subtle,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            room.name,
            fontSize = 14.sp,
            color = OnBackground,
            modifier = Modifier.weight(1f)
        )
        Text(
            "${room.deviceCount} disp.",
            fontSize = 12.sp,
            color = Subtle
        )
        Spacer(Modifier.width(10.dp))
        Icon(
            Icons.Outlined.Edit,
            contentDescription = "Editar cuarto",
            tint = Subtle,
            modifier = Modifier
                .size(16.dp)
                .clickable { onEdit() }
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
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

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor    = Surface,
            unfocusedContainerColor  = Surface,
            focusedBorderColor       = OnBackground,
            unfocusedBorderColor     = BorderColor,
            focusedTextColor         = OnBackground,
            unfocusedTextColor       = OnBackground,
            cursorColor              = OnBackground
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}