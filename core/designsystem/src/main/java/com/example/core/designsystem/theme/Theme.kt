package com.example.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

fun colorSchemeFrom(
    p: AppPalette,
    dark: Boolean,
): ColorScheme = if (dark) {
    darkColorScheme(
        primary = p.primary,
        onPrimary = p.textOnPrimary,
        background = p.bg,
        onBackground = p.textPrimary,
        surface = p.surfaceHigher,
        onSurface = p.textPrimary,
        surfaceVariant = p.surfaceHighest,
        onSurfaceVariant = p.textSecondary,
        outline = p.outline,
        outlineVariant = p.outline50,
        secondary = p.textSecondary,
        onSecondary = Color.Black,
        tertiary = p.primary,
        onTertiary = p.textOnPrimary,
    )
} else {
    lightColorScheme(
        primary = p.primary,
        onPrimary = p.textOnPrimary,
        background = p.bg,
        onBackground = p.textPrimary,
        surface = p.surfaceHigher,
        onSurface = p.textPrimary,
        surfaceVariant = p.surfaceHighest,
        onSurfaceVariant = p.textSecondary,
        outline = p.outline,
        outlineVariant = p.outline50,
        secondary = p.textSecondary,
        onSecondary = Color.White,
        tertiary = p.primary,
        onTertiary = Color.White,
    )
}

val Typography = Typography(
    titleLarge = AppTypography.Title1SemiBold,
    titleMedium = AppTypography.Title2SemiBold,
    titleSmall = AppTypography.Title3SemiBold,

    labelMedium = AppTypography.Label2SemiBold,
    labelSmall = AppTypography.Body4Regular,

    bodyLarge = AppTypography.Body1Regular,
    bodyMedium = AppTypography.Body3Regular,
    bodySmall = AppTypography.Body4Regular,
)

val LocalAppPalette = staticCompositionLocalOf { LightPalette }

@Composable
fun LazyPizzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = colorSchemeFrom(palette, darkTheme)

    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

@Composable
fun LazyPizzaThemePreview(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = colorSchemeFrom(palette, darkTheme)
    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
        ) {
            Surface(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
            ) {
                content()
            }
        }
    }
}
