package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF1E1035),
    primaryContainer = Color(0xFF3B1E6D),
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = Color(0xFFC4B5FD),
    secondaryContainer = Color(0xFF281F42),
    onSecondaryContainer = Color(0xFFDDD6FE),
    tertiary = NeonEmerald,
    background = BentoBackgroundDark,
    onBackground = BentoOnSurfaceDark,
    surface = BentoSurfaceDark,
    onSurface = BentoOnSurfaceDark,
    surfaceVariant = BentoSurfaceVariantDark,
    onSurfaceVariant = BentoOnSurfaceVariantDark,
    outline = BentoCardBorderDark,
    outlineVariant = Color(0xFF231F36),
    error = NeonRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    onPrimary = Color.White,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = Color(0xFF8B5CF6),
    secondaryContainer = BentoSecondaryContainer,
    tertiary = Color(0xFF059669),
    background = BentoBackgroundLight,
    onBackground = BentoOnSurfaceLight,
    surface = BentoSurfaceLight,
    onSurface = BentoOnSurfaceLight,
    surfaceVariant = BentoSurfaceVariantLight,
    onSurfaceVariant = BentoOnSurfaceVariantLight,
    outline = BentoOutlineLight,
    outlineVariant = Color(0xFFECE7F6),
    error = Color(0xFFE11D48),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek luxury dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
