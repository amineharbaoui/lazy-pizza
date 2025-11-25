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
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTextField
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout
import com.example.lazypizza.features.home.presentation.components.ProductCard
import com.example.menu.domain.model.ProductCategory
import com.example.menu.presentation.menu.MenuUiState
import com.example.menu.presentation.menu.MenuViewModel
import com.example.menu.presentation.model.MenuSectionDisplayModel
import com.example.menu.presentation.model.toDisplayName
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val content = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsTopBar.Primary(
            phoneNumber = stringResource(R.string.phone_number),
            onPhoneClick = { phoneNumber ->
                onPhoneClick(context = content, phoneNumber = phoneNumber)
            }
        )
        when (val state = uiState) {
            MenuUiState.Loading -> HomeScreenLoadingState()
            is MenuUiState.Success -> HomeScreenContent(
                sections = state.filteredMenu,
                filterTags = state.filterTags,
                searchQuery = state.searchQuery,
                onProductClick = onProductClick,
                onSearchQueryChange = viewModel::onSearchQueryChange
            )

            MenuUiState.Error -> HomeScreenErrorState()
        }
    }
}

@Composable
fun HomeScreenContent(
    sections: List<MenuSectionDisplayModel>,
    filterTags: List<ProductCategory>,
    searchQuery: String,
    onProductClick: (productId: String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    val isWide = isWideLayout()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val sectionStartIndices: List<Int> = remember(sections) { buildSectionStartIndices(sections) }
    val isEmpty = sections.isEmpty() || sections.all { it.items.isEmpty() }

    Column(
        verticalArrangement = Arrangement.Top,
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
                            filterTags = filterTags,
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            onScroll = { tagIndex ->
                                val target = sectionStartIndices.getOrElse(tagIndex) { 0 }
                                scope.launch {
                                    gridState.animateScrollToItem(target)
                                }
                            },
                        )
                    }
                    sections.forEach { section ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = section.category.name.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        items(section.items, key = { it.id }) { product ->
                            ProductCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                product = product,
                                onProductClick = onProductClick
                            )
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
                            filterTags = filterTags,
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            onScroll = { tagIndex ->
                                val target = sectionStartIndices.getOrElse(tagIndex) { 0 }
                                scope.launch {
                                    listState.animateScrollToItem(target)
                                }
                            },
                        )
                    }
                    sections.forEach { section ->
                        item {
                            Text(
                                text = section.category.name.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        items(section.items, key = { it.id }) { product ->
                            ProductCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                product = product,
                                onProductClick = onProductClick
                            )
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
    filterTags: List<ProductCategory>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(R.drawable.img_home_screen),
            contentDescription = "Image Home Screen",
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        DsTextField.Search(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterTags.forEachIndexed { index, tag ->
                DsButton.Rounded(
                    text = tag.toDisplayName(),
                    onClick = { onScroll(index) }
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

private fun onPhoneClick(
    context: Context,
    phoneNumber: String,
) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        setData("tel:$phoneNumber".toUri())
    }
    context.startActivity(intent)
}

private fun buildSectionStartIndices(sections: List<MenuSectionDisplayModel>): List<Int> {
    val indices = mutableListOf<Int>()
    var current = 1
    sections.forEach { section ->
        indices += current
        current += 1 + section.items.size + 1
    }
    return indices
}


@PreviewPhoneTablet
@Composable
private fun HomeScreenPreview() {
    LazyPizzaThemePreview {
        HomeScreenContent(
            sections = emptyList(),//HomeSampleData.sampleSections.toDisplayModels(),
            searchQuery = "",
            onProductClick = {},
            onSearchQueryChange = {},
            filterTags = emptyList(),
        )
    }
}
