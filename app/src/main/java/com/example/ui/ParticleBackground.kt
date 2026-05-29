package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.NeonPurple
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var speedX: Float,
    var speedY: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ParticleBackground(modifier: Modifier = Modifier) {
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speedX = (Random.nextFloat() - 0.5f) * 0.002f,
                speedY = (Random.nextFloat() - 0.5f) * 0.002f,
                size = Random.nextFloat() * 4f + 2f,
                color = if (Random.nextBoolean()) CyberBlue.copy(alpha = 0.5f) else NeonPurple.copy(alpha = 0.5f)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particlesAnim"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Just to depend on the animation state
        animValue.let { _ ->
            particles.forEach { p ->
                p.x += p.speedX
                p.y += p.speedY

                if (p.x < 0f) p.x = 1f
                if (p.x > 1f) p.x = 0f
                if (p.y < 0f) p.y = 1f
                if (p.y > 1f) p.y = 0f

                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = Offset(p.x * width, p.y * height)
                )
            }
            
            // Draw connections
            for (i in particles.indices) {
                for (j in i + 1 until particles.size) {
                    val p1 = particles[i]
                    val p2 = particles[j]
                    val dx = (p1.x - p2.x) * width
                    val dy = (p1.y - p2.y) * height
                    val distSq = dx * dx + dy * dy
                    val maxDist = 150f
                    if (distSq < maxDist * maxDist) {
                        val distance = kotlin.math.sqrt(distSq)
                        val alpha = (1f - (distance / maxDist)) * 0.2f
                        drawLine(
                            color = p1.color.copy(alpha = alpha),
                            start = Offset(p1.x * width, p1.y * height),
                            end = Offset(p2.x * width, p2.y * height),
                            strokeWidth = 1f
                        )
                    }
                }
            }
        }
    }
}
