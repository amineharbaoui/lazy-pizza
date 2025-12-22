package com.example.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaTheme

object DsRadio {

    data class Option<T>(
        val id: T,
        val label: String,
        val enabled: Boolean = true,
    )

    private val roundedCornerShape = RoundedCornerShape(999.dp)
    private val pillHeightDp = 56.dp

    @Composable
    fun <T> GroupPills(
        options: List<Option<T>>,
        selectedId: T,
        onSelected: (T) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            options.forEach { option ->
                Pill(
                    label = option.label,
                    selected = option.id == selectedId,
                    enabled = enabled && option.enabled,
                    onClick = { onSelected(option.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    @Composable
    private fun Pill(
        label: String,
        selected: Boolean,
        enabled: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val borderColor = when {
            selected -> AppColors.Primary
            else -> AppColors.Outline
        }

        Surface(
            modifier = modifier
                .height(pillHeightDp)
                .clip(roundedCornerShape)
                .clickable(
                    enabled = enabled,
                    role = Role.RadioButton,
                    onClick = onClick,
                ),
            color = AppColors.SurfaceHigher,
            shape = roundedCornerShape,
            border = BorderStroke(1.dp, borderColor),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selected,
                    onClick = null,
                    enabled = enabled,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppColors.Primary,
                        unselectedColor = AppColors.TextSecondary,
                        disabledSelectedColor = AppColors.TextSecondary,
                        disabledUnselectedColor = AppColors.TextSecondary,
                    ),
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = label,
                    style = AppTypography.Body3Medium,
                    color = if (enabled) AppColors.TextPrimary else AppColors.TextSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_DsRadio_GroupPills() {
    LazyPizzaTheme {
        val options = listOf(
            DsRadio.Option(id = "asap", label = "Earliest available time"),
            DsRadio.Option(id = "schedule", label = "Schedule time"),
        )

        DsRadio.GroupPills(
            options = options,
            selectedId = "asap",
            onSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
