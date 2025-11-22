package com.example.lazypizza.core.designsystem.components

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
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.lazypizza.R

object DsTopBar {
    @Composable
    fun Primary(
        phoneNumber: String,
        onPhoneClick: (phoneNumber: String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LazzyPizza",
                    style = AppTypography.Body3Bold,
                    color = AppColors.TextPrimary
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(R.drawable.ic_phone),
                    contentDescription = "Phone Number Icon",
                    tint = AppColors.TextSecondary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = phoneNumber,
                    style = AppTypography.Body1Regular,
                    color = AppColors.TextPrimary
                )
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
                        .size(44.dp)
                        .background(
                            color = AppColors.TextSecondary_8,
                            shape = CircleShape
                        ),
                    onClick = it,
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_left_arrow),
                        contentDescription = "Phone Number Icon",
                        tint = AppColors.TextSecondary
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
            verticalArrangement = Arrangement.Top,
        ) {
            DsTopBar.Primary(
                phoneNumber = "+1 (555) 321-7890",
                onPhoneClick = { }
            )
            Spacer(Modifier.height(16.dp))
            DsTopBar.Secondary(onBackClick = { })
            Spacer(Modifier.height(16.dp))
            DsTopBar.Secondary(onBackClick = { }, title = "History")
            Spacer(Modifier.height(16.dp))
            DsTopBar.Secondary(title = "History")
        }
    }
}