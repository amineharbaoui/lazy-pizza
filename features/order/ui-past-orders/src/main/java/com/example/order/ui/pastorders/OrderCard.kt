package com.example.order.ui.pastorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsStatusPill
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaTheme
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.model.OrderStatus

@Composable
fun OrderCard(
    order: OrderDisplayModel,
    isReorderEnabled: Boolean,
    onReorderClick: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                val (text, style) = when (order.status) {
                    OrderStatus.COMPLETED -> stringResource(R.string.past_orders_status_completed) to DsStatusPill.Style.COMPLETED
                    OrderStatus.IN_PROGRESS -> stringResource(R.string.past_orders_status_in_progress) to DsStatusPill.Style.IN_PROGRESS
                    OrderStatus.CANCELED -> stringResource(R.string.past_orders_status_canceled) to DsStatusPill.Style.CANCELLED
                }
                DsStatusPill.Pill(
                    text = text,
                    style = style,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val reorderDescription = stringResource(R.string.past_orders_reorder_description, order.orderNumber)
                DsButton.Outlined(
                    text = stringResource(R.string.past_orders_reorder),
                    onClick = onReorderClick,
                    enabled = isReorderEnabled,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = reorderDescription },
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = stringResource(R.string.past_orders_order_total_amount),
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = order.totalAmountLabel,
                        style = AppTypography.Title2SemiBold,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.End,
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
            isReorderEnabled = true,
            onReorderClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
