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

    data class Success(
        val orderNumber: String,
        val pickupTime: String,
    ) : CheckoutUiState
}

/** Top section (ASAP / Schedule cards) + bottom sheet model */
data class PickupUiState(
    val selectedOption: PickupOption,
    val asapCard: PickupOptionCardUiModel,
    val scheduleCard: PickupOptionCardUiModel,
    val scheduleSheet: SchedulePickupUiModel,
)

data class PickupOptionCardUiModel(
    val id: PickupOption,
    val title: String,
    val dateLabel: String?, // e.g. "Today - Dec 20" or "Choose a time"
    val timeLabel: String?, // e.g. "5:07 PM" (single pickup time)
    val isSelected: Boolean,
    val isEnabled: Boolean = true,
)

/** Bottom sheet */
data class SchedulePickupUiModel(
    val days: List<PickupDayUiModel>,
    val selection: PickupSelectionUiModel,
    val confirmation: PickupConfirmationUiModel? = null,
)

data class PickupSelectionUiModel(
    val selectedDayId: String?,
    val selectedTimeSlotId: String?,
)

data class PickupConfirmationUiModel(
    val dayId: String,
    val timeSlotId: String,
)

data class PickupDayUiModel(
    val id: String,
    val dayLabel: String, // "Today", "Tomorrow", "Mon"
    val dateLabel: String, // "Dec 20"
    val isEnabled: Boolean = true,
    val timeSlots: List<PickupTimeSlotUiModel>,
)

data class PickupTimeSlotUiModel(
    val id: String, // stable id (e.g. "17:15")
    val timeLabel: String, // "5:15 PM"
    val isEnabled: Boolean = true,
)

/** Order summary */
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
    data class PizzaProduct(
        val productId: String,
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineUi

    /** Secondary product (drink, ice cream, sauce…) → not navigable */
    data class OtherProduct(
        override val title: String,
        override val unitPriceLabel: String,
        override val totalPriceLabel: String?,
        override val subtitleLines: List<String>,
    ) : OrderLineUi
}

enum class PickupOption {
    ASAP,
    SCHEDULE,
}
