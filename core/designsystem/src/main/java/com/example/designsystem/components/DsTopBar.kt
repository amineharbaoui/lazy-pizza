package com.example.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview

object DsTopBar {
    @Composable
    fun Primary(
        phoneNumber: String,
        onPhoneClick: (phoneNumber: String) -> Unit,
        modifier: Modifier = Modifier,
        isLoggedIn: Boolean = false,
        onAccountClick: () -> Unit = {},
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LazzyPizza",
                    style = AppTypography.Body3Bold,
                    color = AppColors.TextPrimary,
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = {
                        onPhoneClick(phoneNumber)
                    })
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(R.drawable.phone),
                    contentDescription = "Phone Number Icon",
                    tint = AppColors.TextSecondary,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = phoneNumber,
                    style = AppTypography.Body1Regular,
                    color = AppColors.TextPrimary,
                )
                Spacer(Modifier.width(6.dp))
                IconButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = if (isLoggedIn) AppColors.Primary_8 else AppColors.TextSecondary_8,
                            shape = CircleShape,
                        )
                        .size(32.dp),
                    onClick = onAccountClick,
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = if (isLoggedIn) painterResource(R.drawable.logout) else painterResource(R.drawable.user),
                        contentDescription = "Phone Number Icon",
                        tint = if (isLoggedIn) AppColors.Primary else AppColors.TextSecondary,
                    )
                }
            }
        }
    }

    @Composable
    fun Secondary(
        modifier: Modifier = Modifier,
        title: String? = null,
        onBackClick: (() -> Unit)? = null,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            onBackClick?.let {
                IconButton(
                    modifier = Modifier
                        .size(44.dp),
                    onClick = it,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.arrow_left),
                        contentDescription = "Phone Number Icon",
                        tint = AppColors.TextSecondary,
                    )
                }
            }
            title?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = AppTypography.Body1Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    LazyPizzaThemePreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsTopBar.Primary(
                phoneNumber = "+1 (555) 321-7890",
                onPhoneClick = { },
            )
            DsTopBar.Primary(
                phoneNumber = "+1 (555) 321-7890",
                onPhoneClick = { },
                isLoggedIn = true,
            )
            DsTopBar.Secondary(onBackClick = { })
            DsTopBar.Secondary(onBackClick = { }, title = "History")
            DsTopBar.Secondary(title = "History")
        }
    }
}
