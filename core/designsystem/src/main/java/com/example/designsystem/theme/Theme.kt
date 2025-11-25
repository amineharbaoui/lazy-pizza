package com.example.designsystem.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,

    background = AppColors.Bg,
    onBackground = AppColors.TextPrimary,

    surface = AppColors.SurfaceHigher,
    onSurface = AppColors.TextPrimary,

    surfaceVariant = AppColors.SurfaceHighest,
    onSurfaceVariant = AppColors.TextSecondary,

    outline = AppColors.Outline,
    outlineVariant = AppColors.Outline_50,

    secondary = AppColors.TextSecondary,
    onSecondary = Color.White,
    tertiary = AppColors.Primary,
    onTertiary = Color.White,
)

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

@Composable
fun LazyPizzaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun LazyPizzaThemePreview(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                content()
            }
        }
    }
}