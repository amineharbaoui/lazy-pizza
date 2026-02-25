package com.example.core.designsystem.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "P1 - Phone",
    device = Devices.PHONE,
    showBackground = true,
)
@Preview(
    name = "P1 - Phone",
    device = "id:small_phone",
    showBackground = true,
)
@Preview(
    name = "P1 - Phone (Dark)",
    device = Devices.PHONE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "P2 - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait",
    showBackground = true,
)
@Preview(
    name = "P3 - Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showBackground = true,
)
@Preview(
    name = "P4 - Tablet - Landscape",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
annotation class PreviewPhoneTablet
