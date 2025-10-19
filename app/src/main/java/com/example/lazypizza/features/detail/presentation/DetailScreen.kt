package com.example.lazypizza.features.detail.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.core.designsystem.components.DsAppBar
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsCardItem
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.data.utils.HomeSampleData
import com.example.lazypizza.features.home.domain.models.Product
import java.text.NumberFormat

@Composable
fun DetailScreen(
    innerPadding: PaddingValues,
    viewModel: DetailScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DetailUiState.Loading -> DetailLoadingState(innerPadding)
        is DetailUiState.Success -> DetailContent(
            innerPadding = innerPadding,
            product = state.product
        )

        is DetailUiState.Error -> DetailErrorState(innerPadding)
    }
}

@Composable
private fun DetailContent(
    innerPadding: PaddingValues,
    product: Product
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val isWide = windowSizeClass != WindowWidthSizeClass.COMPACT

    Column(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
    ) {
        DsAppBar.Secondary(onBackClick = { /* Nav handled outside */ })

        if (isWide) {
            //WideDetailLayout(product = product)
        } else {
            PhoneDetailLayout(product = product)
        }
    }
}

@Composable
private fun PhoneDetailLayout(product: Product) {
    Column {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(R.drawable.img_pizza),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(16.dp))
        ProductHeaderSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            name = product.name,
            description = product.description,
        )
        AddonExtraSection()
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = "Add to Cart for ${product.price.asMoney()}",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ProductHeaderSection(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = name,
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary
        )
        Text(
            text = description,
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary
        )
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
private fun AddonExtraSection() {
    Column(
        modifier = Modifier
            .background(
                color = AppColors.SurfaceHigher,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Add extra toppings",
            style = AppTypography.Label2SemiBold,
            color = AppColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        AddonGrid()
    }
}

@Composable
private fun AddonGrid() {
    val items = rememberAddonItems()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items) { item ->
            DsCardItem.AddonCard(
                title = item.title,
                priceText = item.price,
                image = painterResource(R.drawable.img_bacon),
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class Addon(
    val title: String,
    val price: String
)

@Composable
private fun rememberAddonItems(): List<Addon> {
    // a simple static list for UI rendering
    return listOf(
        Addon("Bacon", "$1"),
        Addon("Extra Cheese", "$1"),
        Addon("Corn", "$0.50"),
        Addon("Tomato", "$0.50"),
        Addon("Olives", "$0.50"),
        Addon("Pepperoni", "$1"),
        Addon("Mushrooms", "$0.50"),
        Addon("Basil", "$0.50"),
        Addon("Pineapple", "$1"),
        Addon("Onion", "$0.50"),
        Addon("Chili Peppers", "$0.50"),
        Addon("Spinach", "$0.50"),
    )
}

private fun Double.asMoney(): String = NumberFormat.getCurrencyInstance().format(this)

@PreviewPhoneTablet
@Composable
private fun DetailScreenPreview() {
    LazyPizzaThemePreview {
        DetailContent(
            innerPadding = PaddingValues(),
            product = HomeSampleData.sampleSections.first().products.first()
        )
    }
}

@Composable
private fun DetailLoadingState(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading…")
    }
}

@Composable
private fun DetailErrorState(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Something went wrong")
    }
}

