package com.example.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.cart.R
import com.example.cart.presentation.RecommendedItemDisplayModel
import com.example.designsystem.components.DsCardItem
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography

@Composable
fun RecommendationsSection(
    recommendedItems: List<RecommendedItemDisplayModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceHigher)
    ) {
        Text(
            text = stringResource(R.string.recommended_title),
            style = AppTypography.Label2SemiBold,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            recommendedItems.forEach { item ->
                DsCardItem.ExtraItem(
                    title = item.title,
                    price = item.priceFormatted,
                    image = rememberAsyncImagePainter(item.imageUrl),
                    onAdd = {}
                )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}
