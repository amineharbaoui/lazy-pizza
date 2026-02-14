package com.example.menu.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.card.DsCardRow
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.model.ProductCategory
import com.example.menu.ui.home.MenuItemDisplayModel
import com.example.core.designsystem.R as DsR

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: MenuItemDisplayModel,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val image = if (LocalInspectionMode.current) {
        painterResource(product.category.toPreviewDrawableRes())
    } else {
        rememberAsyncImagePainter(product.imageUrl)
    }
    when (product) {
        is MenuItemDisplayModel.Pizza -> {
            val sharedKey = "pizza-image-${product.id}"
            DsCardRow.MenuItem(
                modifier = modifier,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(sharedKey),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
                title = product.name,
                description = product.description,
                price = product.unitPriceFormatted,
                image = image,
                onClick = { onPizzaClick(product) },

                )
        }

        is MenuItemDisplayModel.Other -> {
            AnimatedContent(
                targetState = product.inCart,
                label = "cardSwitch",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                },
            ) { targetState ->
                if (!targetState) {
                    DsCardRow.AddToCartItem(
                        modifier = modifier,
                        title = product.name,
                        price = product.unitPriceFormatted,
                        image = image,
                        onAddToCart = { onOtherItemAddClick(product) },
                    )
                } else {
                    DsCardRow.CartItem(
                        modifier = modifier,
                        title = product.name,
                        quantity = product.quantity,
                        unitPriceText = product.unitPriceFormatted,
                        totalPriceText = product.totalPriceFormatted,
                        image = image,
                        onQuantityChange = { newQty ->
                            onOtherItemQuantityChange(product, newQty)
                        },
                        onRemove = {
                            onOtherItemQuantityChange(product, 0)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@PreviewPhoneTablet
@Composable
private fun ProductCardPreview(
    @PreviewParameter(ProductCardPreviewProvider::class) product: MenuItemDisplayModel,
) {
    LazyPizzaThemePreview {
        SharedTransitionLayout {
            AnimatedContent(targetState = product, label = "productCardPreview") { targetProduct ->
                ProductCard(
                    modifier = Modifier.padding(8.dp),
                    product = targetProduct,
                    onPizzaClick = {},
                    onOtherItemAddClick = {},
                    onOtherItemQuantityChange = { _, _ -> },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            }
        }
    }

}

private class ProductCardPreviewProvider : PreviewParameterProvider<MenuItemDisplayModel> {

    override val values: Sequence<MenuItemDisplayModel> = sequenceOf(
        MenuItemDisplayModel.Pizza(
            id = "pizza_margherita",
            name = "Margherita",
            imageUrl = "",
            unitPrice = 8.5,
            unitPriceFormatted = "€8.50",
            category = ProductCategory.PIZZA,
            description = "Tomato sauce, mozzarella, fresh basil",
        ),
        MenuItemDisplayModel.Other(
            id = "drink_cola",
            name = "Coca-Cola",
            imageUrl = "",
            unitPrice = 2.5,
            unitPriceFormatted = "€2.50",
            category = ProductCategory.DRINK,
            quantity = 0,
        ),
        MenuItemDisplayModel.Other(
            id = "sauce_bbq",
            name = "BBQ Sauce",
            imageUrl = "",
            unitPrice = 0.8,
            unitPriceFormatted = "€0.80",
            category = ProductCategory.SAUCE,
            quantity = 2,
        ),
    )
    override fun getDisplayName(index: Int): String = values.elementAt(index).category.name
}

private fun ProductCategory.toPreviewDrawableRes(): Int = when (this) {
    ProductCategory.PIZZA -> DsR.drawable.pizza
    ProductCategory.DRINK -> DsR.drawable.drink
    ProductCategory.SAUCE -> DsR.drawable.sauce
    ProductCategory.ICE_CREAM -> DsR.drawable.ice_cream
    ProductCategory.TOPPING -> DsR.drawable.topping
}
