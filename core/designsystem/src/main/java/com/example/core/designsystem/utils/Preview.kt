package com.example.core.designsystem.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

@Composable
fun rememberImagePainterPreviewAware(
    @DrawableRes previewDrawableResId: Int,
    painterProvider: @Composable () -> Painter,
): Painter = if (LocalInspectionMode.current) {
    painterResource(id = previewDrawableResId)
} else {
    painterProvider()
}
