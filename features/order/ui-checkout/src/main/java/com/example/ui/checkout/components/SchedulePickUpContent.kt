package com.example.ui.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DsButton
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.ui.checkout.PickupDayUiModel
import com.example.ui.checkout.PickupSelectionUiModel
import com.example.ui.checkout.PickupTimeSlotUiModel
import com.example.ui.checkout.SchedulePickupUiModel

@Composable
fun SchedulePickUpContent(
    modifier: Modifier = Modifier,
    schedulePickupUiModel: SchedulePickupUiModel,
    onScheduleClick: () -> Unit,
    onSelectDay: (dayId: String) -> Unit,
    onTimeSlotSelect: (slotId: String) -> Unit,
) {
    val daysLazyListState = rememberLazyListState()
    val slotsLazyListState = rememberLazyListState()

    val availableTimeSlots = schedulePickupUiModel.days
        .find { it.id == schedulePickupUiModel.selection.selectedDayId }
        ?.timeSlots ?: emptyList()

    LaunchedEffect(schedulePickupUiModel.selection.selectedDayId) {
        val index = schedulePickupUiModel.days.indexOfFirst { it.id == schedulePickupUiModel.selection.selectedDayId }
        if (index >= 0) {
            daysLazyListState.animateScrollToItem(index)
        }
    }

    LaunchedEffect(schedulePickupUiModel.selection.selectedTimeSlotId) {
        val index = availableTimeSlots.indexOfFirst { it.id == schedulePickupUiModel.selection.selectedTimeSlotId }
        if (index >= 0) {
            slotsLazyListState.animateScrollToItem(index)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Schedule your pickup time",
                style = AppTypography.Title2SemiBold,
                color = AppColors.TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(
                state = daysLazyListState,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(schedulePickupUiModel.days) { day ->
                    DayItem(
                        day = day,
                        selected = day.id == schedulePickupUiModel.selection.selectedDayId,
                        enabled = day.timeSlots.isNotEmpty(),
                        onClick = { onSelectDay(day.id) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                state = slotsLazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(availableTimeSlots) { slot ->
                    TimeRangeRow(
                        label = slot.timeLabel,
                        selected = slot.id == schedulePickupUiModel.selection.selectedTimeSlotId,
                        onClick = { onTimeSlotSelect(slot.id) },
                    )
                    HorizontalDivider()
                }
            }
        }

        DsButton.Filled(
            text = "Confirm",
            onClick = onScheduleClick,
            enabled = schedulePickupUiModel.selection.selectedTimeSlotId != null,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun DayItem(
    day: PickupDayUiModel,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.width(120.dp),
        onClick = onClick,
        border = if (selected) {
            BorderStroke(1.dp, AppColors.Primary)
        } else {
            CardDefaults.outlinedCardBorder(enabled = true)
        },
        shape = MaterialTheme.shapes.small,
        enabled = enabled,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = day.dayLabel,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = if (day.timeSlots.isEmpty()) "Closed" else day.dateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TimeRangeRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}

@PreviewPhoneTablet
@Preview
@Composable
private fun SchedulePickUpPreview() {
    val availableTimeSlots = listOf(
        PickupTimeSlotUiModel("0", "9:00 AM"),
        PickupTimeSlotUiModel("1", "10:00 AM"),
        PickupTimeSlotUiModel("2", "11:00 AM"),
        PickupTimeSlotUiModel("3", "12:00 PM"),
    )
    LazyPizzaThemePreview {
        SchedulePickUpContent(
            schedulePickupUiModel = SchedulePickupUiModel(
                days = listOf(
                    PickupDayUiModel(
                        id = "1",
                        dayLabel = "Today",
                        dateLabel = "28 Dec",
                        timeSlots = availableTimeSlots,
                    ),
                    PickupDayUiModel(
                        id = "2",
                        dayLabel = "Tomorrow",
                        dateLabel = "29 Dec",
                        timeSlots = availableTimeSlots,
                    ),
                    PickupDayUiModel(
                        id = "3",
                        dayLabel = "Saturday",
                        dateLabel = "30 Dec",
                        timeSlots = availableTimeSlots,
                    ),
                    PickupDayUiModel(
                        id = "4",
                        dayLabel = "Sunday",
                        dateLabel = "31 Dec",
                        timeSlots = availableTimeSlots,
                    ),
                ),
                selection = PickupSelectionUiModel(
                    selectedDayId = "1",
                    selectedTimeSlotId = "2",
                ),
                confirmation = null,
            ),
            onScheduleClick = {},
            onSelectDay = {},
            onTimeSlotSelect = {},
        )
    }
}
