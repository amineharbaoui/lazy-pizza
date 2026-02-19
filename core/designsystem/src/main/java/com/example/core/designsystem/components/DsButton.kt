package com.example.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.R
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaTheme

object DsButton {
    private val Shape = RoundedCornerShape(100.dp)
    private val Height = 40.dp

    @Composable
    fun Filled(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Surface(
            modifier = modifier
                .height(Height)
                .clip(Shape)
                .then(
                    if (enabled) {
                        Modifier.background(brush = AppColors.PrimaryGradientBrush, shape = Shape)
                    } else {
                        Modifier.background(color = AppColors.TextSecondary_8, shape = Shape)
                    },
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = AppColors.TextPrimary),
                    enabled = enabled,
                    onClick = onClick,
                ),
            color = Color.Transparent,
            shape = Shape,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = text,
                    style = AppTypography.Title3SemiBold,
                    color = if (enabled) AppColors.OnPrimary else AppColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    @Composable
    fun Outlined(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Surface(
            modifier = modifier
                .height(Height)
                .clip(Shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = AppColors.Primary),
                    enabled = enabled,
                    onClick = onClick,
                ),
            color = Color.Transparent,
            shape = Shape,
            border = BorderStroke(
                1.dp,
                if (enabled) AppColors.Primary_8 else AppColors.Outline,
            ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = text,
                    style = AppTypography.Title3SemiBold,
                    color = if (enabled) AppColors.Primary else AppColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    @Composable
    fun Text(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Surface(
            modifier = modifier
                .height(Height)
                .clip(Shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = AppColors.Primary),
                    enabled = enabled,
                    onClick = onClick,
                ),
            color = Color.Transparent,
            shape = Shape,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = text,
                    style = AppTypography.Title3SemiBold,
                    color = if (enabled) AppColors.Primary else AppColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    @Composable
    fun IconSmallRounded(
        icon: Painter,
        iconTint: Color,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ) {
        val bgColor = if (enabled) AppColors.SurfaceHigher else AppColors.Outline_50
        Surface(
            modifier = modifier.size(22.dp),
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            color = bgColor,
            border = BorderStroke(
                1.dp,
                AppColors.Outline,
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(10.dp),
                    painter = icon,
                    tint = iconTint,
                    contentDescription = "",
                )
            }
        }
    }

    @Composable
    fun Rounded(
        text: String,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ) {
        val shape = RoundedCornerShape(8.dp)
        val interactionSource = remember { MutableInteractionSource() }
        Surface(
            modifier = modifier
                .height(Height)
                .clip(shape)
                .background(color = Color.Transparent, shape = shape)
                .border(1.dp, AppColors.Outline, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = AppColors.Primary),
                    enabled = enabled,
                    onClick = onClick,
                ),
            color = Color.Transparent,
            shape = Shape,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(
                    text = text,
                    style = AppTypography.Title3SemiBold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                )
            }
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
private fun FilledPreview() {
    LazyPizzaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsButton.Filled(
                    text = "Filled Enabled",
                    onClick = {},
                )
                Spacer(Modifier.width(8.dp))
                DsButton.Filled(
                    text = "Filled Disabled",
                    enabled = false,
                    onClick = {},
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsButton.Outlined(
                    text = "Outlined Enabled",
                    onClick = {},
                )
                Spacer(Modifier.width(8.dp))
                DsButton.Outlined(
                    text = "Outlined Disabled",
                    enabled = false,
                    onClick = {},
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsButton.Text(
                    text = "Text Enabled",
                    onClick = {},
                )
                Spacer(Modifier.width(8.dp))
                DsButton.Text(
                    text = "Text Disabled",
                    enabled = false,
                    onClick = {},
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsButton.IconSmallRounded(
                    icon = painterResource(R.drawable.remove),
                    iconTint = AppColors.Primary,
                    onClick = {},
                )
                Spacer(Modifier.width(8.dp))
                DsButton.IconSmallRounded(
                    icon = painterResource(R.drawable.plus),
                    iconTint = AppColors.TextSecondary,
                    onClick = {},
                )
                Spacer(Modifier.width(8.dp))
                DsButton.IconSmallRounded(
                    icon = painterResource(R.drawable.plus),
                    enabled = false,
                    iconTint = AppColors.TextSecondary,
                    onClick = {},
                )
            }
        }
    }
}
