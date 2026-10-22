package com.example.checkout.order.ui.pastorders.screenshot

import com.example.checkout.order.ui.pastorders.OrdersScreenNotLoggedInPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenNothingAvailableDialogPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenReadyMixedStatusesPreview
import com.example.checkout.order.ui.pastorders.OrdersScreenSomeSkippedDialogPreview
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

// ORDER-01, ORDER-02, ORDER-15, ORDER-16: Roborazzi captures of the Orders screen previews.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class OrdersScreenScreenshotTest {

    @Test
    fun `captures OrdersScreenReadyMixedStatusesPreview`() {
        captureRoboImage { OrdersScreenReadyMixedStatusesPreview() }
    }

    @Test
    fun `captures OrdersScreenNotLoggedInPreview`() {
        captureRoboImage { OrdersScreenNotLoggedInPreview() }
    }

    @Test
    fun `captures OrdersScreenSomeSkippedDialogPreview`() {
        captureRoboImage { OrdersScreenSomeSkippedDialogPreview() }
    }

    @Test
    fun `captures OrdersScreenNothingAvailableDialogPreview`() {
        captureRoboImage { OrdersScreenNothingAvailableDialogPreview() }
    }
}
