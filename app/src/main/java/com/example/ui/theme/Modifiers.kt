package com.example.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassmorphism(
    shape: Shape = RoundedCornerShape(32.dp)
): Modifier = composed {
    this
        .clip(shape)
        .background(GlassWhite)
        .border(1.dp, GlassBorder, shape)
}

fun Modifier.glassCard(shape: Shape = RoundedCornerShape(32.dp)): Modifier = this
    .clip(shape)
    .background(GlassWhite)
    .border(1.dp, GlassBorder, shape)

// Aurora background layer
fun Modifier.auroraBackground(): Modifier = this.drawBehind {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF1B2448),
                Color(0xFF0B1020),
                Color(0xFF05070D)
            ),
            center = center,
            radius = size.width
        )
    )
}

// Breathing motion animation
fun Modifier.breathingPulse(
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    durationMillis: Int = 4000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
