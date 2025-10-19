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
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.DsAppBar
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsCardItem
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.lazypizza.R
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
            uiState = state
        )

        is DetailUiState.Error -> DetailErrorState(innerPadding)
    }
}

@Composable
private fun DetailContent(
    innerPadding: PaddingValues,
    uiState: DetailUiState.Success,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val isWide = windowSizeClass != WindowWidthSizeClass.COMPACT

    Column(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
    ) {
        DsAppBar.Secondary(onBackClick = { /* Nav handled outside */ })

        if (isWide) {
            //WideDetailLayout(product = product)
        } else {
            PhoneDetailLayout(uiState = uiState)
        }
    }
}

@Composable
private fun PhoneDetailLayout(
    uiState: DetailUiState.Success,
) {
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
            name = uiState.product.name,
            description = uiState.product.description,
        )
        AddonExtraSection(toppings = uiState.toppings)
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = "Add to Cart for ${uiState.product.price.asMoney()}",
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
private fun AddonExtraSection(
    toppings: List<Product>,
) {
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
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(toppings) { item ->
                DsCardItem.AddonCard(
                    title = item.name,
                    priceText = item.price.asMoney(),
                    image = rememberAsyncImagePainter(item.imageUrl),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun Double.asMoney(): String = NumberFormat.getCurrencyInstance().format(this)


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

