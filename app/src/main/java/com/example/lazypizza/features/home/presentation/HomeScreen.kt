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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.core.designsystem.components.DsAppBar
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsTextField
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.features.home.data.utils.HomeSampleData
import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.ProductCategory
import com.example.lazypizza.features.home.presentation.components.ProductCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val content = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
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
        when (val state = uiState) {
            HomeScreenUiState.Loading -> HomeScreenLoadingState()
            is HomeScreenUiState.Success -> HomeScreenContent(
                sections = state.displaySections,
                searchQuery = state.searchQuery,
                onProductClick = onProductClick,
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
    onProductClick: (productId: String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val isWide = windowSizeClass != WindowWidthSizeClass.COMPACT
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val sectionIndexMap = remember(sections) { buildSectionHeaderIndexMap(sections) }
    val isEmpty = sections.isEmpty() || sections.all { it.products.isEmpty() }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        if (isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_products_found),
                        style = AppTypography.Body1Medium,
                        color = AppColors.TextSecondary
                    )
                }
            }
        } else {
            if (isWide) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Header(
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            onScroll = { target ->
                                scope.launch {
                                    gridState.animateScrollToItem(
                                        target
                                    )
                                }
                            },
                            sectionIndexMap = sectionIndexMap
                        )
                    }
                    sections.forEach { section ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = section.category.value.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(section.products, key = { it.id }) { product ->
                            ProductCard(product, onProductClick)
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(16.dp)) }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Header(
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            onScroll = { target ->
                                scope.launch {
                                    listState.animateScrollToItem(
                                        target
                                    )
                                }
                            },
                            sectionIndexMap = sectionIndexMap
                        )
                    }
                    sections.forEach { section ->
                        item {
                            Text(
                                text = section.category.value.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(section.products, key = { it.id }) { product ->
                            ProductCard(product, onProductClick)
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Header(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScroll: (targetPosition: Int) -> Unit,
    sectionIndexMap: Map<ProductCategory, Int>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductCategory.entries.forEach { category ->
                DsButton.Rounded(
                    text = category.value,
                    onClick = {
                        val target = sectionIndexMap[category] ?: 0
                        onScroll(target)
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeScreenLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading...")
    }
}

@Composable
private fun HomeScreenErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Something went wrong")
    }
}

@PreviewPhoneTablet
@Composable
private fun HomeScreenPreview() {
    LazyPizzaThemePreview {
        HomeScreenContent(
            sections = HomeSampleData.sampleSections,
            searchQuery = "",
            onProductClick = {},
            onSearchQueryChange = {}
        )
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
