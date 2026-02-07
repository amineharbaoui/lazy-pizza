package com.example.checkout.order.ui.pastorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DsStatusPill
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaTheme
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.model.OrderStatus

@Composable
fun OrderCard(
    orderUi: OrderUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.SurfaceHigher,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = orderUi.orderNumberLabel,
                    style = AppTypography.Title3SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = orderUi.dateTimeLabel,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(16.dp))

                orderUi.items.forEach { item ->
                    Text(
                        text = item.name,
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    item.toppings.forEach { topping ->
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = topping,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = AppTypography.Body3Regular,
                            color = AppColors.TextSecondary,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                val (text, style) = when (orderUi.status) {
                    OrderStatus.COMPLETED -> "Completed" to DsStatusPill.Style.COMPLETED
                    OrderStatus.IN_PROGRESS -> "In Progress" to DsStatusPill.Style.IN_PROGRESS
                    OrderStatus.CANCELED -> "Canceled" to DsStatusPill.Style.CANCELLED
                }
                DsStatusPill.Pill(
                    text = text,
                    style = style,
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "Total amount:",
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextSecondary,
                    )
                    Text(
                        text = orderUi.totalAmountLabel,
                        style = AppTypography.Title2SemiBold,
                        color = AppColors.TextPrimary,
                    )
                }
            }
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun OrderCardPreview() {
    LazyPizzaTheme {
        OrderCard(
            orderUi = OrderUi(
                orderNumberLabel = "Order #12346",
                dateTimeLabel = "September 25, 12:15",
                items = listOf(
                    OrderItem(
                        name = "1 x Margherita",
                        toppings = listOf(
                            "1 x Extra cheese",
                            "2 x Pepperoni",
                        ),
                    ),
                    OrderItem(
                        name = "1 x Margherita",
                        toppings = listOf(
                            "1 x Extra cheese",
                            "2 x Pepperoni",
                        ),
                    ),
                ),
                totalAmountLabel = "$25.45",
                status = OrderStatus.COMPLETED,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
