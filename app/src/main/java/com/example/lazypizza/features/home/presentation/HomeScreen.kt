package com.example.lazypizza.features.home.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.core.designsystem.components.DsAppBar
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsTextField
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.ProductCategory
import com.example.lazypizza.ui.theme.AppColors
import com.example.lazypizza.ui.theme.LazyPizzaThemePreview

@Composable
fun HomeScreen(innerPadding: PaddingValues) {
    val content = LocalContext.current
    Column(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsAppBar.Primary(
            phoneNumber = stringResource(R.string.phone_number),
            onPhoneClick = { phoneNumber ->
                onPhoneClick(context = content, phoneNumber = phoneNumber)
            }
        )
        HomeScreenContent(sections = emptyList())
    }
}

@Composable
fun HomeScreenContent(
    sections: List<CategorySection>,
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(R.drawable.img_home_screen),
            contentDescription = "Image Home Screen",
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        DsTextField.Search(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            }
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductCategory.entries.forEach {
                DsButton.Rounded(
                    text = it.value,
                    onClick = {}
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        ProductList(
            sections = sections,
            onProductClick = {}
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun HomeScreenPreview() {
    LazyPizzaThemePreview {
        HomeScreen(PaddingValues())
    }
}

private fun onPhoneClick(
    context: Context,
    phoneNumber: String
) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        setData("tel:$phoneNumber".toUri())
    }
    context.startActivity(intent)
}
