package com.emilionavarro.prueba.inicio



import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Color tokens ─────────────────────────────────────────────────────────────
private val Background    = Color(0xFFEAE7E0)
private val SkeletonBase  = Color(0xFFDDDAD3)
private val SkeletonShine = Color(0xFFEFECE5)

// ── Shimmer brush ─────────────────────────────────────────────────────────────
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue   = -600f,
        targetValue    = 600f,
        animationSpec  = infiniteRepeatable(
            animation     = tween(1000, easing = LinearEasing),
            repeatMode    = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )
    return Brush.linearGradient(
        colors     = listOf(SkeletonBase, SkeletonShine, SkeletonBase),
        start      = Offset(translateX, 0f),
        end        = Offset(translateX + 400f, 0f)
    )
}

// ── Base skeleton box ─────────────────────────────────────────────────────────
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp   = 10.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush())
    )
}

@Composable
fun SkeletonCircle(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

// ══════════════════════════════════════════════════════════════════════════════
// HOME SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun HomeScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Greeting
        SkeletonBox(Modifier.width(120.dp).height(13.dp))
        Spacer(Modifier.height(6.dp))
        SkeletonBox(Modifier.width(200.dp).height(28.dp), cornerRadius = 8.dp)

        Spacer(Modifier.height(20.dp))

        // Energy card
        SkeletonBox(Modifier.fillMaxWidth().height(90.dp), cornerRadius = 18.dp)

        Spacer(Modifier.height(24.dp))

        // Section label
        SkeletonBox(Modifier.width(140.dp).height(10.dp))
        Spacer(Modifier.height(10.dp))

        // 2x2 device grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(2) {
                        SkeletonBox(Modifier.weight(1f).height(100.dp), cornerRadius = 16.dp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Gestures label
        SkeletonBox(Modifier.width(130.dp).height(10.dp))
        Spacer(Modifier.height(12.dp))

        // Gesture cards row
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            repeat(4) {
                SkeletonBox(Modifier.size(72.dp), cornerRadius = 16.dp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// DEVICES SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun DevicesScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                SkeletonBox(Modifier.width(140.dp).height(28.dp), cornerRadius = 8.dp)
                Spacer(Modifier.height(6.dp))
                SkeletonBox(Modifier.width(170.dp).height(13.dp))
            }
            SkeletonBox(Modifier.size(44.dp), cornerRadius = 14.dp)
        }

        Spacer(Modifier.height(20.dp))

        // Consumo card
        SkeletonBox(Modifier.fillMaxWidth().height(76.dp), cornerRadius = 18.dp)

        Spacer(Modifier.height(24.dp))

        // Room 1
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SkeletonBox(Modifier.width(60.dp).height(17.dp))
            SkeletonBox(Modifier.width(100.dp).height(12.dp))
        }
        Spacer(Modifier.height(10.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(180.dp), cornerRadius = 18.dp)

        Spacer(Modifier.height(24.dp))

        // Room 2
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SkeletonBox(Modifier.width(90.dp).height(17.dp))
            SkeletonBox(Modifier.width(100.dp).height(12.dp))
        }
        Spacer(Modifier.height(10.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(120.dp), cornerRadius = 18.dp)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// DEVICE DETAIL SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun DeviceDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Top bar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SkeletonBox(Modifier.size(36.dp), cornerRadius = 10.dp)
            SkeletonBox(Modifier.width(140.dp).height(18.dp))
            SkeletonBox(Modifier.size(40.dp), cornerRadius = 12.dp)
        }
        Spacer(Modifier.height(20.dp))

        // Status card
        SkeletonBox(Modifier.fillMaxWidth().height(140.dp), cornerRadius = 20.dp)
        Spacer(Modifier.height(16.dp))

        // Chart card
        SkeletonBox(Modifier.fillMaxWidth().height(130.dp), cornerRadius = 20.dp)
        Spacer(Modifier.height(24.dp))

        // Section label
        SkeletonBox(Modifier.width(140.dp).height(10.dp))
        Spacer(Modifier.height(10.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(120.dp), cornerRadius = 18.dp)
        Spacer(Modifier.height(24.dp))

        // Info section
        SkeletonBox(Modifier.width(100.dp).height(10.dp))
        Spacer(Modifier.height(10.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(150.dp), cornerRadius = 18.dp)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// GESTURES SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun GesturesScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                SkeletonBox(Modifier.width(80.dp).height(28.dp), cornerRadius = 8.dp)
                Spacer(Modifier.height(6.dp))
                SkeletonBox(Modifier.width(200.dp).height(13.dp))
            }
            SkeletonBox(Modifier.size(44.dp), cornerRadius = 14.dp)
        }
        Spacer(Modifier.height(16.dp))

        // Search
        SkeletonBox(Modifier.fillMaxWidth().height(52.dp), cornerRadius = 14.dp)
        Spacer(Modifier.height(12.dp))

        // Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(90.dp, 70.dp, 60.dp, 80.dp).forEach { w ->
                SkeletonBox(Modifier.width(w).height(36.dp), cornerRadius = 20.dp)
            }
        }
        Spacer(Modifier.height(14.dp))

        // Gesture cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) {
                SkeletonBox(Modifier.fillMaxWidth().height(86.dp), cornerRadius = 18.dp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PROFILE SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun ProfileScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                SkeletonBox(Modifier.width(80.dp).height(28.dp), cornerRadius = 8.dp)
                Spacer(Modifier.height(6.dp))
                SkeletonBox(Modifier.width(170.dp).height(13.dp))
            }
            SkeletonBox(Modifier.size(40.dp), cornerRadius = 12.dp)
        }
        Spacer(Modifier.height(20.dp))

        // User card
        SkeletonBox(Modifier.fillMaxWidth().height(90.dp), cornerRadius = 18.dp)
        Spacer(Modifier.height(12.dp))

        // Stats
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) { SkeletonBox(Modifier.weight(1f).height(66.dp), cornerRadius = 14.dp) }
        }
        Spacer(Modifier.height(24.dp))

        // Sections
        SkeletonBox(Modifier.width(70.dp).height(10.dp))
        Spacer(Modifier.height(8.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(160.dp), cornerRadius = 18.dp)
        Spacer(Modifier.height(20.dp))

        SkeletonBox(Modifier.width(110.dp).height(10.dp))
        Spacer(Modifier.height(8.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(160.dp), cornerRadius = 18.dp)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SUGGESTIONS SKELETON
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun SuggestionsScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                SkeletonBox(Modifier.width(130.dp).height(28.dp), cornerRadius = 8.dp)
                Spacer(Modifier.height(6.dp))
                SkeletonBox(Modifier.width(200.dp).height(13.dp))
            }
            SkeletonBox(Modifier.size(44.dp), cornerRadius = 14.dp)
        }
        Spacer(Modifier.height(20.dp))

        // Featured dark card
        SkeletonBox(Modifier.fillMaxWidth().height(170.dp), cornerRadius = 20.dp)
        Spacer(Modifier.height(24.dp))

        // Section label
        SkeletonBox(Modifier.width(160.dp).height(10.dp))
        Spacer(Modifier.height(10.dp))

        // Suggestion cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) {
                SkeletonBox(Modifier.fillMaxWidth().height(90.dp), cornerRadius = 18.dp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// GENERIC / DETAIL SKELETON (para pantallas simples)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun GenericDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {
        // Top bar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SkeletonBox(Modifier.size(36.dp), cornerRadius = 10.dp)
            SkeletonBox(Modifier.width(160.dp).height(18.dp))
            SkeletonBox(Modifier.size(36.dp), cornerRadius = 10.dp)
        }
        Spacer(Modifier.height(24.dp))

        // Content blocks
        SkeletonBox(Modifier.fillMaxWidth().height(160.dp), cornerRadius = 20.dp)
        Spacer(Modifier.height(16.dp))
        repeat(3) {
            SkeletonBox(Modifier.fillMaxWidth().height(56.dp), cornerRadius = 14.dp)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(8.dp))
        SkeletonBox(Modifier.fillMaxWidth().height(180.dp), cornerRadius = 18.dp)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeSkeletonPreview() { HomeScreenSkeleton() }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DevicesSkeletonPreview() { DevicesScreenSkeleton() }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GesturesSkeletonPreview() { GesturesScreenSkeleton() }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ProfileSkeletonPreview() { ProfileScreenSkeleton() }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SuggestionsSkeletonPreview() { SuggestionsScreenSkeleton() }