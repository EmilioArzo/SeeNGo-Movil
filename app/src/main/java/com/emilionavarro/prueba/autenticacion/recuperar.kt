package com.emilionavarro.prueba.autenticacion


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
private val InfoBg       = Color(0xFFE8E5DE)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSend: (email: String) -> Unit = {},
) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
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
                "Recuperar contraseña",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Mail icon ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Surface),
            contentAlignment = Alignment.Center
        ) {
            // Envelope with arrow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.KeyboardReturn,
                    contentDescription = null,
                    tint = OnBackground,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(1.dp))
                Icon(
                    Icons.Outlined.Email,
                    contentDescription = null,
                    tint = OnBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Headline ──────────────────────────────────────────────────────
        Text(
            "Te enviamos un enlace",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = OnBackground,
            lineHeight = 32.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Escribe el correo asociado a tu cuenta y te enviaremos instrucciones para restablecer tu contraseña.",
            fontSize = 14.sp,
            color = Subtle,
            lineHeight = 21.sp
        )

        Spacer(Modifier.height(28.dp))

        // ── Email field ───────────────────────────────────────────────────
        Text(
            "CORREO",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = Subtle
        )
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor    = Surface,
                unfocusedContainerColor  = Surface,
                focusedBorderColor       = OnBackground,
                unfocusedBorderColor     = BorderColor,
                focusedTextColor         = OnBackground,
                unfocusedTextColor       = OnBackground,
                cursorColor              = OnBackground
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        // ── Send button ───────────────────────────────────────────────────
        Button(
            onClick = { onSend(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            enabled = email.isNotBlank()
        ) {
            Text(
                "Enviar enlace",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Info notice ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InfoBg)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = Subtle,
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 1.dp)
            )
            Text(
                "El enlace expira en 30 min. Revisa también tu carpeta de spam.",
                fontSize = 13.sp,
                color = Subtle,
                lineHeight = 19.sp
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}