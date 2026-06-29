package com.emilionavarro.prueba.autenticacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

// ── Color tokens ────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)   // warm off-white
private val Surface      = Color(0xFFF2EFEA)   // slightly lighter card/field
private val OnBackground = Color(0xFF1C1C1C)   // near-black text
private val Subtle       = Color(0xFF7A7A7A)   // secondary / caption text
private val Accent       = Color(0xFF232320)   // dark button fill
private val BorderColor  = Color(0xFFD4D0C8)   // field border
private val CheckGreen   = Color(0xFF232320)   // checkbox tint (same as accent)

@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // ── App icon ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(OnBackground, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✋", fontSize = 28.sp) // replace with your real icon
            }

            Spacer(Modifier.height(28.dp))

            // ── Headline ──────────────────────────────────────────────────
            Text(
                text = "Bienvenido de vuelta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Entra para controlar tu hogar.",
                fontSize = 15.sp,
                color = Subtle
            )

            Spacer(Modifier.height(32.dp))

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

            Spacer(Modifier.height(16.dp))

            // ── Remember me + Forgot password ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CheckGreen,
                            uncheckedColor = BorderColor
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Recordarme", fontSize = 14.sp, color = OnBackground)
                }
                TextButton(onClick = onForgotPassword) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        fontSize = 14.sp,
                        color = Subtle
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Primary CTA ───────────────────────────────────────────────
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(
                    "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Divider "o continúa con" ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderColor
                )
                Text(
                    "  o continúa con  ",
                    fontSize = 13.sp,
                    color = Subtle
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderColor
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Social buttons ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialButton(
                    label = "Google",
                    onClick = onGoogleLogin,
                    modifier = Modifier.weight(1f)
                )
                SocialButton(
                    label = "Apple",
                    onClick = onAppleLogin,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Footer ────────────────────────────────────────────────────────
        TextButton(
            onClick = onCreateAccount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Subtle, fontSize = 14.sp)) {
                        append("¿Aún no tienes cuenta? ")
                    }
                    withStyle(
                        SpanStyle(
                            color = OnBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.None
                        )
                    ) {
                        append("Crear cuenta")
                    }
                }
            )
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

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
private fun SocialButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = OnBackground
        )
    ) {
        Text(
            text = if (label == "Google") "G  $label" else "  $label",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = OnBackground
        )
    }
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
fun LoginScreenPreview() {
    LoginScreen()
}