package com.example.core.designsystem.utils

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "P1 - Phone",
    device = Devices.PHONE,
    showSystemUi = true,
    showBackground = true,
)
@Preview(
    name = "P2 - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait",
    showSystemUi = true,
    showBackground = true,
)
@Preview(
    name = "P3 - Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showSystemUi = true,
    showBackground = true,
)
@Preview(
    name = "P4 - Tablet - Landscape",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showSystemUi = true,
    showBackground = true,
)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
annotation class PreviewPhoneTablet
