package com.example.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DsStatusPill.Pill
import com.example.designsystem.components.DsStatusPill.Style
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaTheme

object DsStatusPill {

    enum class Style {
        COMPLETED,
        IN_PROGRESS,
        CANCELLED,
    }

    @Composable
    fun Pill(
        text: String,
        style: Style,
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(
            horizontal = 16.dp,
            vertical = 6.dp,
        ),
    ) {
        val (bg, textColor) = when (style) {
            Style.COMPLETED -> AppColors.Success to AppColors.TextOnPrimary
            Style.IN_PROGRESS -> AppColors.Warning to AppColors.TextOnPrimary
            Style.CANCELLED -> AppColors.Overlay to AppColors.TextOnPrimary
        }

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(100.dp),
            color = bg,
            contentColor = textColor,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(contentPadding),
                style = AppTypography.Label1Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusPillPreview() {
    LazyPizzaTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Pill(
                text = "Completed",
                style = Style.COMPLETED,
            )
            Pill(
                text = "In Progress",
                style = Style.IN_PROGRESS,
            )
            Pill(
                text = "In Progress",
                style = Style.CANCELLED,
            )
        }
    }
}
