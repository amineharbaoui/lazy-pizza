package com.example.designsystem.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview

object DsCardRow {

    private val radius = 12.dp
    private val imageSize = 120.dp

    @Composable
    fun MenuItem(
        title: String,
        description: String,
        price: String,
        image: Painter,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
    ) {
        BaseCardRow(
            title = title,
            image = image,
            modifier = modifier,
            onClick = onClick,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                )
                Text(
                    text = description,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = price,
                    style = AppTypography.Title1SemiBold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }

    @Composable
    fun CartItem(
        modifier: Modifier = Modifier,
        title: String,
        subtitleLines: List<String> = emptyList(),
        unitPriceText: String,
        totalPriceText: String,
        image: Painter,
        quantity: Int,
        onQuantityChange: (Int) -> Unit,
        onRemove: () -> Unit,
    ) {
        BaseCardRow(
            title = title,
            image = image,
            modifier = modifier,
            onClick = null,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                DsButton.IconSmallRounded(
                    icon = painterResource(R.drawable.remove),
                    iconTint = AppColors.Primary,
                    onClick = onRemove,
                )
            }
            subtitleLines.forEach { subtitleLine ->
                Text(
                    text = subtitleLine,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    DsButton.IconSmallRounded(
                        icon = painterResource(R.drawable.minus),
                        iconTint = AppColors.TextSecondary,
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

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = totalPriceText,
                        style = AppTypography.Title1SemiBold,
                        color = AppColors.TextPrimary,
                    )
                    Text(
                        text = "$quantity × $unitPriceText",
                        style = AppTypography.Body3Regular,
                        color = AppColors.TextSecondary,
                    )
                }
            }
        }
    }

    @Composable
    fun AddToCartItem(
        title: String,
        price: String,
        image: Painter,
        modifier: Modifier = Modifier,
        onAddToCart: () -> Unit,
        buttonText: String = "Add to Cart",
    ) {
        BaseCardRow(
            title = title,
            image = image,
            modifier = modifier,
            onClick = null,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = AppTypography.Body1Medium,
                    color = AppColors.TextPrimary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = price,
                        style = AppTypography.Title1SemiBold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    DsButton.Outlined(
                        text = buttonText,
                        onClick = onAddToCart,
                    )
                }
            }
        }
    }

    @Composable
    private fun BaseCardRow(
        title: String,
        image: Painter,
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        val cardModifier = remember(modifier, onClick) {
            if (onClick != null) {
                modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
            } else {
                modifier.fillMaxWidth()
            }
        }

        Card(
            modifier = cardModifier,
            shape = RoundedCornerShape(radius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .width(imageSize)
                        .fillMaxHeight()
                        .padding(1.dp)
                        .background(
                            color = AppColors.SurfaceHighest,
                            shape = RoundedCornerShape(
                                topStart = radius,
                                bottomStart = radius,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = image,
                        contentDescription = title,
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Fit,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = imageSize)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PizzaCardPreview() {
    LazyPizzaThemePreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            DsCardRow.MenuItem(
                title = "Margherita",
                description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                price = "$8.99",
                image = painterResource(R.drawable.img_pizza),
            )
            DsCardRow.CartItem(
                title = "Margherita",
                subtitleLines = listOf(
                    "1 x Extra Cheese",
                    "1 x Extra Cheese",
                    "1 x Extra Cheese",
                    "1 x Extra Cheese",
                ),
                unitPriceText = "$8.00",
                totalPriceText = "$16.00",
                image = painterResource(R.drawable.img_pizza),
                quantity = 0,
                onQuantityChange = {},
                onRemove = {},
            )
            DsCardRow.CartItem(
                title = "Margherita",
                subtitleLines = listOf(
                    "1 x Extra Cheese",
                ),
                unitPriceText = "$8.00",
                totalPriceText = "$16.00",
                image = painterResource(R.drawable.img_pizza),
                quantity = 0,
                onQuantityChange = {},
                onRemove = {},
            )
            DsCardRow.CartItem(
                title = "Margherita",
                subtitleLines = listOf(),
                unitPriceText = "$8.00",
                totalPriceText = "$16.00",
                image = painterResource(R.drawable.img_pizza),
                quantity = 0,
                onQuantityChange = {},
                onRemove = {},
            )
            DsCardRow.AddToCartItem(
                title = "Margherita",
                price = "$8.99",
                onAddToCart = {},
                image = painterResource(R.drawable.img_pizza),
                buttonText = "Add to Cart",
            )
        }
    }
}
