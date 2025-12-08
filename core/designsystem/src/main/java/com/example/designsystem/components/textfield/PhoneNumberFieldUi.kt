package com.example.designsystem.components.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.example.designsystem.components.DsTextField
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography

@Composable
internal fun CountryLeadingIcon(
    country: PhoneCountryUi,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(country.flagResId),
            contentDescription = country.name,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            painter = painterResource(R.drawable.chevron_down),
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = country.dialCode,
            style = AppTypography.Body1Regular,
            color = AppColors.TextPrimary,
        )
    }
}

@Composable
internal fun CountryDropdown(
    expanded: Boolean,
    searchQuery: String,
    countries: List<PhoneCountryUi>,
    onDismissRequest: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCountrySelected: (PhoneCountryUi) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.background(AppColors.Bg),
        shape = RoundedCornerShape(12.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(horizontal = 8.dp),
        ) {
            DsTextField.Search(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Search for countries",
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    items(
                        count = countries.size,
                        key = { index -> countries[index].isoCode },
                    ) { index ->
                        val country = countries[index]
                        CountryRow(
                            country = country,
                            onClick = { onCountrySelected(country) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CountryRow(
    country: PhoneCountryUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(country.flagResId),
            contentDescription = country.name,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = country.name,
            style = AppTypography.Body3Regular,
            color = AppColors.TextPrimary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = country.dialCode,
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
    }
}
