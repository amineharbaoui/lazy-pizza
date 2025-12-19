package com.example.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTextField
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.components.card.DsExpandableCard
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.ui.checkout.components.OrderSummaryContent
import com.example.ui.checkout.components.SchedulePickUpButtons
import com.example.ui.checkout.components.SchedulePickUpContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onOrderPlace: () -> Unit,
    onOrderItemClick: (productId: String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var shouldShowBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CheckoutEvent.NavigateToAuth -> onNavigateToAuth()
                CheckoutEvent.OrderPlaced -> onOrderPlace()
                CheckoutEvent.ShowScheduleBottomSheet -> shouldShowBottomSheet = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        DsTopBar.Secondary(
            title = "Checkout",
            onBackClick = onBackClick,
        )
        when (val state = uiState) {
            CheckoutUiState.Loading -> {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Loading…",
                    style = AppTypography.Body2Regular,
                    color = AppColors.TextSecondary,
                )
            }

            is CheckoutUiState.Ready -> {
                CheckoutScreenContent(
                    state = state,
                    onPickupOptionSelect = viewModel::selectPickupOption,
                    onCommentChange = viewModel::updateComment,
                    onPlaceOrderClick = viewModel::submitOrder,
                    onOrderItemClick = onOrderItemClick,
                    modifier = Modifier.weight(1f),
                )
            }

            is CheckoutUiState.Error -> {}
        }
    }

    if (shouldShowBottomSheet && uiState is CheckoutUiState.Ready) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { shouldShowBottomSheet = false },
            containerColor = AppColors.Bg,
            content = {
                SchedulePickUpContent(
                    schedulePickUpDisplayModel = (uiState as CheckoutUiState.Ready).pickup.schedule,
                    onSelectDay = { dayId ->
                        viewModel.selectScheduleDay(dayId)
                    },
                    onTimeSlotSelect = { slotId ->
                        viewModel.selectScheduleTimeSlot(slotId)
                    },
                    onScheduleClick = {
                        viewModel.confirmSchedule()
                        shouldShowBottomSheet = false
                    },
                )
            },
        )
    }
}

@Composable
private fun CheckoutScreenContent(
    state: CheckoutUiState.Ready,
    onPickupOptionSelect: (PickupOption) -> Unit,
    onCommentChange: (String) -> Unit,
    onPlaceOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlacingOrder: Boolean = false,
    errorMessage: String? = null,
    onOrderItemClick: (productId: String?) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "PICKUP TIME",
                style = AppTypography.Label2SemiBold,
                color = AppColors.TextSecondary,
            )

            SchedulePickUpButtons(
                selectedOption = state.pickup.selectedOption,
                asapOption = state.pickup.asapOption,
                scheduleOption = state.pickup.scheduleOption,
                onOptionSelect = onPickupOptionSelect,
            )

            HorizontalDivider()

            DsExpandableCard(
                title = "Order Summary",
                content = {
                    state.orderSummary.lines.forEachIndexed { index, line ->
                        OrderSummaryContent(
                            title = line.title,
                            priceLabel = line.basePriceLabel,
                            subtitleLines = line.subtitleLines,
                            totalPriceLabel = line.totalPriceLabel,
                            onOrderItemClick = {
                                if (line is OrderLineUi.MainProduct) {
                                    onOrderItemClick(line.productId)
                                } else {
                                    onOrderItemClick(null)
                                }
                            },
                        )
                        if (index < state.orderSummary.lines.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                },
            )

            HorizontalDivider()

            Text(
                text = "COMMENTS",
                style = AppTypography.Label2SemiBold,
                color = AppColors.TextSecondary,
            )

            DsTextField.Primary(
                modifier = Modifier.fillMaxWidth(),
                value = state.comment,
                onValueChange = onCommentChange,
                placeholder = "Add Comment",
                singleLine = false,
                minLines = 3,
                maxLines = 3,
            )

            errorMessage?.let {
                Text(
                    text = it,
                    style = AppTypography.Body4Regular,
                    color = AppColors.Error,
                )
            }
        }

        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "ORDER TOTAL",
                style = AppTypography.Label2SemiBold,
                color = AppColors.TextSecondary,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = state.orderSummary.totalLabel,
                style = AppTypography.Title3SemiBold,
                color = AppColors.TextPrimary,
            )
        }

        DsButton.Filled(
            text = if (isPlacingOrder) "Placing…" else "Place Order",
            enabled = state.canPlaceOrder && !isPlacingOrder,
            onClick = onPlaceOrderClick,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
        )
    }
}

@PreviewPhoneTablet
@Preview
@Composable
private fun CheckoutScreenContentPreview() {
    LazyPizzaThemePreview {
        val sampleState = CheckoutUiState.Ready(
            pickup = PickupUiState(
                selectedOption = PickupOption.ASAP,
                asapOption = PickupOptionDisplayModel(title = "Earliest available"),
                scheduleOption = PickupOptionDisplayModel(title = "Schedule", dateLabel = "Choose a time"),
                schedule = SchedulePickUpDisplayModel(
                    days = listOf(),
                    selection = PickUpSelection(
                        selectedDayId = "tritani",
                        selectedTimeSlotId = "eius",
                    ),
                    confirmation = null,
                ),
            ),
            orderSummary = OrderSummaryUi(
                lines = listOf(
                    OrderLineUi.MainProduct(
                        productId = "1",
                        title = "Pizza Margherita",
                        basePriceLabel = "$10.00",
                        subtitleLines = listOf("Extra cheese ($0.5)", "Spicy sauce ($0.5)"),
                        totalPriceLabel = "$12.00",
                    ),
                    OrderLineUi.SecondaryProduct(
                        title = "Garlic Bread",
                        basePriceLabel = "$2.00",
                        subtitleLines = emptyList(),
                        totalPriceLabel = null,
                    ),
                ),
                totalLabel = "$12.00",
            ),
            comment = "",
            canPlaceOrder = true,
        )

        CheckoutScreenContent(
            state = sampleState,
            onPickupOptionSelect = {},
            onCommentChange = {},
            onPlaceOrderClick = {},
            isPlacingOrder = false,
            onOrderItemClick = {},
        )
    }
}
