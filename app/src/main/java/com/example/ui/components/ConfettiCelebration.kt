package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Particle(
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val swayAmount: Float
)

@Composable
fun ConfettiCelebration(
    triggerTime: Long,
    modifier: Modifier = Modifier
) {
    if (triggerTime == 0L) return

    val progress = remember(triggerTime) { Animatable(0f) }

    LaunchedEffect(triggerTime) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    if (progress.value >= 1f) return

    val particles = remember(triggerTime) {
        val colors = listOf(
            Color(0xFFFFD54F), // Gold
            Color(0xFF00E676), // Emerald
            Color(0xFF40C4FF), // Sky Blue
            Color(0xFFFF5252), // Ruby
            Color(0xFFE040FB), // Magenta
            Color(0xFFFFFFFF)  // Sparkle white
        )
        List(45) {
            Particle(
                xRatio = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.8f,
                size = 14f + Random.nextFloat() * 16f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                swayAmount = 30f + Random.nextFloat() * 40f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value

        particles.forEach { p ->
            val y = (t * p.speed * h * 1.2f) - 50f
            val x = (p.xRatio * w) + (kotlin.math.sin((t * 6f + p.xRatio * 10f).toDouble()).toFloat() * p.swayAmount)
            val currentRotation = t * p.rotationSpeed
            val alpha = (1f - (t * 0.8f)).coerceIn(0f, 1f)

            rotate(degrees = currentRotation, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(p.size, p.size * 0.6f)
                )
            }
        }
    }
}
