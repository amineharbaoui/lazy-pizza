package com.example.cart.ui.cart.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.cart.ui.cart.RecommendedItemDisplayModel
import com.example.designsystem.R
import com.example.designsystem.components.card.DsCardItem
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.model.ProductCategory

@Composable
fun RecommendationsSection(
    recommendedItems: List<RecommendedItemDisplayModel>,
    onAddToCart: (item: RecommendedItemDisplayModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.recommended_title),
            style = AppTypography.Label2SemiBold,
            color = AppColors.TextSecondary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            recommendedItems.forEach { item ->
                DsCardItem.ExtraItem(
                    title = item.name,
                    price = item.unitPriceFormatted,
                    image = rememberAsyncImagePainter(item.imageUrl),
                    onAdd = { onAddToCart(item) },
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun RecommendationsSectionPreview() {
    val recommendedItems = listOf(
        RecommendedItemDisplayModel(
            id = "1",
            name = "Extra Fries",
            unitPrice = 2.50,
            unitPriceFormatted = "$2.50",
            imageUrl = "https://example.com/fries.jpg",
            category = ProductCategory.DRINK,
        ),
        RecommendedItemDisplayModel(
            id = "2",
            name = "Cola",
            unitPrice = 1.50,
            unitPriceFormatted = "$1.50",
            imageUrl = "https://example.com/cola.jpg",
            category = ProductCategory.DRINK,
        ),
    )
    LazyPizzaThemePreview {
        RecommendationsSection(
            recommendedItems = recommendedItems,
            onAddToCart = {},
        )
    }
}
