package com.example.core.screenshottesting

import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.inspectionMode

/**
 * Renders every preview in inspection mode, as Android Studio does: `rememberImagePainterPreviewAware`
 * shows its local drawable instead of loading the image from the network.
 */
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
class InspectionModePreviewTester :
    ComposePreviewTester<AndroidPreviewJUnit4TestParameter> by AndroidComposePreviewTester(
        capturer = object : AndroidComposePreviewTester.Capturer {
            override fun capture(parameter: AndroidComposePreviewTester.CaptureParameter) {
                val options = parameter.roborazziComposeOptions.builder().inspectionMode(true).build()
                AndroidComposePreviewTester.DefaultCapturer().capture(parameter.copy(roborazziComposeOptions = options))
            }
        },
    )
