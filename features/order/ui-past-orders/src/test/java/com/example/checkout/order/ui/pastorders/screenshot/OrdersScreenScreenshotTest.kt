package com.example.checkout.order.ui.pastorders.screenshot

import com.example.checkout.order.ui.pastorders.OrdersScreenNotLoggedInPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenNothingAvailableDialogPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenReadyMixedStatusesPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenSomeSkippedDialogPreview
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

// ORDER-01, ORDER-02, ORDER-15, ORDER-16: Roborazzi captures of the Orders screen previews.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class OrdersScreenScreenshotTest {

    // Baselines are recorded on macOS; Linux CI renders a few anti-aliased pixels differently.
    private val crossPlatformOptions = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(resultValidator = ThresholdValidator(0.0001f)),
    )

    @Test
    fun `captures OrdersScreenReadyMixedStatusesPreview`() {
        captureRoboImage(roborazziOptions = crossPlatformOptions) { OrdersScreenReadyMixedStatusesPreview() }
    }

    @Test
    fun `captures OrdersScreenNotLoggedInPreview`() {
        captureRoboImage(roborazziOptions = crossPlatformOptions) { OrdersScreenNotLoggedInPreview() }
    }

    @Test
    fun `captures OrdersScreenSomeSkippedDialogPreview`() {
        captureRoboImage(roborazziOptions = crossPlatformOptions) { OrdersScreenSomeSkippedDialogPreview() }
    }

    @Test
    fun `captures OrdersScreenNothingAvailableDialogPreview`() {
        captureRoboImage(roborazziOptions = crossPlatformOptions) { OrdersScreenNothingAvailableDialogPreview() }
    }
}
