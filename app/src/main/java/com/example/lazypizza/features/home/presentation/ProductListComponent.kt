package com.example.lazypizza.features.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.DsCardRow
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.data.sample.HomeSampleData
import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.Product
import com.example.lazypizza.features.home.domain.models.ProductCategory.PIZZA
import com.example.lazypizza.ui.theme.AppColors
import com.example.lazypizza.ui.theme.AppTypography
import com.example.lazypizza.ui.theme.LazyPizzaThemePreview
import java.text.NumberFormat

@Composable
fun ProductList(
    sections: List<CategorySection>,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isWide: Boolean,
    listState: LazyListState,
    gridState: LazyGridState
) {
    val isEmpty = sections.isEmpty() || sections.all { it.products.isEmpty() }
    if (isEmpty) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_products_found),
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextSecondary
                )
            }
        }
    } else {
        if (isWide) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    item(span = { GridItemSpan(2) }) {
                        SectionHeader(section.category.value)
                    }
                    items(
                        items = section.products,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    item {
                        SectionHeader(section.category.value)
                    }
                    items(
                        items = section.products,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
) {
    when (product.category) {
        PIZZA -> {
            DsCardRow.MenuItem(
                title = product.name,
                description = product.description,
                price = product.price.toString(),
                image = rememberAsyncImagePainter(product.imageUrl),
                onClick = { onProductClick(product) },
            )
        }

        else -> {
            var qty by rememberSaveable(product.id) { mutableIntStateOf(0) }

            AnimatedContent(
                targetState = qty > 0,
                label = "cardSwitch",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                }
            ) { inCart ->
                if (!inCart) {
                    DsCardRow.AddToCartItem(
                        title = product.name,
                        price = product.price.asMoney(),
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onAddToCart = { qty = 1 },
                        onClick = { onProductClick(product) }
                    )
                } else {
                    DsCardRow.CartItem(
                        title = product.name,
                        unitPrice = product.price,
                        quantity = qty,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onIncrease = { qty += 1 },
                        onDecrease = {
                            if (qty <= 1) qty = 0 else qty -= 1
                        },
                        onRemove = { qty = 0 },
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

private fun Double.asMoney(): String = NumberFormat.getCurrencyInstance().format(this)

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = AppTypography.Label2SemiBold,
        color = AppColors.TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@PreviewPhoneTablet
@Composable
private fun ProductListPreview() {
    LazyPizzaThemePreview {
        ProductList(
            sections = HomeSampleData.sampleSections,
            onProductClick = { },
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg),
            isWide = false,
            listState = LazyListState(),
            gridState = LazyGridState(),
        )
    }
}