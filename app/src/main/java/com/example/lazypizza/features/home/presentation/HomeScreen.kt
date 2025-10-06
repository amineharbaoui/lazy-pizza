package com.example.lazypizza.features.home.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.DsAppBar
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsTextField
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.ProductCategory
import com.example.lazypizza.ui.theme.AppColors
import com.example.lazypizza.ui.theme.LazyPizzaThemePreview
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val content = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding()),
    ) {
        DsAppBar.Primary(
            phoneNumber = stringResource(R.string.phone_number),
            onPhoneClick = { phoneNumber ->
                onPhoneClick(context = content, phoneNumber = phoneNumber)
            }
        )
        when (val state = uiState) {
            HomeScreenUiState.Loading -> HomeScreenLoadingState()
            is HomeScreenUiState.Success -> HomeScreenContent(
                sections = state.displaySections,
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange
            )

            HomeScreenUiState.Error -> HomeScreenErrorState()
        }
    }
}

@Composable
fun HomeScreenContent(
    sections: List<CategorySection>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val isWide = LocalConfiguration.current.screenWidthDp >= 840
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val sectionIndexMap = remember(sections) { buildSectionHeaderIndexMap(sections) }

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
            onValueChange = onSearchQueryChange
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductCategory.entries.forEach { category ->
                DsButton.Rounded(
                    text = category.value,
                    onClick = {
                        val target = sectionIndexMap[category] ?: 0
                        scope.launch {
                            if (isWide) {
                                gridState.animateScrollToItem(target)
                            } else {
                                listState.animateScrollToItem(target)
                            }
                        }
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        ProductList(
            sections = sections,
            onProductClick = {},
            isWide = isWide,
            listState = listState,
            gridState = gridState
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun HomeScreenPreview() {
    LazyPizzaThemePreview {
        val sections = ProductCategory.entries.map { category ->
            CategorySection(
                category = category,
                products = sampleProducts().filter { it.category == category }
            )
        }
        HomeScreenContent(
            sections = sections,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

@Composable
private fun HomeScreenLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading...")
    }
}

@Composable
private fun HomeScreenErrorState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Something went wrong")
    }
}

private fun buildSectionHeaderIndexMap(
    sections: List<CategorySection>
): Map<ProductCategory, Int> {
    val map = mutableMapOf<ProductCategory, Int>()
    var currentIndex = 0
    sections.forEach { section ->
        map[section.category] = currentIndex
        currentIndex += 1 + section.products.size
    }
    return map
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
