package com.emilionavarro.prueba.autenticacion


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background   = Color(0xFFEAE7E0)
private val Surface      = Color(0xFFF2EFEA)
private val SurfaceCard  = Color(0xFFE8E5DE)
private val OnBackground = Color(0xFF1C1C1C)
private val Subtle       = Color(0xFF7A7A7A)
private val Accent       = Color(0xFF232320)
private val BorderColor  = Color(0xFFD4D0C8)
private val CheckGreen   = Color(0xFF4A8C62)
private val ErrorRed     = Color(0xFFD94F3D)

// ── Password rules ────────────────────────────────────────────────────────────
data class PasswordRule(val label: String, val check: (String) -> Boolean)

private val rules = listOf(
    PasswordRule("Al menos 10 caracteres")          { it.length >= 10 },
    PasswordRule("Letras mayúsculas y minúsculas")  { it.any { c -> c.isUpperCase() } && it.any { c -> c.isLowerCase() } },
    PasswordRule("Un número")                       { it.any { c -> c.isDigit() } },
    PasswordRule("Un carácter especial")            { it.any { c -> !c.isLetterOrDigit() } },
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun NewPasswordScreen(
    onBack: () -> Unit = {},
    onSubmit: (password: String) -> Unit = {},
) {
    var password        by remember { mutableStateOf("") }
    var confirm         by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }

    val allRulesMet    = rules.all { it.check(password) }
    val passwordsMatch = password == confirm && confirm.isNotEmpty()
    val canSubmit      = allRulesMet && passwordsMatch

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
                "Nueva contraseña",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Headline ──────────────────────────────────────────────────────
        Text(
            "Crea una contraseña nueva",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = OnBackground,
            lineHeight = 32.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Debe tener al menos 10 caracteres.",
            fontSize = 14.sp,
            color = Subtle
        )

        Spacer(Modifier.height(28.dp))

        // ── New password ──────────────────────────────────────────────────
        FieldLabel("NUEVA CONTRASEÑA")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon  = { Icon(Icons.Outlined.Lock, null, tint = Subtle) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Ver contraseña",
                        tint = Subtle
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            shape  = RoundedCornerShape(14.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(14.dp))

        // ── Confirm password ──────────────────────────────────────────────
        FieldLabel("CONFIRMAR")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon  = { Icon(Icons.Outlined.LockOpen, null, tint = Subtle) },
            trailingIcon = {
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(
                        if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Ver confirmación",
                        tint = Subtle
                    )
                }
            },
            visualTransformation = if (confirmVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            shape  = RoundedCornerShape(14.dp),
            colors = fieldColors(
                borderOverride = if (confirm.isNotEmpty() && !passwordsMatch) ErrorRed else null
            )
        )

        Spacer(Modifier.height(16.dp))

        // ── Rules checklist ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "TU CONTRASEÑA TIENE:",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = OnBackground
            )
            rules.forEach { rule ->
                val met = rule.check(password)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (met) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(CheckGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    } else {
                        Icon(
                            Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = Subtle,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        rule.label,
                        fontSize = 13.sp,
                        color = if (met) OnBackground else Subtle
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Submit ────────────────────────────────────────────────────────
        Button(
            onClick = { onSubmit(password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = Accent,
                disabledContainerColor = Accent.copy(alpha = 0.4f)
            ),
            enabled = canSubmit
        ) {
            Text(
                "Cambiar contraseña",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
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
private fun fieldColors(borderOverride: Color? = null) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor    = Surface,
    unfocusedContainerColor  = Surface,
    focusedBorderColor       = borderOverride ?: OnBackground,
    unfocusedBorderColor     = borderOverride ?: BorderColor,
    focusedTextColor         = OnBackground,
    unfocusedTextColor       = OnBackground,
    cursorColor              = OnBackground
)

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NewPasswordScreenPreview() {
    NewPasswordScreen()
}
