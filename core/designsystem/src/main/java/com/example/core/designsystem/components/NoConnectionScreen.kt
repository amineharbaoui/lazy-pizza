package com.example.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.R
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet

@Composable
fun NoConnectionScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.no_connection),
            contentDescription = null,
            modifier = Modifier.size(256.dp),
        )
        Text(
            text = stringResource(R.string.whooops),
            style = AppTypography.Title2SemiBold,
            color = AppColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_internet),
            style = AppTypography.Body2Regular,
            color = AppColors.TextPrimary,
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun NoConnectionScreenPreview() {
    LazyPizzaThemePreview {
        NoConnectionScreen()
    }
}
