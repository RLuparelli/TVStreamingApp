package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.*
import kotlin.random.Random

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 30
) {
    val particles = remember {
        List(particleCount) {
            FloatingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1,
                speed = Random.nextFloat() * 0.5f + 0.1f,
                angle = Random.nextFloat() * 360,
                color = listOf(
                    Color(0xFF6B46C1),
                    Color(0xFF9333EA),
                    Color(0xFF0891B2),
                    Color(0xFF06B6D4),
                    Color(0xFFF59E0B)
                ).random().copy(alpha = Random.nextFloat() * 0.3f + 0.1f)
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )
    
    Canvas(modifier = modifier) {
        particles.forEach { particle ->
            drawFloatingParticle(particle, time)
        }
    }
}

private data class FloatingParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val angle: Float,
    val color: Color
)

private fun DrawScope.drawFloatingParticle(particle: FloatingParticle, time: Float) {
    val width = size.width
    val height = size.height
    
    // Movimento sinusoidal suave
    val angleRad = Math.toRadians(particle.angle.toDouble())
    val xOffset = (sin(time * 2 * PI + particle.x * 2 * PI) * 50).toFloat()
    val yOffset = (cos(time * 2 * PI * particle.speed + particle.y * 2 * PI) * 30).toFloat()
    
    var x = (particle.x * width + xOffset + time * particle.speed * 100 * cos(angleRad)).toFloat() % width
    var y = (particle.y * height + yOffset + time * particle.speed * 100 * sin(angleRad)).toFloat() % height
    
    // Wrap around nas bordas
    if (x < 0) x += width
    if (y < 0) y += height
    
    // Efeito de brilho pulsante
    val pulse = ((sin(time * 4 * PI + particle.x * 2 * PI) + 1) / 2).toFloat()
    val finalSize = particle.size * (0.8f + pulse * 0.4f)
    val finalAlpha = particle.color.alpha * (0.5f + pulse * 0.5f)
    
    // Desenhar a partícula com brilho
    drawCircle(
        color = particle.color.copy(alpha = finalAlpha * 0.3f),
        radius = finalSize * 3f,
        center = Offset(x, y)
    )
    
    drawCircle(
        color = particle.color.copy(alpha = finalAlpha),
        radius = finalSize,
        center = Offset(x, y)
    )
}