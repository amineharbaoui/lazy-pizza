package com.example.order.ui.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.order.ui.checkout.R
import com.example.core.designsystem.R as DSR

@Composable
fun ConfirmationOrderComponent(
    orderNumber: String,
    pickupTime: String,
    onBackToMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isWideLayout()) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(DSR.drawable.loading),
                contentDescription = null,
                modifier = Modifier.size(128.dp),
            )
            OrderDetails(
                orderNumber = orderNumber,
                pickupTime = pickupTime,
                onBackToMenuClick = onBackToMenuClick,
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(DSR.drawable.loading),
                contentDescription = null,
                modifier = Modifier.size(128.dp),
            )
            OrderDetails(
                orderNumber = orderNumber,
                pickupTime = pickupTime,
                onBackToMenuClick = onBackToMenuClick,
            )
        }
    }
}

@Composable
fun OrderDetails(
    orderNumber: String,
    pickupTime: String,
    onBackToMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.your_order_has_been_placed),
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.confirmation_order),
            style = AppTypography.Body2Regular,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceHigher),
            border = BorderStroke(1.dp, AppColors.Outline),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.order_number),
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextSecondary,
                    )
                    Text(
                        text = orderNumber,
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextPrimary,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.pickup_time),
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextSecondary,
                    )
                    Text(
                        text = pickupTime,
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextPrimary,
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        DsButton.Text(
            text = "Back to Menu",
            onClick = onBackToMenuClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun ConfirmationOrderComponentPreview() {
    LazyPizzaThemePreview {
        ConfirmationOrderComponent(
            orderNumber = "#12345",
            pickupTime = "SEPTEMBER 25, 12:15",
            onBackToMenuClick = {},
        )
    }
}
