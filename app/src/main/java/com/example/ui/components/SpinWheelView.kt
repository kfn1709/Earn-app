package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpinWheelView(
    targetDegree: Float,
    isSpinning: Boolean,
    onSpinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedRotation by animateFloatAsState(
        targetValue = targetDegree,
        animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
        label = "spin_wheel_rotation"
    )

    val prizes = listOf(50, 25, 200, 100, 500, 75, 150, 30)
    val sectorColors = listOf(
        Color(0xFF0D47A1), // Deep Blue
        Color(0xFF00796B), // Teal
        Color(0xFFE65100), // Vibrant Amber
        Color(0xFF4A148C), // Royal Purple
        Color(0xFFC2185B), // Ruby Magenta
        Color(0xFF1B5E20), // Forest Green
        Color(0xFFBF360C), // Flame
        Color(0xFF2E7D32)  // Emerald
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing ring
        Box(
            modifier = Modifier
                .fillMaxSize(0.96f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF1B4D36), Color(0xFF0A1C13))
                    )
                )
                .border(6.dp, Brush.sweepGradient(listOf(GoldAccent, EmeraldPrimary, GoldBright, GoldAccent)), CircleShape)
        )

        // The spinning wheel Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.88f)
                .rotate(animatedRotation)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val sweepAngle = 360f / prizes.size

            for (i in prizes.indices) {
                val startAngle = i * sweepAngle - 90f
                // Sector arc
                drawArc(
                    color = sectorColors[i % sectorColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Sector border separator line
                val rad = Math.toRadians((startAngle).toDouble())
                val endX = center.x + radius * cos(rad).toFloat()
                val endY = center.y + radius * sin(rad).toFloat()
                drawLine(
                    color = GoldAccent.copy(alpha = 0.6f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 3f
                )

                // Prize text
                val midAngle = startAngle + (sweepAngle / 2f)
                val midRad = Math.toRadians(midAngle.toDouble())
                val textDistance = radius * 0.68f
                val textX = center.x + textDistance * cos(midRad).toFloat()
                val textY = center.y + textDistance * sin(midRad).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 34f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    save()
                    rotate(midAngle + 90f, textX, textY)
                    drawText("+${prizes[i]}", textX, textY + 12f, paint)
                    restore()
                }
            }

            // Outer decorative dots
            val dotCount = 16
            for (d in 0 until dotCount) {
                val angle = (d * (360f / dotCount))
                val rad = Math.toRadians(angle.toDouble())
                val dotRadius = radius * 0.94f
                val dx = center.x + dotRadius * cos(rad).toFloat()
                val dy = center.y + dotRadius * sin(rad).toFloat()
                drawCircle(
                    color = if (d % 2 == 0) GoldAccent else Color.White,
                    radius = 5f,
                    center = Offset(dx, dy)
                )
            }
        }

        // Center Spin Button Hub
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GoldBright, GoldAccent, Color(0xFFC67D00))
                    )
                )
                .border(3.dp, Color.White, CircleShape)
                .clickable(enabled = !isSpinning) {
                    onSpinClick()
                }
                .testTag("spin_wheel_center_btn"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSpinning) "..." else "أدر!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF331F00)
            )
        }

        // Pointer Needle at Top (pointing down into current winning sector)
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
                .size(34.dp, 36.dp)
        ) {
            val path = Path().apply {
                moveTo(size.width / 2f, size.height) // Needle bottom tip
                lineTo(0f, 0f) // Top left
                lineTo(size.width, 0f) // Top right
                close()
            }
            drawPath(path = path, color = GoldAccent)
            // Needle inner shadow
            val innerPath = Path().apply {
                moveTo(size.width / 2f, size.height - 4f)
                lineTo(6f, 4f)
                lineTo(size.width - 6f, 4f)
                close()
            }
            drawPath(path = innerPath, color = Color(0xFFFFA000))
        }
    }
}
