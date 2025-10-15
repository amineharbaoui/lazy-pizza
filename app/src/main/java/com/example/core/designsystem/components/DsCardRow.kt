package com.example.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.lazypizza.R
import java.text.NumberFormat
import java.util.Locale

object DsCardRow {
    val radius = 12.dp
    val imageSize = 120.dp

    @Composable
    fun MenuItem(
        title: String,
        description: String,
        price: String,
        image: Painter,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(radius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick,
        ) {
            Row {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier
                        .size(imageSize)
                        .padding(1.dp)
                        .background(
                            color = AppColors.SurfaceHighest,
                            shape = RoundedCornerShape(
                                topStart = radius,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = radius,
                            ),
                        ),
                    contentScale = ContentScale.Fit,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .requiredHeight(imageSize)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun CartItem(
        title: String,
        unitPrice: Double,
        quantity: Int,
        image: Painter,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit,
        onRemove: () -> Unit,
        modifier: Modifier = Modifier,
        currencyLocale: Locale = Locale.US,
        onClick: () -> Unit = {},
    ) {
        val currencyFmt = NumberFormat.getCurrencyInstance(currencyLocale)
        val total = unitPrice * quantity
        val totalText = currencyFmt.format(total)
        val unitText = currencyFmt.format(unitPrice)

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(radius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick,
        ) {
            Row {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier
                        .size(imageSize)
                        .padding(1.dp)
                        .background(
                            color = AppColors.SurfaceHighest,
                            shape = RoundedCornerShape(
                                topStart = radius,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = radius,
                            ),
                        ),
                    contentScale = ContentScale.Fit,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .requiredHeight(imageSize)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = AppTypography.Body1Medium,
                            color = AppColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        DsButton.IconSmallRounded(
                            icon = painterResource(R.drawable.ic_remove),
                            iconTint = AppColors.Primary,
                            onClick = onRemove,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            DsButton.IconSmallRounded(
                                icon = painterResource(R.drawable.ic_minus),
                                iconTint = AppColors.TextSecondary,
                                onClick = onDecrease,
                            )
                            Text(
                                text = quantity.toString(),
                                style = AppTypography.Title2SemiBold,
                                color = AppColors.TextPrimary,
                            )
                            DsButton.IconSmallRounded(
                                icon = painterResource(R.drawable.ic_plus),
                                iconTint = AppColors.TextSecondary,
                                onClick = onIncrease,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = totalText,
                                style = AppTypography.Title1SemiBold,
                                color = AppColors.TextPrimary,
                            )
                            Text(
                                text = "$quantity × $unitText",
                                style = AppTypography.Body3Regular,
                                color = AppColors.TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AddToCartItem(
        title: String,
        price: String,
        image: Painter,
        buttonText: String = "Add to Cart",
        onAddToCart: () -> Unit,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(radius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick,
        ) {
            Row {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier
                        .size(imageSize)
                        .padding(1.dp)
                        .background(
                            color = AppColors.SurfaceHighest,
                            shape = RoundedCornerShape(
                                topStart = radius,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = radius,
                            ),
                        ),
                    contentScale = ContentScale.Fit,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .requiredHeight(imageSize)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = title,
                        style = AppTypography.Body1Medium,
                        color = AppColors.TextPrimary,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
    }
}

@Preview(showBackground = true)
@Composable
private fun PizzaCardPreview() {
    LazyPizzaThemePreview {
        DsCardRow.MenuItem(
            title = "Margherita",
            description = "Tomato sauce, mozzarella, fresh basil, olive oil",
            price = "$8.99",
            image = painterResource(R.drawable.img_pizza),
        )
        Spacer(Modifier.height(8.dp))
        DsCardRow.CartItem(
            title = "Margherita",
            unitPrice = 8.99,
            quantity = 2,
            image = painterResource(R.drawable.img_pizza),
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
        )
        Spacer(Modifier.height(8.dp))
        DsCardRow.AddToCartItem(
            title = "Margherita",
            price = "$8.99",
            onAddToCart = {},
            image = painterResource(R.drawable.img_pizza),
            buttonText = "Add to Cart",
        )
    }
}
