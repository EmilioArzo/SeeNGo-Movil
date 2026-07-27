package com.emilionavarro.prueba.autenticacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.viewmodel.RegisterViewModelFactory
import com.emilionavarro.prueba.autenticacion.viewmodel.RegisterUiState
import com.emilionavarro.prueba.autenticacion.viewmodel.RegisterViewModel
import com.emilionavarro.prueba.inicio.GenericDetailSkeleton

private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFD4D0C8)
private val StrengthWeak   = Color(0xFFD94F3D)
private val StrengthFair   = Color(0xFFE8A838)
private val StrengthGood   = Color(0xFF4A9B6F)
private val StrengthStrong = Color(0xFF2D7A50)
private val ErrorRed       = Color(0xFFD94F3D)

private enum class PasswordStrength(val label: String, val segments: Int) {
    EMPTY("", 0),
    WEAK("Contraseña débil · 10 caracteres mín.", 1),
    FAIR("Contraseña regular · 10 caracteres mín.", 2),
    GOOD("Contraseña buena", 3),
    STRONG("Contraseña fuerte · 10 caracteres mín.", 4)
}

private fun evaluateStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.EMPTY
    var score = 0
    if (password.length >= 10) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return when (score) {
        0, 1 -> PasswordStrength.WEAK
        2    -> PasswordStrength.FAIR
        3    -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

private fun strengthColor(s: PasswordStrength) = when (s) {
    PasswordStrength.WEAK   -> StrengthWeak
    PasswordStrength.FAIR   -> StrengthFair
    PasswordStrength.GOOD   -> StrengthGood
    PasswordStrength.STRONG -> StrengthStrong
    else                    -> Color(0xFFD4D0C8)
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit          = {},
    onCreateAccount: () -> Unit = {},
    onLogin: () -> Unit         = {},
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val vm: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(session))
    val uiState by vm.uiState.collectAsState()

    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted   by remember { mutableStateOf(false) }

    val strength = evaluateStrength(password)

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            vm.resetState()
            onCreateAccount()
        }
    }

    if (uiState is RegisterUiState.Loading) {
        GenericDetailSkeleton()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBackIosNew, "Volver", tint = OnBackground, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Crear cuenta", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text("Empieza con See n go en 1 minuto.", fontSize = 15.sp, color = Subtle)
        Spacer(Modifier.height(24.dp))

        // Error banner
        if (uiState is RegisterUiState.Error) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text((uiState as RegisterUiState.Error).message, fontSize = 13.sp, color = ErrorRed)
            }
            Spacer(Modifier.height(16.dp))
        }

        // Name
        FieldLabel("NOMBRE")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it; if (uiState is RegisterUiState.Error) vm.resetState() },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Subtle) },
            shape = RoundedCornerShape(14.dp), colors = fieldColors()
        )

        Spacer(Modifier.height(14.dp))

        // Email
        FieldLabel("CORREO")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it; if (uiState is RegisterUiState.Error) vm.resetState() },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Subtle) },
            shape = RoundedCornerShape(14.dp), colors = fieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(14.dp))

        // Password
        FieldLabel("CONTRASEÑA")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; if (uiState is RegisterUiState.Error) vm.resetState() },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            leadingIcon  = { Icon(Icons.Outlined.Lock, null, tint = Subtle) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Subtle)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp), colors = fieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Strength bar
        if (strength != PasswordStrength.EMPTY) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(4) { i ->
                    Box(modifier = Modifier.weight(1f).height(3.dp).background(
                        if (i < strength.segments) strengthColor(strength) else Color(0xFFD4D0C8),
                        RoundedCornerShape(2.dp)
                    ))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(strength.label, fontSize = 12.sp, color = strengthColor(strength))
        }

        Spacer(Modifier.height(16.dp))

        // Terms
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = termsAccepted, onCheckedChange = { termsAccepted = it },
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(checkedColor = Accent, uncheckedColor = Color(0xFFD4D0C8))
            )
            Spacer(Modifier.width(10.dp))
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) { append("Acepto los ") }
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)) { append("Términos") }
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) { append(" y la ") }
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)) { append("Política de privacidad") }
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) { append(".") }
            }, lineHeight = 20.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.register(name, email, password) },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            enabled = termsAccepted && uiState !is RegisterUiState.Loading
        ) {
            Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = Subtle, fontSize = 14.sp)) { append("¿Ya tienes cuenta? ") }
                withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)) { append("Iniciar sesión") }
            })
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, color = Subtle)
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Surface, unfocusedContainerColor = Surface,
    focusedBorderColor = OnBackground, unfocusedBorderColor = BorderColor,
    focusedTextColor = OnBackground, unfocusedTextColor = OnBackground, cursorColor = OnBackground
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RegisterScreenPreview() { RegisterScreen() }