package com.example.ui.checkout

sealed interface CheckoutUiState {

    data object Loading : CheckoutUiState
    data object Error : CheckoutUiState

    data class Ready(
        val pickupOptionId: PickupOption,
        val asapOption: PickupOptionDisplayModel,
        val scheduleOption: PickupOptionDisplayModel,
        val orderSummary: List<OrderLineUi>,
        val comment: String,
        val orderTotalLabel: String,
        val canPlaceOrder: Boolean,

        val schedulePickUp: SchedulePickUpDisplayModel,

    ) : CheckoutUiState
}

data class OrderLineUi(
    val productId: String?,
    val title: String,
    val basePriceLabel: String,
    val totalPriceLabel: String? = null,
    val subtitleLines: List<String>,
)

data class SchedulePickUpDisplayModel(
    val days: List<PickUpDay>,
    val selectedDayId: String?,
    val selectedTimeSlotId: String?,
    val confirmedDayId: String? = null,
    val confirmedTimeSlotId: String? = null,
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
