package com.example.order.ui.checkout

sealed interface CheckoutUiState {

    data object Loading : CheckoutUiState

    data class Error(
        val message: String,
    ) : CheckoutUiState

    data class ReadyToOrder(
        val pickup: PickupDisplayModel,
        val orderSummary: OrderSummaryDisplayModel,
        val comment: String,
        val canPlaceOrder: Boolean,
        val isPlacingOrder: Boolean = false,
    ) : CheckoutUiState

    data class OrderPlaced(
        val orderNumber: String,
        val pickupTime: String,
    ) : CheckoutUiState
}

/** Top section (ASAP / Schedule cards) + bottom sheet model */
data class PickupDisplayModel(
    val selectedOption: PickupOption,
    val asapCard: PickupOptionCardDisplayModel,
    val scheduleCard: PickupOptionCardDisplayModel,
    val scheduleSheet: SchedulePickupDisplayModel,
)

data class PickupOptionCardDisplayModel(
    val id: PickupOption,
    val title: String,
    val dateLabel: String?,
    val timeLabel: String?,
    val isSelected: Boolean,
    val isEnabled: Boolean = true,
)

/** Bottom sheet */
data class SchedulePickupDisplayModel(
    val days: List<PickupDayDisplayModel>,
    val selection: PickupSelectionDisplayModel,
    val confirmation: PickupConfirmationDisplayModel? = null,
)

data class PickupSelectionDisplayModel(
    val selectedDayId: String?,
    val selectedTimeSlotId: String?,
)

data class PickupConfirmationDisplayModel(
    val dayId: String,
    val timeSlotId: String,
)

data class PickupDayDisplayModel(
    val id: String,
    val dayLabel: String,
    val dateLabel: String,
    val isEnabled: Boolean = true,
    val timeSlots: List<PickupTimeSlotDisplayModel>,
)

data class PickupTimeSlotDisplayModel(
    val id: String,
    val timeLabel: String,
    val isEnabled: Boolean = true,
)

/** Order summary */
data class OrderSummaryDisplayModel(
    val lines: List<OrderLineDisplayModel>,
    val totalLabel: String,
)

sealed interface OrderLineDisplayModel {
    val title: String
    val unitPriceLabel: String
    val totalPriceLabel: String?
    val subtitleLines: List<String>

    /** Main product (Pizza) → navigable to details */
    data class PizzaProduct(
        val productId: String,
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineDisplayModel

    /** Secondary product (drink, ice cream, sauce…) → not navigable */
    data class OtherProduct(
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineDisplayModel
}

enum class PickupOption {
    ASAP,
    SCHEDULE,
}
