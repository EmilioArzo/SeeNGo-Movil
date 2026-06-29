package com.emilionavarro.prueba.autenticacion



import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Color tokens ─────────────────────────────────────────────────────────────
private val SplashBackground = Color(0xFF2E3830)   // dark muted green
private val IconBackground   = Color(0xFF3D4840)   // slightly lighter for icon box
private val OnDark           = Color(0xFFFFFFFF)
private val OnDarkSubtle     = Color(0xFFB0B8B2)   // muted white for subtitle
private val DotActive        = Color(0xFFFFFFFF)
private val DotInactive      = Color(0xFF6B7570)

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit = {}
) {
    // Animate content in on first composition
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
        label = "splash_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutBack),
        label = "splash_scale"
    )

    // Navigate away after delay
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        // ── Center content ────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            // App icon box
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(IconBackground, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Smart home / sensor icon drawn with Text emoji as placeholder.
                // Replace with your actual SVG/vector drawable:
                // Icon(painterResource(R.drawable.ic_seengo), contentDescription = null, tint = OnDark, modifier = Modifier.size(48.dp))
                Text("⌂•", fontSize = 36.sp, color = OnDark)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "See n go",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = OnDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tu casa responde con una seña.",
                fontSize = 15.sp,
                color = OnDarkSubtle,
                letterSpacing = 0.sp
            )
        }

        // ── Bottom: dots + version ────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingDots()
            Spacer(Modifier.height(12.dp))
            Text(
                text = "V 1.0.0",
                fontSize = 12.sp,
                color = OnDarkSubtle,
                letterSpacing = 1.sp
            )
        }
    }
}

// ── Animated loading dots ─────────────────────────────────────────────────────
@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    // Each dot pulses with a staggered delay
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ), label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ), label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ), label = "dot3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(dot1Alpha, dot2Alpha, dot3Alpha).forEach { dotAlpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(dotAlpha)
                    .background(DotActive, RoundedCornerShape(50))
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}

