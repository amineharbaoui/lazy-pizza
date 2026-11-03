package com.example.core.screenshottesting

import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
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
                val composeOptions = parameter.roborazziComposeOptions.builder().inspectionMode(true).build()
                val compareOptions = RoborazziOptions.CompareOptions(resultValidator = ThresholdValidator(CROSS_PLATFORM_THRESHOLD))
                AndroidComposePreviewTester.DefaultCapturer().capture(
                    parameter.copy(
                        roborazziComposeOptions = composeOptions,
                        roborazziOptions = parameter.roborazziOptions.copy(compareOptions = compareOptions),
                    ),
                )
            }
        },
    )

// Share of pixels allowed to differ: references are recorded on macOS, and Linux CI anti-aliases a few pixels differently.
private const val CROSS_PLATFORM_THRESHOLD = 0.0001f
