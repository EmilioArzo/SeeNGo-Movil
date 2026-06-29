package com.emilionavarro.prueba.autenticacion

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens (same palette as LoginScreen) ───────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFD4D0C8)

// Password strength colors
private val StrengthWeak   = Color(0xFFD94F3D)
private val StrengthFair   = Color(0xFFE8A838)
private val StrengthGood   = Color(0xFF4A9B6F)
private val StrengthStrong = Color(0xFF2D7A50)

// ── Password strength helper ─────────────────────────────────────────────────
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

private fun strengthColor(strength: PasswordStrength) = when (strength) {
    PasswordStrength.WEAK   -> StrengthWeak
    PasswordStrength.FAIR   -> StrengthFair
    PasswordStrength.GOOD   -> StrengthGood
    PasswordStrength.STRONG -> StrengthStrong
    else                    -> BorderColor
}

// ── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onLogin: () -> Unit = {},
    onTerms: () -> Unit = {},
    onPrivacy: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }

    val strength = evaluateStrength(password)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // ── Back arrow ────────────────────────────────────────────────
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = (-8).dp)
            ) {
                Icon(
                    Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = OnBackground,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Headline ──────────────────────────────────────────────────
            Text(
                text = "Crear cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                lineHeight = 36.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Empieza con See n go en 1 minuto.",
                fontSize = 15.sp,
                color = Subtle
            )

            Spacer(Modifier.height(28.dp))

            // ── Name field ────────────────────────────────────────────────
            FieldLabel("NOMBRE")
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = Subtle)
                },
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(16.dp))

            // ── Email field ───────────────────────────────────────────────
            FieldLabel("CORREO")
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = Subtle)
                },
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // ── Password field ────────────────────────────────────────────
            FieldLabel("CONTRASEÑA")
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = Subtle)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility
                            else Icons.Outlined.VisibilityOff,
                            contentDescription = "Mostrar contraseña",
                            tint = Subtle
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(10.dp))

            // ── Password strength bar ─────────────────────────────────────
            if (strength != PasswordStrength.EMPTY) {
                PasswordStrengthBar(strength = strength)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = strength.label,
                    fontSize = 12.sp,
                    color = strengthColor(strength)
                )
                Spacer(Modifier.height(16.dp))
            } else {
                Spacer(Modifier.height(8.dp))
            }

            // ── Terms & Privacy ───────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    modifier = Modifier.size(24.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Accent,
                        uncheckedColor = BorderColor
                    )
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) {
                            append("Acepto los ")
                        }
                        withStyle(
                            SpanStyle(
                                color = OnBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.None
                            )
                        ) {
                            append("Términos")
                        }
                        withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) {
                            append(" y la ")
                        }
                        withStyle(
                            SpanStyle(
                                color = OnBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Política de privacidad")
                        }
                        withStyle(SpanStyle(color = OnBackground, fontSize = 14.sp)) {
                            append(".")
                        }
                    },
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Primary CTA ───────────────────────────────────────────────
            Button(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = termsAccepted
            ) {
                Text(
                    "Crear cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // ── Footer ────────────────────────────────────────────────────────
        TextButton(
            onClick = onLogin,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Subtle, fontSize = 14.sp)) {
                        append("¿Ya tienes cuenta? ")
                    }
                    withStyle(
                        SpanStyle(
                            color = OnBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Iniciar sesión")
                    }
                }
            )
        }
    }
}

// ── Password strength bar ─────────────────────────────────────────────────────
@Composable
private fun PasswordStrengthBar(strength: PasswordStrength) {
    val activeColor = strengthColor(strength)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .background(
                        color = if (index < strength.segments) activeColor else BorderColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────
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
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = Surface,
    unfocusedContainerColor = Surface,
    focusedBorderColor      = OnBackground,
    unfocusedBorderColor    = BorderColor,
    focusedTextColor        = OnBackground,
    unfocusedTextColor      = OnBackground,
    cursorColor             = OnBackground
)

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}

