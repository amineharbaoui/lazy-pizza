package com.example.ui.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DsButton
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet

@Composable
fun ConfirmationOrderComponent(
    orderNumber: String,
    pickupTime: String,
    onBackToMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Your order has been placed!",
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Thank you for your order! Please come at the indicated time.",
            style = AppTypography.Body2Regular,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Bg),
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
                        text = "ORDER NUMBER:",
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
                        text = "PICKUP TIME:",
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
@Preview
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
