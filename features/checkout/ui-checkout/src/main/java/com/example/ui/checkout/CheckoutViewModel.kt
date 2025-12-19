package com.example.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetCurrentUserUidUseCase
import com.example.domain.usecase.ObserveCartUseCase
import com.example.domain.usecase.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
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
                _uiState.value = createInitialReadyState(cart)
            }
        }
    }

    fun selectPickupOption(optionId: PickupOption) {
        viewModelScope.launch {
            if (optionId == PickupOption.SCHEDULE) {
                _events.emit(CheckoutEvent.ShowScheduleBottomSheet)
            }
            updateReadyState { state ->
                if (optionId == PickupOption.SCHEDULE) {
                    val schedule = state.pickup.schedule
                    val confirmation = schedule.confirmation

                    val (selectedDayId, selectedTimeSlotId) = if (confirmation != null) {
                        confirmation.dayId to confirmation.timeSlotId
                    } else {
                        val firstDay = schedule.days.firstOrNull { it.availableTimeSlots.isNotEmpty() }
                        firstDay?.id to firstDay?.availableTimeSlots?.firstOrNull()?.id
                    }

                    state.updatePickupSelection(selectedDayId, selectedTimeSlotId)
                } else {
                    state.updatePickupOption(optionId)
                }
            }
        }
    }

    fun updateComment(text: String) {
        updateReadyState { it.copy(comment = text) }
    }

    fun selectScheduleDay(dayId: String) {
        updateReadyState { state ->
            val selectedDay = state.pickup.schedule.days.find { it.id == dayId }
            val firstSlotId = selectedDay?.availableTimeSlots?.firstOrNull()?.id
            state.updatePickupSelection(dayId, firstSlotId)
        }
    }

    fun selectScheduleTimeSlot(timeSlotId: String) {
        updateReadyState { state ->
            state.updatePickupSelection(
                dayId = state.pickup.schedule.selection.selectedDayId,
                timeSlotId = timeSlotId,
            )
        }
    }

    fun confirmSchedule() {
        updateReadyState { state ->
            val schedule = state.pickup.schedule
            val selectedDayId = schedule.selection.selectedDayId
            val selectedSlotId = schedule.selection.selectedTimeSlotId

            val selectedDay = schedule.days.find { it.id == selectedDayId }
            val selectedSlot = selectedDay?.availableTimeSlots?.find { it.id == selectedSlotId }

            if (selectedDay != null && selectedSlot != null) {
                state.confirmPickupSchedule(
                    dayId = selectedDay.id,
                    dayLabelTop = selectedDay.labelTop,
                    dayLabelBottom = selectedDay.labelBottom,
                    timeSlotId = selectedSlot.id,
                    timeSlotLabel = selectedSlot.label,
                )
            } else {
                state
            }
        }
    }

    fun submitOrder() {
        viewModelScope.launch {
            val userId = getCurrentUserUidUseCase()
            if (userId == null) {
                viewModelScope.launch { _events.emit(CheckoutEvent.NavigateToAuth) }
                return@launch
            }
            val state = _uiState.value as? CheckoutUiState.Ready ?: return@launch
            val cart = observeCartUseCase().firstOrNull()

            viewModelScope.launch {
                _uiState.update { (it as? CheckoutUiState.Ready)?.copy(isPlacingOrder = true) ?: it }

                val order = cart?.toOrder(userId = userId, checkout = state)
                val result = order?.let { placeOrderUseCase(it) }

                result?.fold(
                    onSuccess = {
                        _uiState.update { (it as? CheckoutUiState.Ready)?.copy(isPlacingOrder = false) ?: it }
                        _events.emit(CheckoutEvent.OrderPlaced)
                    },
                    onFailure = { e ->
                        _uiState.value = CheckoutUiState.Error(
                            message = e.message ?: "Could not place order",
                        )
                    },
                )
            }
        }
    }

    private fun updateReadyState(block: (CheckoutUiState.Ready) -> CheckoutUiState.Ready) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Ready) block(state) else state
        }
    }
}
