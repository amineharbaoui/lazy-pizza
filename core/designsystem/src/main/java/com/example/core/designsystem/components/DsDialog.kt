package com.example.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview

object DsDialog {

    @Composable
    fun Confirm(
        title: String,
        modifier: Modifier = Modifier,
        message: String? = null,
        primaryButtonText: String,
        onPrimaryClick: () -> Unit,
        onDismissRequest: () -> Unit,
        secondaryButtonText: String = "Cancel",
        showSecondaryButton: Boolean = true,
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = AppColors.PrimaryShadow,
                        spotColor = AppColors.PrimaryShadow,
                    ),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.SurfaceHigher,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = title,
                        style = AppTypography.Title2SemiBold,
                        color = AppColors.TextPrimary,
                    )

                    if (message != null) {
                        Text(
                            text = message,
                            style = AppTypography.Body1Regular,
                            color = AppColors.TextSecondary,
                        )
                    }

                    Spacer(Modifier.weight(1f, fill = false))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showSecondaryButton) {
                            DsButton.Outlined(
                                text = secondaryButtonText,
                                onClick = onDismissRequest,
                            )
                        }

                        DsButton.Filled(
                            text = primaryButtonText,
                            onClick = onPrimaryClick,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmDialogPreview() {
    var open by remember { mutableStateOf(true) }

    if (!open) return

    LazyPizzaThemePreview {
        DsDialog.Confirm(
            title = "Are you sure you want to log out?",
            primaryButtonText = "Log Out",
            secondaryButtonText = "Cancel",
            onPrimaryClick = { open = false },
            onDismissRequest = { open = false },
        )
    }
}
