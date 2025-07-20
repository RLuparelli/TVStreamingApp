package com.tvstreaming.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.*
import kotlin.random.Random

@Composable
fun AnimatedSpaceBackground(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Animações infinitas para movimento suave
    val infiniteTransition = rememberInfiniteTransition(label = "space")
    
    // Rotação lenta das estrelas
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = LinearEasing), // 2 minutos para uma rotação completa
            repeatMode = RepeatMode.Restart
        ),
        label = "starRotation"
    )
    
    // Pulsação suave do brilho
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowIntensity"
    )
    
    // Movimento de nebulosa
    val nebulaOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebulaOffset"
    )
    
    // Gerar estrelas fixas uma vez
    val stars = remember {
        List(200) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2 + 0.5f,
                brightness = Random.nextFloat() * 0.5f + 0.5f,
                twinkleSpeed = Random.nextFloat() * 2000 + 1000
            )
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000814), // Azul muito escuro
                        Color(0xFF001D3D), // Azul escuro
                        Color(0xFF000814)
                    )
                )
            )
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Desenhar nebulosas de fundo
            drawNebula(nebulaOffset, glowIntensity)
            
            // Desenhar estrelas com rotação
            rotate(degrees = starRotation * 0.1f) {
                stars.forEach { star ->
                    drawStar(star, glowIntensity)
                }
            }
            
            // Adicionar alguns planetas/orbes distantes
            drawDistantOrbs(nebulaOffset, glowIntensity)
        }
        
        // Gradiente de overlay para melhor visibilidade do conteúdo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val brightness: Float,
    val twinkleSpeed: Float
)

private fun DrawScope.drawStar(star: Star, glowIntensity: Float) {
    val x = star.x * size.width
    val y = star.y * size.height
    val time = System.currentTimeMillis()
    
    // Cintilação individual de cada estrela
    val twinkle = (sin(time / star.twinkleSpeed) + 1) / 2
    val finalBrightness = star.brightness * twinkle * glowIntensity
    
    // Desenhar o brilho da estrela
    val glowRadius = star.size * 3
    val glowColor = Color.White.copy(alpha = finalBrightness * 0.3f)
    
    // Gradiente radial para o brilho
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor,
                glowColor.copy(alpha = 0f)
            ),
            radius = glowRadius
        ),
        radius = glowRadius,
        center = Offset(x, y)
    )
    
    // Centro da estrela
    drawCircle(
        color = Color.White.copy(alpha = finalBrightness),
        radius = star.size,
        center = Offset(x, y)
    )
}

private fun DrawScope.drawNebula(offset: Float, intensity: Float) {
    val width = size.width
    val height = size.height
    
    // Primeira camada de nebulosa - roxa
    val nebula1Path = Path().apply {
        moveTo(0f, height * 0.3f)
        cubicTo(
            width * 0.25f + offset, height * 0.2f,
            width * 0.75f - offset, height * 0.4f,
            width, height * 0.35f
        )
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    
    drawPath(
        path = nebula1Path,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF6B46C1).copy(alpha = 0.1f * intensity),
                Color(0xFF9333EA).copy(alpha = 0.05f * intensity),
                Color.Transparent
            )
        )
    )
    
    // Segunda camada de nebulosa - azul
    val nebula2Path = Path().apply {
        moveTo(0f, height * 0.7f)
        cubicTo(
            width * 0.3f - offset * 0.5f, height * 0.6f,
            width * 0.7f + offset * 0.5f, height * 0.8f,
            width, height * 0.75f
        )
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    
    drawPath(
        path = nebula2Path,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF0891B2).copy(alpha = 0.1f * intensity),
                Color(0xFF06B6D4).copy(alpha = 0.05f * intensity),
                Color.Transparent
            )
        )
    )
}

private fun DrawScope.drawDistantOrbs(offset: Float, intensity: Float) {
    // Planeta distante 1
    val planet1X = size.width * 0.8f + sin(offset * 0.01f) * 20
    val planet1Y = size.height * 0.2f + cos(offset * 0.01f) * 10
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFDC2626).copy(alpha = 0.3f * intensity),
                Color(0xFFDC2626).copy(alpha = 0.1f * intensity),
                Color.Transparent
            ),
            radius = 40f
        ),
        radius = 40f,
        center = Offset(planet1X, planet1Y)
    )
    
    drawCircle(
        color = Color(0xFFDC2626).copy(alpha = 0.6f * intensity),
        radius = 15f,
        center = Offset(planet1X, planet1Y)
    )
    
    // Planeta distante 2
    val planet2X = size.width * 0.2f - sin(offset * 0.015f) * 15
    val planet2Y = size.height * 0.15f - cos(offset * 0.015f) * 8
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF0891B2).copy(alpha = 0.3f * intensity),
                Color(0xFF0891B2).copy(alpha = 0.1f * intensity),
                Color.Transparent
            ),
            radius = 30f
        ),
        radius = 30f,
        center = Offset(planet2X, planet2Y)
    )
    
    drawCircle(
        color = Color(0xFF0891B2).copy(alpha = 0.6f * intensity),
        radius = 10f,
        center = Offset(planet2X, planet2Y)
    )
}