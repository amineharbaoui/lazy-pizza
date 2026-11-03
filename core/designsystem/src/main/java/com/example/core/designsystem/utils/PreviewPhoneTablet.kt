package com.example.core.designsystem.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Phone",
    device = Devices.PHONE,
    showBackground = true,
)
@Preview(
    name = "Phone - Dark",
    device = Devices.PHONE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Tablet - Landscape",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
annotation class PreviewPhoneTablet
