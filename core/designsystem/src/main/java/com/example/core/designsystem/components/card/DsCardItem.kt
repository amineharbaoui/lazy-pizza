package com.example.core.designsystem.components.card

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.R
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview

object DsCardItem {
    @Composable
    fun AddonCard(
        title: String,
        priceText: String,
        image: Painter,
        modifier: Modifier = Modifier,
        minQty: Int = 1,
        maxQty: Int = 3,
    ) {
        var quantity by rememberSaveable { mutableIntStateOf(0) }
        Base(
            title = title,
            priceText = priceText,
            image = image,
            quantity = quantity,
            onQuantityChange = { quantity = it },
            modifier = modifier,
            minQty = minQty,
            maxQty = maxQty,
        )
    }

    @Composable
    fun AddonCard(
        title: String,
        priceText: String,
        image: Painter,
        quantity: Int,
        onQuantityChange: (Int) -> Unit,
        modifier: Modifier = Modifier,
        minQty: Int = 1,
        maxQty: Int = 3,
    ) {
        Base(
            title = title,
            priceText = priceText,
            image = image,
            quantity = quantity,
            onQuantityChange = onQuantityChange,
            modifier = modifier,
            minQty = minQty,
            maxQty = maxQty,
        )
    }

    @Composable
    fun ExtraItem(
        title: String,
        price: String,
        image: Painter,
        modifier: Modifier = Modifier,
        onAdd: () -> Unit = {},
    ) {
        val radius = 12.dp
        Card(
            modifier = modifier
                .width(160.dp)
                .height(260.dp),
            shape = RoundedCornerShape(radius),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceHigher),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier
                        .padding(1.dp)
                        .fillMaxWidth()
                        .background(
                            color = AppColors.SurfaceHighest,
                            shape = RoundedCornerShape(
                                topStart = radius,
                                topEnd = radius,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp,
                            ),
                        ),
                    contentScale = ContentScale.Fit,
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Text(
                        text = title,
                        style = AppTypography.Body1Regular,
                        color = AppColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = price,
                            style = AppTypography.Title1SemiBold,
                            color = AppColors.TextPrimary,
                        )
                        DsButton.IconSmallRounded(
                            icon = painterResource(R.drawable.plus),
                            iconTint = AppColors.Primary,
                            onClick = onAdd,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Base(
        title: String,
        priceText: String,
        image: Painter,
        quantity: Int,
        onQuantityChange: (Int) -> Unit,
        modifier: Modifier = Modifier,
        minQty: Int = 1,
        maxQty: Int = 3,
    ) {
        val active = quantity > 0
        val shape = RoundedCornerShape(12.dp)
        val highlightColor = if (active) MaterialTheme.colorScheme.primary else AppColors.Outline_50
        val borderColor by animateColorAsState(highlightColor, label = "border")

        Surface(
            shape = shape,
            tonalElevation = if (active) 1.dp else 0.dp,
            shadowElevation = 6.dp,
            modifier = modifier
                .border(width = 1.dp, color = borderColor, shape = shape)
                .clip(shape)
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    if (!active) onQuantityChange(minQty)
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(122.dp)
                    .padding(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary_8),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = title,
                    style = AppTypography.Body3Regular,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                AnimatedContent(
                    targetState = active,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { isActive ->
                    if (!isActive) {
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    } else {
                        QuantityRow(
                            quantity = quantity,
                            onMinus = {
                                if (quantity <= minQty) {
                                    onQuantityChange(0)
                                } else {
                                    onQuantityChange((quantity - 1).coerceAtLeast(minQty))
                                }
                            },
                            onPlus = {
                                if (quantity < maxQty) {
                                    onQuantityChange((quantity + 1).coerceAtMost(maxQty))
                                }
                            },
                            plusEnabled = quantity < maxQty,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun QuantityRow(
        quantity: Int,
        onMinus: () -> Unit,
        onPlus: () -> Unit,
        plusEnabled: Boolean,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsButton.IconSmallRounded(
                icon = painterResource(R.drawable.minus),
                iconTint = AppColors.TextSecondary,
                onClick = onMinus,
            )
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            DsButton.IconSmallRounded(
                icon = painterResource(R.drawable.plus),
                iconTint = AppColors.TextSecondary,
                onClick = onPlus,
                enabled = plusEnabled,
            )
        }
    }
}

@Preview(
    name = "P1 - Phone",
    showBackground = true,
)
@Preview(
    name = "P1 - Phone (Dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun AddonCardPreview() {
    LazyPizzaThemePreview {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier,
        ) {
            val painter = painterResource(R.drawable.img_bacon)
            DsCardItem.AddonCard(title = "This is a long Bacon", priceText = "$1", image = painter)
            Spacer(Modifier.height(8.dp))
            DsCardItem.ExtraItem(
                title = "Bacon",
                price = "$0.59",
                image = painterResource(R.drawable.img_bacon),
            )
        }
    }
}
