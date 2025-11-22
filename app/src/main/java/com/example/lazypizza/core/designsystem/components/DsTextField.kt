package com.example.lazypizza.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.lazypizza.R

object DsTextField {

    @Composable
    fun Search(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "Search for delicious food…",
    ) {
        OutlinedTextField(
            modifier = modifier
                .background(AppColors.SurfaceHigher, RoundedCornerShape(28.dp)),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTypography.Body1Regular,
                    color = AppColors.TextSecondary
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = AppColors.Primary
                )
            },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                focusedContainerColor = AppColors.SurfaceHigher,
                unfocusedContainerColor = AppColors.SurfaceHigher,
                disabledContainerColor = AppColors.SurfaceHigher,
            ),
            singleLine = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPreview() {
    var searchQuery by remember { mutableStateOf("") }
    LazyPizzaThemePreview {
        DsTextField.Search(value = searchQuery, onValueChange = { searchQuery = it })
    }
}
