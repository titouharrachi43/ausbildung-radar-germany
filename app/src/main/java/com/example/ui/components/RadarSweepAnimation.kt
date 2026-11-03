package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarCyanGlow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarSweepAnimation(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarTransition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isScanning) 2500 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarPulse"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (minOf(size.width, size.height) / 2f) * 0.92f

        // Draw concentric radar range rings
        val rings = 4
        for (i in 1..rings) {
            val r = maxRadius * (i.toFloat() / rings)
            drawCircle(
                color = RadarCyan.copy(alpha = 0.15f),
                radius = r,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Draw crosshairs
        drawLine(
            color = RadarCyan.copy(alpha = 0.2f),
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = RadarCyan.copy(alpha = 0.2f),
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 1.dp.toPx()
        )

        // Draw sweep line & wedge
        val rad = Math.toRadians(angle.toDouble())
        val sweepEnd = Offset(
            x = (center.x + maxRadius * cos(rad)).toFloat(),
            y = (center.y + maxRadius * sin(rad)).toFloat()
        )

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(RadarCyanGlow, RadarCyan.copy(alpha = 0.2f)),
                start = center,
                end = sweepEnd
            ),
            start = center,
            end = sweepEnd,
            strokeWidth = 2.5.dp.toPx()
        )

        // Draw center pulse dot
        drawCircle(
            color = RadarCyanGlow,
            radius = 4.dp.toPx() * pulse,
            center = center
        )

        // Draw mock discovery target blips
        val blip1 = Offset(center.x + maxRadius * 0.45f, center.y - maxRadius * 0.35f)
        val blip2 = Offset(center.x - maxRadius * 0.55f, center.y + maxRadius * 0.25f)
        val blip3 = Offset(center.x + maxRadius * 0.2f, center.y + maxRadius * 0.6f)

        drawCircle(color = Color(0xFFF59E0B), radius = 3.5.dp.toPx(), center = blip1)
        drawCircle(color = Color(0xFF10B981), radius = 3.5.dp.toPx(), center = blip2)
        drawCircle(color = Color(0xFF8B5CF6), radius = 3.dp.toPx(), center = blip3)
    }
}
