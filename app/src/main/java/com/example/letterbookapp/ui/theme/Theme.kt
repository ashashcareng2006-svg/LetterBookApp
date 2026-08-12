package com.example.letterbookapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightMint,
    onPrimary = DarkGreenBg,
    secondary = DeepForest,
    onSecondary = Color.White,
    tertiary = SandAccent,
    background = DarkGreenBg,
    surface = DarkGreenSurface,
    surfaceVariant = Color(0xFF1E301F), // For search bars and secondary cards
    onBackground = Color(0xFFE2EBE2),
    onSurface = Color(0xFFE2EBE2),
    onSurfaceVariant = Color(0xFFA8BCA9)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    secondary = SageGreen,
    onSecondary = Color.White,
    tertiary = EarthBrown,
    background = MintCream,
    surface = SurfaceWhite,
    surfaceVariant = Color(0xFFE0EBE0), // For search bars and secondary cards
    onBackground = Color(0xFF172118),
    onSurface = Color(0xFF172118),
    onSurfaceVariant = Color(0xFF4A5E4B)
)

@Composable
fun LetterBookAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}