package com.example.order.ui.checkout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet

@Composable
fun OrderSummaryContent(
    title: String,
    priceLabel: String,
    subtitleLines: List<String>,
    modifier: Modifier = Modifier,
    totalPriceLabel: String? = null,
    onOrderItemClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onOrderItemClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = priceLabel,
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            subtitleLines.forEach { subtitleLine ->
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = subtitleLine,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                )
            }
            totalPriceLabel?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Total: $it",
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun OrderSummaryContentPreview() {
    LazyPizzaThemePreview {
        for (i in 0..3) {
            OrderSummaryContent(
                title = "2 x Pizza Margherita",
                priceLabel = "$12.00",
                subtitleLines = listOf(
                    "1 x Extra Cheese ($0.5)",
                    "3 x Extra Cheese ($1.5)",
                ),
                totalPriceLabel = "$15.00",
                onOrderItemClick = {},
            )
        }
    }
}
