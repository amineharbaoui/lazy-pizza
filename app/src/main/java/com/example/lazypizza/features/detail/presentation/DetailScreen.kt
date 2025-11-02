package com.example.lazypizza.features.detail.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsCardItem
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.lazypizza.R
import com.example.lazypizza.features.home.domain.models.Product
import com.example.lazypizza.features.home.domain.models.ProductCategory
import java.text.NumberFormat

@Composable
fun DetailScreen(
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: DetailScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsTopBar.Secondary(onBackClick = onBackClick)
        when (val state = uiState) {
            is DetailUiState.Loading -> DetailLoadingState()
            is DetailUiState.Success -> DetailContent(uiState = state)
            is DetailUiState.Error -> DetailErrorState()
        }
    }
}

@Composable
private fun DetailContent(
    uiState: DetailUiState.Success,
) {
    val isWide = isWideLayout()

    // Keep selection state at screen level so both phone and wide layouts share it
    val selectedQuantities = remember { mutableStateMapOf<String, Int>() }
    val onQuantityChange: (String, Int) -> Unit = { id, qty ->
        if (qty <= 0) selectedQuantities.remove(id) else selectedQuantities[id] = qty
    }
    val totalPrice: Double = uiState.product.price + uiState.toppings.fold(0.0) { acc, topping ->
        val qty = selectedQuantities[topping.id] ?: 0
        acc + (qty.toDouble() * topping.price)
    }

    if (isWide) {
        WideDetailLayout(
            uiState = uiState,
            selectedQuantities = selectedQuantities,
            onQuantityChange = onQuantityChange,
            totalPrice = totalPrice,
        )
    } else {
        PhoneDetailLayout(
            uiState = uiState,
            selectedQuantities = selectedQuantities,
            onQuantityChange = onQuantityChange,
            totalPrice = totalPrice,
        )
    }
}

@Composable
private fun PhoneDetailLayout(
    uiState: DetailUiState.Success,
    selectedQuantities: Map<String, Int>,
    onQuantityChange: (String, Int) -> Unit,
    totalPrice: Double,
) {
    val ctaVerticalPadding = 16.dp
    val ctaHeight = 56.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppColors.SurfaceHigher,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp)
            ) {
                ProductHeaderSection(
                    modifier = Modifier
                        .background(
                            color = AppColors.SurfaceHigher,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    name = uiState.product.name,
                    description = uiState.product.description,
                )
                Spacer(Modifier.height(8.dp))
                ExtraToppingsContent(
                    toppings = uiState.toppings,
                    selectedQuantities = selectedQuantities,
                    onQuantityChange = onQuantityChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    contentPadding = PaddingValues(bottom = ctaHeight + ctaVerticalPadding * 2)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = ctaVerticalPadding)
        ) {
            AddToCartButton(totalPrice = totalPrice)
        }
    }
}

@Composable
fun ProductHeaderSection(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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

private fun Double.asMoney(): String = NumberFormat.getCurrencyInstance().format(this)

@Composable
private fun DetailLoadingState() {
    Box(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading…")
    }
}

@Composable
private fun DetailErrorState() {
    Box(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Something went wrong")
    }
}

@Composable
private fun WideDetailLayout(
    uiState: DetailUiState.Success,
    selectedQuantities: Map<String, Int>,
    onQuantityChange: (String, Int) -> Unit,
    totalPrice: Double,
) {
    Row(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(R.drawable.img_pizza),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
            Spacer(Modifier.height(24.dp))
            ProductHeaderSection(
                name = uiState.product.name,
                description = uiState.product.description,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = AppColors.SurfaceHigher,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            ExtraToppingsContent(
                toppings = uiState.toppings,
                selectedQuantities = selectedQuantities,
                onQuantityChange = onQuantityChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
            )
            Spacer(Modifier.height(12.dp))
            AddToCartButton(totalPrice = totalPrice)
        }
    }
}

@Composable
fun AddToCartButton(
    totalPrice: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        DsButton.Filled(
            text = "Add to Cart for ${totalPrice.asMoney()}",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ExtraToppingsContent(
    toppings: List<Product>,
    selectedQuantities: Map<String, Int>,
    onQuantityChange: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = modifier.background(
            color = AppColors.SurfaceHigher,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        )
    ) {
        Text(
            text = "Add extra toppings",
            style = AppTypography.Label2SemiBold,
            color = AppColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        ) {
            items(toppings) { item ->
                val qty = selectedQuantities[item.id] ?: 0
                DsCardItem.AddonCard(
                    title = item.name,
                    priceText = item.price.asMoney(),
                    image = rememberAsyncImagePainter(item.imageUrl),
                    quantity = qty,
                    onQuantityChange = { newQty -> onQuantityChange(item.id, newQty) },
                )
            }
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun DetailContentPreview() {
    LazyPizzaThemePreview {
        DetailContent(
            uiState = DetailUiState.Success(
                product = Product(
                    id = "1",
                    category = ProductCategory.PIZZA,
                    name = "Margherita",
                    description = "Classic pizza with tomato, mozzarella, and basil.",
                    price = 9.99,
                    imageUrl = ""
                ),
                toppings = listOf(
                    Product("t1", ProductCategory.TOPPINGS, "Olives", "", 1.0, ""),
                    Product("t2", ProductCategory.TOPPINGS, "Mushrooms", "", 1.0, ""),
                    Product("t3", ProductCategory.TOPPINGS, "Extra Cheese", "", 1.5, ""),
                    Product("t4", ProductCategory.TOPPINGS, "Pepperoni", "", 2.0, ""),
                    Product("t5", ProductCategory.TOPPINGS, "Onions", "", 0.5, ""),
                )
            )
        )
    }
}