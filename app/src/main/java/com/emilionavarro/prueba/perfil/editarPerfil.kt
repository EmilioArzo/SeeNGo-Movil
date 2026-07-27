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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.inicio.GenericDetailSkeleton
import com.emilionavarro.prueba.perfil.viewmodel.EditProfileUiState
import com.emilionavarro.prueba.perfil.viewmodel.EditProfileViewModel
import com.emilionavarro.prueba.perfil.viewmodel.EditProfileViewModelFactory


private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFD4D0C8)
private val AvatarBg     = Color(0xFFDDDAD3)
private val ErrorRed     = Color(0xFFD94F3D)

data class HomeRoom(
    val name: String,
    val deviceCount: Int
)

@Composable
fun EditProfileScreen(
    initialName: String     = "",
    initialEmail: String    = "",
    initialPhone: String    = "",
    initialLocation: String = "",
    initialRooms: List<HomeRoom> = emptyList(),
    onBack: () -> Unit      = {},
    onSave: (String, String, String, String, List<HomeRoom>) -> Unit = { _, _, _, _, _ -> },
    onChangePhoto: () -> Unit = {},
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val vm: EditProfileViewModel = viewModel(factory = EditProfileViewModelFactory(session))
    val uiState by vm.uiState.collectAsState()

    // Pre-fill with session data if initialName is blank
    var name     by remember { mutableStateOf(initialName.ifBlank { session.getUserName() ?: "" }) }
    var email    by remember { mutableStateOf(initialEmail.ifBlank { session.getUserEmail() ?: "" }) }
    var phone    by remember { mutableStateOf(initialPhone) }
    var location by remember { mutableStateOf(initialLocation) }
    val rooms    = remember { mutableStateListOf(*initialRooms.toTypedArray()) }

    val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

    // Navigate back on success
    LaunchedEffect(uiState) {
        if (uiState is EditProfileUiState.Success) {
            vm.resetState()
            onBack()
        }
    }

    if (uiState is EditProfileUiState.Loading) {
        GenericDetailSkeleton()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 100.dp)
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
                Text("Editar perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
            }

            Spacer(Modifier.height(28.dp))

            // Avatar
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(
                    modifier = Modifier.size(88.dp).clip(RoundedCornerShape(24.dp)).background(AvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                }
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(Accent).clickable { onChangePhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            // Error banner
            if (uiState is EditProfileUiState.Error) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).padding(14.dp)
                ) {
                    Text((uiState as EditProfileUiState.Error).message, fontSize = 13.sp, color = ErrorRed)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Fields
            FieldLabel("NOMBRE")
            Spacer(Modifier.height(6.dp))
            StyledTextField(name, { name = it; if (uiState is EditProfileUiState.Error) vm.resetState() }, { Icon(Icons.Outlined.Person, null, tint = Subtle) })

            Spacer(Modifier.height(14.dp))

            FieldLabel("CORREO")
            Spacer(Modifier.height(6.dp))
            StyledTextField(email, { email = it; if (uiState is EditProfileUiState.Error) vm.resetState() }, { Icon(Icons.Outlined.Email, null, tint = Subtle) }, KeyboardType.Email)

            Spacer(Modifier.height(14.dp))

            FieldLabel("TELÉFONO")
            Spacer(Modifier.height(6.dp))
            StyledTextField(phone, { phone = it }, { Icon(Icons.Outlined.Phone, null, tint = Subtle) }, KeyboardType.Phone)

            Spacer(Modifier.height(14.dp))

            FieldLabel("UBICACIÓN DEL HOGAR")
            Spacer(Modifier.height(6.dp))
            StyledTextField(location, { location = it }, { Icon(Icons.Outlined.LocationOn, null, tint = Subtle) })

            Spacer(Modifier.height(6.dp))

            FieldLabel("CUARTOS DEL HOGAR")
            Spacer(Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface)) {
                rooms.forEachIndexed { index, room ->
                    RoomRow(room = room, onEdit = {})
                    if (index < rooms.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = BorderColor, thickness = 0.5.dp)
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = BorderColor, thickness = 0.5.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { rooms.add(HomeRoom("Nuevo cuarto", 0)) }.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Outlined.Add, null, tint = Subtle, modifier = Modifier.size(18.dp))
                    Text("Agregar cuarto", fontSize = 14.sp, color = Subtle)
                }
            }
        }

        // Save button
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Background).padding(horizontal = 24.dp, vertical = 16.dp)) {
            Button(
                onClick  = { vm.saveProfile(name, email, phone) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled  = uiState !is EditProfileUiState.Loading
            ) {
                Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun RoomRow(room: HomeRoom, onEdit: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Home, null, tint = Subtle, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(room.name, fontSize = 14.sp, color = OnBackground, modifier = Modifier.weight(1f))
        Text("${room.deviceCount} disp.", fontSize = 12.sp, color = Subtle)
        Spacer(Modifier.width(10.dp))
        Icon(Icons.Outlined.Edit, null, tint = Subtle, modifier = Modifier.size(16.dp).clickable { onEdit() })
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
}

@Composable
private fun StyledTextField(
    value: String, onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Surface, unfocusedContainerColor = Surface,
            focusedBorderColor = OnBackground, unfocusedBorderColor = BorderColor,
            focusedTextColor = OnBackground, unfocusedTextColor = OnBackground, cursorColor = OnBackground
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun EditProfileScreenPreview() { EditProfileScreen() }