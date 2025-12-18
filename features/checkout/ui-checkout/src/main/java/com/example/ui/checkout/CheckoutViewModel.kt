package com.example.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CartItem
import com.example.domain.usecase.ObserveCartUseCase
import com.example.uilogin.utils.formatting.toFormattedCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.onStart {
        loadCheckout()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = CheckoutUiState.Loading,
    )

    private val _events = MutableSharedFlow<CheckoutEvent>()
    val events = _events.asSharedFlow()

    private fun loadCheckout() {
        viewModelScope.launch {
            observeCartUseCase().collect { cart ->
                val currentTime = LocalTime.now()
                val asapTimeLabel = currentTime.plusMinutes(15).format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
                val days = generatePickupDays()
                val today = days.firstOrNull()

                _uiState.value = CheckoutUiState.Ready(
                    pickupOptionId = PickupOption.ASAP,
                    asapOption = PickupOptionDisplayModel(
                        title = "Earliest available",
                        dateLabel = "Today - ${today?.labelBottom}",
                        timeLabel = asapTimeLabel,
                    ),
                    scheduleOption = PickupOptionDisplayModel(
                        title = "Schedule",
                        dateLabel = "Choose a time",
                        timeLabel = null,
                    ),
                    orderSummary = cart.items.map { cartItem ->
                        when (cartItem) {
                            is CartItem.Pizza -> OrderLineUi(
                                productId = cartItem.productId,
                                title = "${cartItem.quantity}x ${cartItem.name}",
                                basePriceLabel = cartItem.basePrice.toFormattedCurrency(),
                                subtitleLines = cartItem.toppings.map { cartTopping ->
                                    "${cartTopping.quantity}x ${cartTopping.name} (${cartTopping.price.toFormattedCurrency()})"
                                },
                                totalPriceLabel = cartItem.lineTotal.toFormattedCurrency().takeIf { cartItem.toppings.isNotEmpty() },
                            )

                            is CartItem.Other -> OrderLineUi(
                                productId = null,
                                title = "${cartItem.quantity}x ${cartItem.name}",
                                basePriceLabel = (cartItem.price * cartItem.quantity).toFormattedCurrency(),
                                totalPriceLabel = null,
                                subtitleLines = emptyList(),
                            )
                        }
                    },
                    comment = "",
                    orderTotalLabel = cart.subtotal.toFormattedCurrency(),
                    canPlaceOrder = true,
                    schedulePickUp = SchedulePickUpDisplayModel(
                        days = days,
                        selectedDayId = null,
                        selectedTimeSlotId = null,
                    ),
                )
            }
        }
    }

    fun onPickupOptionSelected(optionId: PickupOption) {
        viewModelScope.launch {
            if (optionId == PickupOption.SCHEDULE) {
                _events.emit(CheckoutEvent.ShowScheduleBottomSheet)
            }
            _uiState.update { state ->
                when (state) {
                    is CheckoutUiState.Ready -> {
                        if (optionId == PickupOption.SCHEDULE) {
                            val schedule = state.schedulePickUp
                            val confirmedDayId = schedule.confirmedDayId
                            val confirmedTimeSlotId = schedule.confirmedTimeSlotId

                            val (selectedDayId, selectedTimeSlotId) = if (confirmedDayId != null) {
                                confirmedDayId to confirmedTimeSlotId
                            } else {
                                val firstDay = schedule.days.firstOrNull { it.availableTimeSlots.isNotEmpty() }
                                firstDay?.id to firstDay?.availableTimeSlots?.firstOrNull()?.id
                            }

                            state.copy(
                                schedulePickUp = schedule.copy(
                                    selectedDayId = selectedDayId,
                                    selectedTimeSlotId = selectedTimeSlotId,
                                ),
                            )
                        } else {
                            state.copy(pickupOptionId = optionId)
                        }
                    }

                    CheckoutUiState.Loading -> state
                    CheckoutUiState.Error -> state
                }
            }
        }
    }

    fun onCommentChanged(text: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is CheckoutUiState.Ready -> state.copy(comment = text)
                    CheckoutUiState.Loading -> state
                    CheckoutUiState.Error -> state
                }
            }
        }
    }

    fun onScheduleDaySelected(dayId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is CheckoutUiState.Ready -> {
                        val selectedDay = state.schedulePickUp.days.find { it.id == dayId }
                        val firstSlotId = selectedDay?.availableTimeSlots?.firstOrNull()?.id
                        state.copy(
                            schedulePickUp = state.schedulePickUp.copy(
                                selectedDayId = dayId,
                                selectedTimeSlotId = firstSlotId,
                            ),
                        )
                    }

                    else -> state
                }
            }
        }
    }

    fun onScheduleSlotSelected(slotId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is CheckoutUiState.Ready -> {
                        state.copy(
                            schedulePickUp = state.schedulePickUp.copy(
                                selectedTimeSlotId = slotId,
                            ),
                        )
                    }

                    else -> state
                }
            }
        }
    }

    fun onScheduleConfirmed() {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is CheckoutUiState.Ready -> {
                        val schedule = state.schedulePickUp
                        val selectedDay = schedule.days.find { it.id == schedule.selectedDayId }
                        val selectedSlot = selectedDay?.availableTimeSlots?.find { it.id == schedule.selectedTimeSlotId }

                        if (selectedDay != null && selectedSlot != null) {
                            state.copy(
                                pickupOptionId = PickupOption.SCHEDULE,
                                scheduleOption = state.scheduleOption.copy(
                                    dateLabel = "${selectedDay.labelTop} - ${selectedDay.labelBottom}",
                                    timeLabel = selectedSlot.label,
                                ),
                                schedulePickUp = schedule.copy(
                                    confirmedDayId = schedule.selectedDayId,
                                    confirmedTimeSlotId = schedule.selectedTimeSlotId,
                                ),
                            )
                        } else {
                            state
                        }
                    }

                    else -> state
                }
            }
        }
    }

    fun onPlaceOrderClicked() {
        val current = _uiState.value
        val ready = when (current) {
            is CheckoutUiState.Ready -> current
            CheckoutUiState.Loading -> return
            else -> {}
        }

//        if (!ready.canPlaceOrder) return

//        viewModelScope.launch {
//            val signedIn = isUserSignedIn()
//            if (!signedIn) {
//                _events.emit(CheckoutEvent.NavigateToAuth)
//                return@launch
//            }
//
//            _uiState.value = CheckoutUiState.PlacingOrder(snapshot = ready)
//
//            val result = placeOrder()
//            result
//                .onSuccess {
//                    _events.emit(CheckoutEvent.OrderPlaced)
//                }
//                .onFailure { e ->
//                    _uiState.value = CheckoutUiState.Error(
//                        snapshot = ready,
//                        message = e.message ?: "Something went wrong. Please try again.",
//                    )
//                }
//        }
    }

    fun onErrorDismissed() {
        _uiState.update { state ->
            when (state) {
                is CheckoutUiState.Error -> state
                else -> state
            }
        }
    }

    fun generatePickupDays(count: Int = 7): List<PickUpDay> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
        val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

        return (0 until count).map { i ->
            val date = today.plusDays(i.toLong())
            val labelTop = when (i) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> date.format(dayNameFormatter)
            }
            PickUpDay(
                id = date.toString(), // e.g., "2023-10-27"
                labelTop = labelTop,
                labelBottom = date.format(formatter),
                availableTimeSlots = generateTimeSlots(date),
            )
        }.filter { it.availableTimeSlots.isNotEmpty() }
    }

    fun generateTimeSlots(selectedDate: LocalDate): List<PickUpTimeSlot> {
        val now = LocalTime.now()
        val isToday = selectedDate == LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val slots = mutableListOf<PickUpTimeSlot>()

        // Start from the beginning of the day (e.g., 9 AM) or now
        var current = LocalTime.of(9, 0)
        val endTime = LocalTime.of(23, 0) // End at 11 PM

        while (current.isBefore(endTime)) {
            val next = current.plusMinutes(15)

            // If it's today, skip slots that have already passed
            val isFuture = !isToday || current.isAfter(now.plusMinutes(15)) // 15m buffer for prep

            if (isFuture) {
                val label = "${current.format(formatter)} - ${next.format(formatter)}"
                slots.add(PickUpTimeSlot(id = current.toString(), label = label))
            }
            current = next
        }
        return slots
    }
}
