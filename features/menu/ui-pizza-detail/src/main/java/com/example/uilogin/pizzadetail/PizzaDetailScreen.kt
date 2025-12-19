package com.example.uilogin.pizzadetail

import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.components.card.DsCardItem
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout

@Composable
fun PizzaDetailScreen(
    productId: String,
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    onNavigateToMenu: () -> Unit = {},
    viewModel: PizzaDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) { viewModel.observePizza(productId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsTopBar.Secondary(onBackClick = onBackClick)
        when (val state = uiState) {
            is PizzaDetailUiState.Loading -> DetailLoadingState()
            is PizzaDetailUiState.Error -> DetailErrorState()
            is PizzaDetailUiState.Success -> DetailContent(
                uiState = state,
                onToppingQuantityChange = viewModel::updateToppingQuantity,
                onAddToCartClick = {
                    viewModel.addItemToCart(state)
                    onNavigateToMenu()
                    Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                },
                onQuantityChange = viewModel::updateQuantity,
            )
        }
    }
}

@Composable
private fun DetailContent(
    uiState: PizzaDetailUiState.Success,
    onToppingQuantityChange: (toppingId: String, newQuantity: Int) -> Unit,
    onAddToCartClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
) {
    if (isWideLayout()) {
        WideDetailLayout(
            uiState = uiState,
            onToppingQuantityChange = onToppingQuantityChange,
            onAddToCartClick = onAddToCartClick,
            onQuantityChange = onQuantityChange,
        )
    } else {
        PhoneDetailLayout(
            uiState = uiState,
            onToppingQuantityChange = onToppingQuantityChange,
            onAddToCartClick = onAddToCartClick,
            onQuantityChange = onQuantityChange,
        )
    }
}

@Composable
private fun PhoneDetailLayout(
    uiState: PizzaDetailUiState.Success,
    onToppingQuantityChange: (toppingId: String, newQuantity: Int) -> Unit,
    onAddToCartClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
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
                painter = rememberAsyncImagePainter(uiState.pizza.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppColors.SurfaceHigher,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 16.dp),
            ) {
                ProductHeaderSection(
                    name = uiState.pizza.name,
                    description = uiState.pizza.description,
                )
                Spacer(Modifier.height(8.dp))
                ExtraToppingsContent(
                    toppings = uiState.toppings,
                    toppingQuantities = uiState.toppingQuantities,
                    onToppingQuantityChange = onToppingQuantityChange,
                    quantity = uiState.quantity,
                    onQuantityChange = onQuantityChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    contentPadding = PaddingValues(bottom = ctaHeight + ctaVerticalPadding * 2),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = ctaVerticalPadding),
        ) {
            AddToCartButton(
                totalPriceText = uiState.totalPriceFormatted,
                onClick = onAddToCartClick,
            )
        }
    }
}

@Composable
private fun DetailLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Loading…")
    }
}

@Composable
private fun DetailErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Something went wrong")
    }
}

@Composable
private fun WideDetailLayout(
    uiState: PizzaDetailUiState.Success,
    onToppingQuantityChange: (toppingId: String, newQuantity: Int) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onAddToCartClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(R.drawable.img_pizza),
                contentDescription = null,
                contentScale = ContentScale.Inside,
            )
            Spacer(Modifier.height(24.dp))
            ProductHeaderSection(
                name = uiState.pizza.name,
                description = uiState.pizza.description,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = AppColors.SurfaceHigher,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
        ) {
            ExtraToppingsContent(
                toppings = uiState.toppings,
                toppingQuantities = uiState.toppingQuantities,
                onToppingQuantityChange = onToppingQuantityChange,
                quantity = uiState.quantity,
                onQuantityChange = onQuantityChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
            )
            Spacer(Modifier.height(12.dp))
            AddToCartButton(
                totalPriceText = uiState.totalPriceFormatted,
                onClick = onAddToCartClick,
            )
        }
    }
}

@Composable
private fun ProductHeaderSection(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = name,
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = description,
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AddToCartButton(
    totalPriceText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        DsButton.Filled(
            text = "Add to Cart for $totalPriceText",
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ExtraToppingsContent(
    toppings: List<ToppingDisplayModel>,
    toppingQuantities: Map<String, Int>,
    onToppingQuantityChange: (toppingId: String, newQuantity: Int) -> Unit,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = modifier.background(
            color = AppColors.SurfaceHigher,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
            ),
        ),
    ) {
        Text(
            text = "Add extra toppings",
            style = AppTypography.Label2SemiBold,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
        ) {
            items(toppings, key = { it.id }) { item ->
                val qty = toppingQuantities[item.id] ?: 0
                DsCardItem.AddonCard(
                    title = item.name,
                    priceText = item.unitPriceFormatted,
                    image = rememberAsyncImagePainter(item.imageUrl),
                    quantity = qty,
                    onQuantityChange = { newQty ->
                        onToppingQuantityChange(item.id, newQty)
                    },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                QuantitySelector(
                    quantity = quantity,
                    onQuantityChange = onQuantityChange,
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Quantity",
            style = AppTypography.Title3SemiBold,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DsButton.IconSmallRounded(
                icon = painterResource(R.drawable.minus),
                iconTint = AppColors.TextSecondary,
                enabled = quantity != 1,
                onClick = {
                    val newQty = (quantity - 1).coerceAtLeast(0)
                    onQuantityChange(newQty)
                },
            )
            Text(
                text = quantity.toString(),
                style = AppTypography.Title2SemiBold,
                color = AppColors.TextPrimary,
            )
            DsButton.IconSmallRounded(
                icon = painterResource(R.drawable.plus),
                iconTint = AppColors.TextSecondary,
                onClick = {
                    val newQty = quantity + 1
                    onQuantityChange(newQty)
                },
            )
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun DetailContentPreview() {
    val pizza = PizzaDetailDisplayModel(
        id = "1",
        name = "Margherita",
        description = "Tomato sauce, mozzarella, fresh basil, olive oil",
        imageUrl = "",
        unitPrice = 8.99,
        unitPriceFormatted = "$8.99",
    )

    val toppings = listOf(
        ToppingDisplayModel("t1", "Extra Cheese", 1.0, "$1.00", ""),
        ToppingDisplayModel("t2", "Pepperoni", 1.5, "$1.50", ""),
        ToppingDisplayModel("t3", "Olives", 0.5, "$0.50", ""),
    )

    val state = PizzaDetailUiState.Success(
        pizza = pizza,
        toppings = toppings,
        toppingQuantities = mapOf("t1" to 1, "t2" to 1),
        totalPriceFormatted = "$12.99",
        quantity = 1,
    )

    LazyPizzaThemePreview {
        DetailContent(
            uiState = state,
            onToppingQuantityChange = { _, _ -> },
            onAddToCartClick = {},
            onQuantityChange = {},
        )
    }
}
