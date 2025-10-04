package com.example.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.DsButton.Filled
import com.example.core.designsystem.DsButton.Outlined
import com.example.lazypizza.ui.theme.AppColors
import com.example.lazypizza.ui.theme.AppTypography
import com.example.lazypizza.ui.theme.LazyPizzaTheme

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
        val indication = LocalIndication.current
        Surface(
            modifier = modifier
                .height(Height)
                .clip(Shape)
                .then(
                    if (enabled) {
                        Modifier.background(brush = AppColors.PrimaryGradientBrush, shape = Shape)
                    } else {
                        Modifier.background(color = AppColors.TextSecondary_8, shape = Shape)
                    }
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.DarkGray),
                    enabled = enabled,
                    onClick = onClick
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
                    textAlign = TextAlign.Center
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
                    onClick = onClick
                ),
            color = Color.Transparent,
            shape = Shape,
            border = BorderStroke(
                1.dp,
                if (enabled) AppColors.Primary_8 else AppColors.Outline
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = text,
                    style = AppTypography.Title3SemiBold,
                    color = if (enabled) AppColors.Primary else AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilledPreview() {
    LazyPizzaTheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Filled(
                    text = "Filled Enabled",
                    onClick = {}
                )
                Spacer(Modifier.width(8.dp))
                Filled(
                    text = "Filled Disabled",
                    enabled = false,
                    onClick = {}
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Outlined(
                    text = "Outlined Enabled",
                    onClick = {}
                )
                Spacer(Modifier.width(8.dp))
                Outlined(
                    text = "Outlined Disabled",
                    enabled = false,
                    onClick = {}
                )
            }
        }
    }
}