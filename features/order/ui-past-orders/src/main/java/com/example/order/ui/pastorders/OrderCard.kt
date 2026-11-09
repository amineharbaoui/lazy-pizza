package com.example.order.ui.pastorders

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.DsStatusPill
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaTheme
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.model.OrderStatus

@Composable
fun OrderCard(
    order: OrderDisplayModel,
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
                    text = stringResource(R.string.past_orders_order_number, order.orderNumber),
                    style = AppTypography.Title3SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = order.dateTimeLabel,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(16.dp))

                order.items.forEach { item ->
                    Text(
                        text = stringResource(R.string.past_orders_order_line, item.quantity, item.name),
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    item.toppings.forEach { topping ->
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(R.string.past_orders_order_line, topping.quantity, topping.name),
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
                val (text, style) = when (order.status) {
                    OrderStatus.COMPLETED -> stringResource(R.string.past_orders_status_completed) to DsStatusPill.Style.COMPLETED
                    OrderStatus.IN_PROGRESS -> stringResource(R.string.past_orders_status_in_progress) to DsStatusPill.Style.IN_PROGRESS
                    OrderStatus.CANCELED -> stringResource(R.string.past_orders_status_canceled) to DsStatusPill.Style.CANCELLED
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
                        text = stringResource(R.string.past_orders_order_total_amount),
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextSecondary,
                    )
                    Text(
                        text = order.totalAmountLabel,
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
            order = OrderDisplayModel(
                orderNumber = "12346",
                dateTimeLabel = "September 25, 12:15",
                items = listOf(
                    OrderItemDisplayModel(
                        quantity = 1,
                        name = "Margherita",
                        toppings = listOf(
                            OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                            OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                        ),
                    ),
                    OrderItemDisplayModel(
                        quantity = 1,
                        name = "Margherita",
                        toppings = listOf(
                            OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                            OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
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
