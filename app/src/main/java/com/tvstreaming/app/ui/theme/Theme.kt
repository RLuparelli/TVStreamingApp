package com.tvstreaming.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = OnPrimaryDark,
    secondary = Secondary,
    onSecondary = OnPrimaryDark,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = Error,
    onError = OnPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnPrimaryLight,
    secondary = Secondary,
    onSecondary = OnPrimaryLight,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = Error,
    onError = OnPrimaryLight
)

@Composable
fun TVStreamingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (isTV) TVTypography else MobileTypography,
        content = content
    )
}

// Extension function to check if device is TV
fun android.content.res.Configuration.isTelevision(): Boolean {
    return (uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) == 
           android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
}