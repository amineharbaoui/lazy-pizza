package com.example.order.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.core.ui.utils.formatting.formatOrderNumber
import com.example.core.ui.utils.formatting.toPickupDisplayLabel
import com.example.order.domain.usecase.CreateOrderUseCase
import com.example.order.domain.usecase.GetPickupScheduleUseCase
import com.example.order.domain.usecase.PlaceOrderUseCase
import com.example.order.ui.checkout.mapper.CartToOrderMapper
import com.example.order.ui.checkout.mapper.CheckoutUiStateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    getPickupScheduleUseCase: GetPickupScheduleUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    checkoutUiStateFactory: CheckoutUiStateFactory,
    private val cartToOrderMapper: CartToOrderMapper,
    private val clearCartUseCase: ClearCartUseCase,
) : ViewModel() {

    private val pickupChoice = MutableStateFlow(PickupChoice())
    private val comment = MutableStateFlow("")
    private val orderSubmission = MutableStateFlow<OrderSubmission>(OrderSubmission.NotSent)

    private val readyStates = observeCartUseCase()
        .onEach { clearFailedSubmission() }
        .map { cart -> checkoutUiStateFactory.createInitialReadyState(cart = cart, schedule = getPickupScheduleUseCase()) }

    val uiState: StateFlow<CheckoutUiState> = combine(
        readyStates,
        pickupChoice,
        comment,
        orderSubmission,
    ) { readyState, pickup, comment, submission ->
        when (submission) {
            OrderSubmission.NotSent -> readyState.withChoices(choice = pickup, comment = comment, isPlacingOrder = false)
            OrderSubmission.Placing -> readyState.withChoices(choice = pickup, comment = comment, isPlacingOrder = true)
            is OrderSubmission.Placed -> CheckoutUiState.OrderPlaced(
                orderNumber = submission.orderNumber,
                pickupTime = submission.pickupTime,
            )

            is OrderSubmission.Failed -> CheckoutUiState.Error(message = submission.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = CheckoutUiState.Loading,
    )

    private val _events = MutableSharedFlow<CheckoutEvent>()
    val events = _events.asSharedFlow()

    fun selectPickupOption(optionId: PickupOption) {
        viewModelScope.launch {
            if (optionId == PickupOption.SCHEDULE) {
                _events.emit(CheckoutEvent.ShowScheduleBottomSheet)
            }
            updatePickupChoice { choice, sheet ->
                when (optionId) {
                    PickupOption.ASAP -> choice.copy(option = PickupOption.ASAP)

                    PickupOption.SCHEDULE -> {
                        val confirmation = choice.confirmation
                        val selection = if (confirmation != null) {
                            PickupSelectionDisplayModel(selectedDayId = confirmation.dayId, selectedTimeSlotId = confirmation.timeSlotId)
                        } else {
                            val firstDay = sheet.days.firstOrNull { it.timeSlots.isNotEmpty() }
                            PickupSelectionDisplayModel(
                                selectedDayId = firstDay?.id,
                                selectedTimeSlotId = firstDay?.timeSlots?.firstOrNull()?.id,
                            )
                        }
                        choice.copy(option = PickupOption.SCHEDULE, selection = selection)
                    }
                }
            }
        }
    }

    fun updateComment(text: String) {
        if (uiState.value is CheckoutUiState.ReadyToOrder) {
            comment.value = text
        }
    }

    fun selectScheduleDay(dayId: String) {
        updatePickupChoice { choice, sheet ->
            val firstSlotId = sheet.days.find { it.id == dayId }?.timeSlots?.firstOrNull()?.id
            choice.copy(selection = PickupSelectionDisplayModel(selectedDayId = dayId, selectedTimeSlotId = firstSlotId))
        }
    }

    fun selectScheduleTimeSlot(timeSlotId: String) {
        updatePickupChoice { choice, _ ->
            choice.copy(selection = choice.selection.copy(selectedTimeSlotId = timeSlotId))
        }
    }

    fun confirmSchedule() {
        updatePickupChoice { choice, sheet ->
            val day = sheet.days.find { it.id == choice.selection.selectedDayId } ?: return@updatePickupChoice choice
            val slot = day.timeSlots.find { it.id == choice.selection.selectedTimeSlotId } ?: return@updatePickupChoice choice

            choice.copy(
                option = PickupOption.SCHEDULE,
                confirmation = PickupConfirmationDisplayModel(dayId = day.id, timeSlotId = slot.id),
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

            val state = uiState.value as? CheckoutUiState.ReadyToOrder ?: return@launch
            val cart = observeCartUseCase().firstOrNull() ?: return@launch

            orderSubmission.value = OrderSubmission.Placing

            val order = createOrderUseCase(
                userId = userId,
                pickup = cartToOrderMapper.mapPickupRequest(state),
                comment = state.comment,
                items = cartToOrderMapper.mapItems(cart),
                total = cart.subtotal,
            )

            val result = placeOrderUseCase(order)

            result.fold(
                onSuccess = {
                    clearCartUseCase()
                    orderSubmission.value = OrderSubmission.Placed(
                        orderNumber = order.orderNumber.formatOrderNumber(),
                        pickupTime = order.pickupAt.toPickupDisplayLabel().uppercase(),
                    )
                },
                onFailure = { e ->
                    orderSubmission.value = OrderSubmission.Failed(message = e.message ?: "Could not place order")
                },
            )
        }
    }

    private fun updatePickupChoice(block: (PickupChoice, SchedulePickupDisplayModel) -> PickupChoice) {
        val sheet = (uiState.value as? CheckoutUiState.ReadyToOrder)?.pickup?.scheduleSheet ?: return
        pickupChoice.update { choice -> block(choice.validIn(sheet), sheet) }
    }

    // The schedule is rebuilt from the clock with each cart, so a slot chosen earlier may have passed: then pickup goes back to ASAP.
    private fun PickupChoice.validIn(sheet: SchedulePickupDisplayModel): PickupChoice {
        val isSelectionValid = selection.selectedDayId == null || sheet.hasSlot(selection.selectedDayId, selection.selectedTimeSlotId)
        val isConfirmationValid = confirmation == null || sheet.hasSlot(confirmation.dayId, confirmation.timeSlotId)
        return if (isSelectionValid && isConfirmationValid) this else PickupChoice()
    }

    private fun SchedulePickupDisplayModel.hasSlot(
        dayId: String,
        timeSlotId: String?,
    ): Boolean = days.any { day -> day.id == dayId && day.timeSlots.any { it.id == timeSlotId } }

    // A new cart brings the form back after a failed order, as the screen did before it derived its state.
    private fun clearFailedSubmission() {
        orderSubmission.update { submission -> if (submission is OrderSubmission.Failed) OrderSubmission.NotSent else submission }
    }

    private fun CheckoutUiState.ReadyToOrder.withChoices(
        choice: PickupChoice,
        comment: String,
        isPlacingOrder: Boolean,
    ): CheckoutUiState.ReadyToOrder {
        val sheet = this.pickup.scheduleSheet
        val pickup = choice.validIn(sheet)
        val confirmedDay = sheet.days.find { it.id == pickup.confirmation?.dayId }
        val confirmedSlot = confirmedDay?.timeSlots?.find { it.id == pickup.confirmation?.timeSlotId }
        val scheduleCard = this.pickup.scheduleCard

        return copy(
            pickup = this.pickup.copy(
                selectedOption = pickup.option,
                asapCard = this.pickup.asapCard.copy(isSelected = pickup.option == PickupOption.ASAP),
                scheduleCard = scheduleCard.copy(
                    isSelected = pickup.option == PickupOption.SCHEDULE,
                    dateLabel = if (confirmedSlot != null) {
                        "${confirmedDay.dayLabel} - ${confirmedDay.dateLabel}"
                    } else {
                        scheduleCard.dateLabel
                    },
                    timeLabel = confirmedSlot?.timeLabel ?: scheduleCard.timeLabel,
                ),
                scheduleSheet = sheet.copy(selection = pickup.selection, confirmation = pickup.confirmation),
            ),
            comment = comment,
            isPlacingOrder = isPlacingOrder,
        )
    }

    private data class PickupChoice(
        val option: PickupOption = PickupOption.ASAP,
        val selection: PickupSelectionDisplayModel = PickupSelectionDisplayModel(selectedDayId = null, selectedTimeSlotId = null),
        val confirmation: PickupConfirmationDisplayModel? = null,
    )

    private sealed interface OrderSubmission {
        data object NotSent : OrderSubmission

        data object Placing : OrderSubmission

        data class Placed(
            val orderNumber: String,
            val pickupTime: String,
        ) : OrderSubmission

        data class Failed(val message: String) : OrderSubmission
    }
}
