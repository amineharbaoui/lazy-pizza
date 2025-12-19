package com.example.ui.checkout

sealed interface CheckoutUiState {

    data object Loading : CheckoutUiState

    data class Error(
        val message: String,
    ) : CheckoutUiState

    data class Ready(
        val pickup: PickupUiState,
        val orderSummary: OrderSummaryUi,
        val comment: String,
        val canPlaceOrder: Boolean,
        val isPlacingOrder: Boolean = false,
    ) : CheckoutUiState
}

data class PickupUiState(
    val selectedOption: PickupOption,
    val asapOption: PickupOptionDisplayModel,
    val scheduleOption: PickupOptionDisplayModel,
    val schedule: SchedulePickUpDisplayModel,
)

data class OrderSummaryUi(
    val lines: List<OrderLineUi>,
    val totalLabel: String,
)

sealed interface OrderLineUi {
    val title: String
    val unitPriceLabel: String
    val totalPriceLabel: String?
    val subtitleLines: List<String>

    /** Main product (Pizza) → navigable to details */
    data class MainProduct(
        val productId: String,
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineUi

    /** Secondary product (drink, ice cream, sauce…) → not navigable */
    data class SecondaryProduct(
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineUi
}

data class SchedulePickUpDisplayModel(
    val days: List<PickUpDay>,
    val selection: PickUpSelection,
    val confirmation: PickUpConfirmation? = null,
)

data class PickUpSelection(
    val selectedDayId: String?,
    val selectedTimeSlotId: String?,
)

data class PickUpConfirmation(
    val dayId: String,
    val timeSlotId: String,
)

data class PickUpDay(
    val id: String,
    val labelTop: String?,
    val labelBottom: String?,
    val availableTimeSlots: List<PickUpTimeSlot>,
)

data class PickUpTimeSlot(
    val id: String,
    val label: String,
)

data class PickupOptionDisplayModel(
    val title: String,
    val dateLabel: String? = null,
    val timeLabel: String? = null,
)

enum class PickupOption {
    ASAP,
    SCHEDULE,
}
