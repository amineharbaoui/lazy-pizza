package com.example.ui.pizzadetail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsDialog
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.components.textfield.DsTextField
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout
import com.example.menu.ui.home.R
import com.example.ui.pizzadetail.components.ProductCard
import kotlinx.coroutines.launch
import com.example.designsystem.R as DsR

@Composable
fun MenuScreen(
    innerPadding: PaddingValues,
    onProductClick: (productId: String) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
    isLoggedIn: Boolean = false,
    onNavigateToAuth: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val content = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showLogoutConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsTopBar.Primary(
            phoneNumber = stringResource(R.string.phone_number),
            onPhoneClick = { phoneNumber ->
                onPhoneClick(context = content, phoneNumber = phoneNumber)
            },
            isLoggedIn = isLoggedIn,
            onAccountClick = {
                if (isLoggedIn) showLogoutConfirm = true else onNavigateToAuth()
            },
        )
        when (val state = uiState) {
            MenuUiState.Loading -> MenuScreenLoadingState()
            is MenuUiState.Success -> MenuScreenContent(
                sections = state.filteredMenu,
                menuTags = state.menuTags,
                searchQuery = state.searchQuery,
                onPizzaClick = { onProductClick(it.id) },
                onOtherItemAddClick = viewModel::onOtherItemAddClick,
                onOtherItemQuantityChange = viewModel::onOtherItemQuantityChange,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                listState = listState,
                gridState = gridState,
            )

            MenuUiState.Error -> MenuScreenErrorState()
        }

        if (showLogoutConfirm) {
            DsDialog.Confirm(
                title = stringResource(id = DsR.string.logout_confirm_title),
                primaryButtonText = stringResource(id = DsR.string.logout),
                secondaryButtonText = stringResource(id = DsR.string.cancel),
                onPrimaryClick = {
                    showLogoutConfirm = false
                    onLogout()
                },
                onDismissRequest = { showLogoutConfirm = false },
            )
        }
    }
}

@Composable
fun MenuScreenContent(
    sections: List<MenuSectionDisplayModel>,
    menuTags: List<MenuTag>,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
) {
    val isWide = isWideLayout()
    val scope = rememberCoroutineScope()

    val sectionStartIndices: List<Int> = remember(sections) { buildSectionStartIndices(sections) }
    val isEmpty = sections.isEmpty() || sections.all { it.items.isEmpty() }

    Column(
        verticalArrangement = Arrangement.Top,
    ) {
        if (isWide) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Header(
                        menuTags = menuTags,
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
                if (isEmpty) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        NoProductsMessage()
                    }
                } else {
                    sections.forEach { section ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = section.category.name.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp),
                            )
                        }
                        items(section.items, key = { it.id }) { product ->
                            ProductCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                product = product,
                                onPizzaClick = onPizzaClick,
                                onOtherItemAddClick = onOtherItemAddClick,
                                onOtherItemQuantityChange = onOtherItemQuantityChange,
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Header(
                        menuTags = menuTags,
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
                if (isEmpty) {
                    item {
                        NoProductsMessage()
                    }
                } else {
                    sections.forEach { section ->
                        item {
                            Text(
                                text = section.category.name.uppercase(),
                                style = AppTypography.Label2SemiBold,
                                color = AppColors.TextSecondary,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp),
                            )
                        }
                        items(section.items, key = { it.id }) { product ->
                            ProductCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                product = product,
                                onPizzaClick = onPizzaClick,
                                onOtherItemAddClick = onOtherItemAddClick,
                                onOtherItemQuantityChange = onOtherItemQuantityChange,
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
    menuTags: List<MenuTag>,
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
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(16.dp))
        DsTextField.Search(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            menuTags.forEachIndexed { index, tag ->
                DsButton.Rounded(
                    text = tag.displayName,
                    onClick = { onScroll(index) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MenuScreenLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Loading...")
    }
}

@Composable
private fun MenuScreenErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Something went wrong")
    }
}

@Composable
fun NoProductsMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.no_products_found),
                style = AppTypography.Body1Medium,
                color = AppColors.TextSecondary,
            )
        }
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
private fun MenuScreenPreview() {
    LazyPizzaThemePreview {
        MenuScreenContent(
            sections = emptyList(), // HomeSampleData.sampleSections.toDisplayModels(),
            searchQuery = "",
            onPizzaClick = {},
            onSearchQueryChange = {},
            menuTags = emptyList(),
            onOtherItemAddClick = {},
            onOtherItemQuantityChange = { _, _ -> },
            listState = rememberLazyListState(),
            gridState = rememberLazyGridState(),
        )
    }
}
