package com.example.lazypizza.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.DsCardRow
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.Product
import com.example.lazypizza.features.home.domain.ProductCategory
import com.example.lazypizza.features.home.domain.ProductCategory.PIZZA
import com.example.lazypizza.ui.theme.AppColors
import com.example.lazypizza.ui.theme.AppTypography
import com.example.lazypizza.ui.theme.LazyPizzaThemePreview

@Composable
fun ProductList(
    sections: List<CategorySection>,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp >= 840
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
                state = rememberLazyGridState(),
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
                state = rememberLazyListState(),
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
        PIZZA -> DsCardRow.MenuItem(
            title = product.name,
            description = product.description,
            price = product.price.toString(),
            image = painterResource(R.drawable.img_pizza),
            onClick = {
                onProductClick(product)
            },
        )

        else -> DsCardRow.AddToCartItem(
            title = product.name,
            price = product.description,
            image = painterResource(R.drawable.img_bacon),
            onAddToCart = {},
            onClick = {
                onProductClick(product)
            },
        )
    }
}

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
        val products = sampleProducts()
        val sections = ProductCategory.entries.map { category ->
            CategorySection(
                category = category,
                products = products.filter { it.category == category }
            )
        }

        ProductList(
            sections = sections,
            onProductClick = { },
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg)
        )
    }
}