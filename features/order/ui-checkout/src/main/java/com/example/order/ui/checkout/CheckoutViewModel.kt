package com.example.order.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.menu.utils.formatting.formatOrderNumber
import com.example.menu.utils.formatting.toPickupDisplayLabel
import com.example.order.domain.usecase.PlaceOrderUseCase
import com.example.order.ui.checkout.mapper.CartToOrderMapper
import com.example.order.ui.checkout.mapper.CheckoutUiStateFactory
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
    private val checkoutUiStateFactory: CheckoutUiStateFactory,
    private val cartToOrderMapper: CartToOrderMapper,
    private val clearCartUseCase: ClearCartUseCase,
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
                _uiState.update { currentState ->
                    currentState as? CheckoutUiState.OrderPlaced ?: checkoutUiStateFactory.createInitialReadyState(cart)
                }
            }
        }
    }

    fun selectPickupOption(optionId: PickupOption) {
        viewModelScope.launch {
            if (optionId == PickupOption.SCHEDULE) {
                _events.emit(CheckoutEvent.ShowScheduleBottomSheet)
            }
            updateReadyState { state ->
                when (optionId) {
                    PickupOption.ASAP -> state.copy(
                        pickup = state.pickup.copy(
                            selectedOption = PickupOption.ASAP,
                            asapCard = state.pickup.asapCard.copy(isSelected = true),
                            scheduleCard = state.pickup.scheduleCard.copy(isSelected = false),
                        ),
                    )

                    PickupOption.SCHEDULE -> {
                        val sheet = state.pickup.scheduleSheet
                        val confirmation = sheet.confirmation

                        val (selectedDayId, selectedTimeSlotId) = if (confirmation != null) {
                            confirmation.dayId to confirmation.timeSlotId
                        } else {
                            val firstDay = sheet.days.firstOrNull { it.timeSlots.isNotEmpty() }
                            firstDay?.id to firstDay?.timeSlots?.firstOrNull()?.id
                        }

                        state.copy(
                            pickup = state.pickup.copy(
                                selectedOption = PickupOption.SCHEDULE,
                                asapCard = state.pickup.asapCard.copy(isSelected = false),
                                scheduleCard = state.pickup.scheduleCard.copy(isSelected = true),
                                scheduleSheet = sheet.copy(
                                    selection = sheet.selection.copy(
                                        selectedDayId = selectedDayId,
                                        selectedTimeSlotId = selectedTimeSlotId,
                                    ),
                                ),
                            ),
                        )
                    }
                }
            }
        }
    }

    fun updateComment(text: String) {
        updateReadyState { it.copy(comment = text) }
    }

    fun selectScheduleDay(dayId: String) {
        updateReadyState { state ->
            val sheet = state.pickup.scheduleSheet
            val selectedDay = sheet.days.find { it.id == dayId }
            val firstSlotId = selectedDay?.timeSlots?.firstOrNull()?.id

            state.copy(
                pickup = state.pickup.copy(
                    scheduleSheet = sheet.copy(
                        selection = sheet.selection.copy(
                            selectedDayId = dayId,
                            selectedTimeSlotId = firstSlotId,
                        ),
                    ),
                ),
            )
        }
    }

    fun selectScheduleTimeSlot(timeSlotId: String) {
        updateReadyState { state ->
            val sheet = state.pickup.scheduleSheet
            state.copy(
                pickup = state.pickup.copy(
                    scheduleSheet = sheet.copy(
                        selection = sheet.selection.copy(selectedTimeSlotId = timeSlotId),
                    ),
                ),
            )
        }
    }

    fun confirmSchedule() {
        updateReadyState { state ->
            val sheet = state.pickup.scheduleSheet
            val dayId = sheet.selection.selectedDayId ?: return@updateReadyState state
            val slotId = sheet.selection.selectedTimeSlotId ?: return@updateReadyState state

            val day = sheet.days.find { it.id == dayId } ?: return@updateReadyState state
            val slot = day.timeSlots.find { it.id == slotId } ?: return@updateReadyState state

            state.copy(
                pickup = state.pickup.copy(
                    selectedOption = PickupOption.SCHEDULE,
                    asapCard = state.pickup.asapCard.copy(isSelected = false),
                    scheduleCard = state.pickup.scheduleCard.copy(
                        isSelected = true,
                        dateLabel = "${day.dayLabel} - ${day.dateLabel}",
                        timeLabel = slot.timeLabel,
                    ),
                    scheduleSheet = sheet.copy(
                        confirmation = PickupConfirmationUiModel(dayId = day.id, timeSlotId = slot.id),
                    ),
                ),
            )
        }
    }

    fun submitOrder() {
        viewModelScope.launch {
            val userId = getCurrentUserUidUseCase()
            if (userId == null) {
                _events.emit(CheckoutEvent.NavigateToAuth)
                return@launch
            }

            val state = _uiState.value as? CheckoutUiState.ReadyToOrder ?: return@launch
            val cart = observeCartUseCase().firstOrNull() ?: return@launch

            _uiState.update { (it as? CheckoutUiState.ReadyToOrder)?.copy(isPlacingOrder = true) ?: it }

            val order = cartToOrderMapper.map(cart = cart, userId = userId, checkout = state)

            val result = placeOrderUseCase(order)

            result.fold(
                onSuccess = {
                    clearCartUseCase()
                    _uiState.value = CheckoutUiState.OrderPlaced(
                        orderNumber = order.orderNumber.formatOrderNumber(),
                        pickupTime = order.pickupAt.toPickupDisplayLabel().uppercase(),
                    )
                },
                onFailure = { e ->
                    _uiState.value = CheckoutUiState.Error(message = e.message ?: "Could not place order")
                },
            )
        }
    }

    private fun updateReadyState(block: (CheckoutUiState.ReadyToOrder) -> CheckoutUiState.ReadyToOrder) {
        _uiState.update { state -> if (state is CheckoutUiState.ReadyToOrder) block(state) else state }
    }
}
