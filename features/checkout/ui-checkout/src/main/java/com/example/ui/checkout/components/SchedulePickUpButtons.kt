package com.example.ui.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout
import com.example.ui.checkout.PickupOption
import com.example.ui.checkout.PickupOptionDisplayModel

@Composable
fun SchedulePickUpButtons(
    selectedOption: PickupOption,
    asapOption: PickupOptionDisplayModel,
    scheduleOption: PickupOptionDisplayModel,
    onOptionSelect: (PickupOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isWideLayout()) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PickUpCard(
                title = asapOption.title,
                dateLabel = asapOption.dateLabel,
                timeLabel = asapOption.timeLabel,
                modifier = Modifier.weight(1f),
                selected = selectedOption == PickupOption.ASAP,
                onClick = { onOptionSelect(PickupOption.ASAP) },
            )
            PickUpCard(
                title = scheduleOption.title,
                dateLabel = scheduleOption.dateLabel,
                timeLabel = scheduleOption.timeLabel,
                modifier = Modifier.weight(1f),
                selected = selectedOption == PickupOption.SCHEDULE,
                onClick = { onOptionSelect(PickupOption.SCHEDULE) },
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PickUpCard(
                title = asapOption.title,
                dateLabel = asapOption.dateLabel,
                timeLabel = asapOption.timeLabel,
                modifier = Modifier.fillMaxWidth(),
                selected = selectedOption == PickupOption.ASAP,
                onClick = { onOptionSelect(PickupOption.ASAP) },
            )
            PickUpCard(
                title = scheduleOption.title,
                dateLabel = scheduleOption.dateLabel,
                timeLabel = scheduleOption.timeLabel,
                modifier = Modifier.fillMaxWidth(),
                selected = selectedOption == PickupOption.SCHEDULE,
                onClick = { onOptionSelect(PickupOption.SCHEDULE) },
            )
        }
    }
}

@Composable
private fun PickUpCard(
    title: String,
    dateLabel: String?,
    timeLabel: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.height(80.dp),
        onClick = onClick,
        border = if (selected) {
            BorderStroke(1.dp, AppColors.Primary)
        } else {
            CardDefaults.outlinedCardBorder(enabled = true)
        },
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 2.dp, alignment = Alignment.CenterVertically),
        ) {
            Text(
                text = title,
                style = AppTypography.Label1Medium,
            )
            dateLabel?.let {
                Text(
                    text = it,
                    style = AppTypography.Body4Regular,
                    color = AppColors.TextSecondary,
                )
            }
            timeLabel?.let {
                Text(
                    text = it,
                    style = AppTypography.Body4Regular,
                    color = AppColors.TextSecondary,
                )
            }
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun SchedulePickUpButtonsPreview() {
    LazyPizzaThemePreview {
        SchedulePickUpButtons(
            selectedOption = PickupOption.ASAP,
            asapOption = PickupOptionDisplayModel(
                title = "Earliest available",
                dateLabel = "Today - 28 Dec",
                timeLabel = "9:00 AM - 10:00 AM",
            ),
            scheduleOption = PickupOptionDisplayModel(
                title = "Schedule",
                dateLabel = "Tomorrow - 29 Dec",
                timeLabel = "9:00 AM - 10:00 AM",
            ),
            onOptionSelect = {},
        )
    }
}
