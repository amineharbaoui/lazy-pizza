package com.example.menu.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.windowInsets
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
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsDialog
import com.example.core.designsystem.components.DsTextField
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.core.model.ProductCategory
import com.example.menu.ui.home.components.ProductCard
import kotlinx.coroutines.launch
import com.example.core.designsystem.R as DsR

@Composable
fun MenuScreen(
    innerPadding: PaddingValues,
    listState: LazyListState,
    gridState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onProductClick: (productId: String) -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Right))
            .fillMaxSize(),
    ) {
        DsTopBar.Primary(
            phoneNumber = stringResource(R.string.phone_number),
            onPhoneClick = { phoneNumber ->
                onPhoneClick(context = context, phoneNumber = phoneNumber)
            },
            isLoggedIn = uiState.isLoggedIn,
            onAccountClick = {
                if (uiState.isLoggedIn) showLogoutConfirm = true else onNavigateToAuth()
            },
        )
        when (val state = uiState.content) {
            MenuContentUiState.Loading -> MenuScreenLoadingState()
            is MenuContentUiState.Ready -> MenuScreenContent(
                innerPadding = innerPadding,
                sections = state.filteredMenu,
                menuTags = state.menuTags,
                searchQuery = state.searchQuery,
                onPizzaClick = { onProductClick(it.id) },
                onOtherItemAddClick = viewModel::addOtherItemToCart,
                onOtherItemQuantityChange = viewModel::updateOtherItemQuantity,
                onSearchQueryChange = viewModel::updateSearchQuery,
                listState = listState,
                gridState = gridState,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            MenuContentUiState.Error -> MenuScreenErrorState()
        }

        if (showLogoutConfirm) {
            DsDialog.Confirm(
                title = stringResource(id = DsR.string.logout_confirm_title),
                primaryButtonText = stringResource(id = DsR.string.logout),
                secondaryButtonText = stringResource(id = DsR.string.cancel),
                onPrimaryClick = {
                    showLogoutConfirm = false
                    viewModel.signOut()
                },
                onDismissRequest = { showLogoutConfirm = false },
            )
        }
    }
}

@Composable
fun MenuScreenContent(
    innerPadding: PaddingValues,
    sections: List<MenuSectionDisplayModel>,
    menuTags: List<MenuTag>,
    modifier: Modifier = Modifier,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val isWide = isWideLayout()
    val sectionStartIndices: List<Int> = remember(sections) { buildSectionStartIndices(sections) }
    val isEmpty = sections.isEmpty() || sections.all { it.items.isEmpty() }

    Column(modifier = modifier) {
        if (isWide) {
            WideMenuScreenContent(
                sections = sections,
                menuTags = menuTags,
                sectionStartIndices = sectionStartIndices,
                isEmpty = isEmpty,
                gridState = gridState,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onPizzaClick = onPizzaClick,
                onOtherItemAddClick = onOtherItemAddClick,
                onOtherItemQuantityChange = onOtherItemQuantityChange,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        } else {
            PhoneMenuScreenContent(
                sections = sections,
                menuTags = menuTags,
                sectionStartIndices = sectionStartIndices,
                isEmpty = isEmpty,
                listState = listState,
                innerPadding = innerPadding,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onPizzaClick = onPizzaClick,
                onOtherItemAddClick = onOtherItemAddClick,
                onOtherItemQuantityChange = onOtherItemQuantityChange,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun WideMenuScreenContent(
    sections: List<MenuSectionDisplayModel>,
    menuTags: List<MenuTag>,
    sectionStartIndices: List<Int>,
    modifier: Modifier = Modifier,
    isEmpty: Boolean,
    gridState: LazyGridState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val scope = rememberCoroutineScope()
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(contentType = "MenuImageHeader") {
            MenuImageHeader()
        }
        stickyHeader(contentType = "MenuSearchAndTagsHeader") {
            MenuSearchAndTagsHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onScroll = { tagIndex ->
                    val target = sectionStartIndices.getOrElse(tagIndex) { 0 }
                    scope.launch {
                        gridState.animateScrollToItem(target)
                    }
                },
                menuTags = menuTags,
            )
        }
        if (isEmpty) {
            item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = "NoProductsFound",
            ) {
                NoProductsFound()
            }
        } else {
            sections.forEach { section ->
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "Category",
                ) {
                    Text(
                        text = section.category.name.uppercase(),
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextSecondary,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
                items(
                    items = section.items,
                    key = { it.id },
                    contentType = { _ -> "MenuItems" },
                ) { product ->
                    ProductCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        product = product,
                        onPizzaClick = onPizzaClick,
                        onOtherItemAddClick = onOtherItemAddClick,
                        onOtherItemQuantityChange = onOtherItemQuantityChange,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "BottomSpacer",
                ) { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun PhoneMenuScreenContent(
    sections: List<MenuSectionDisplayModel>,
    menuTags: List<MenuTag>,
    sectionStartIndices: List<Int>,
    isEmpty: Boolean,
    listState: LazyListState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(contentType = "MenuImageHeader") {
            MenuImageHeader()
        }
        stickyHeader(contentType = "MenuSearchAndTagsHeader") {
            MenuSearchAndTagsHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                menuTags = menuTags,
                onScroll = { tagIndex ->
                    val target = sectionStartIndices.getOrElse(tagIndex) { 0 }
                    scope.launch { listState.animateScrollToItem(target) }
                },
            )
        }
        if (isEmpty) {
            item(contentType = "NoProductsFound") {
                NoProductsFound()
            }
        } else {
            sections.forEach { section ->
                item(contentType = "Category") {
                    Text(
                        text = section.category.name.uppercase(),
                        style = AppTypography.Label2SemiBold,
                        color = AppColors.TextSecondary,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
                items(
                    items = section.items,
                    key = { it.id },
                    contentType = { _ -> "MenuItems" },
                ) { product ->
                    ProductCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        product = product,
                        onPizzaClick = onPizzaClick,
                        onOtherItemAddClick = onOtherItemAddClick,
                        onOtherItemQuantityChange = onOtherItemQuantityChange,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
                item(contentType = "BottomSpacer") { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun MenuImageHeader(modifier: Modifier = Modifier) {
    if (isWideLayout().not()) {
        Image(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(R.drawable.img_home_screen),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun MenuSearchAndTagsHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScroll: (targetPosition: Int) -> Unit,
    menuTags: List<MenuTag>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.background(AppColors.Bg)) {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(DsR.drawable.error),
            contentDescription = null,
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = "Something went wrong. Please try again later.",
            style = AppTypography.Body1Medium,
            color = AppColors.TextPrimary,
        )
    }
}

@Composable
fun NoProductsFound(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(DsR.drawable.no_result),
            contentDescription = stringResource(R.string.no_orders_yet),
            modifier = Modifier.size(128.dp),
        )
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
    var current = 0
    sections.forEach { section ->
        indices += current
        current += 1 + section.items.size + 1
    }
    return indices
}

@PreviewPhoneTablet
@Composable
private fun MenuScreenContentPreview() {
    val previewMenuSections = listOf(
        MenuSectionDisplayModel(
            category = ProductCategory.PIZZA,
            items = listOf(
                MenuItemDisplayModel.Pizza(
                    id = "pizza_margherita",
                    name = "Margherita",
                    imageUrl = "https://picsum.photos/300/300?pizza1",
                    unitPrice = 8.5,
                    unitPriceFormatted = "€8.50",
                    category = ProductCategory.PIZZA,
                    description = "Tomato sauce, mozzarella, fresh basil",
                ),
                MenuItemDisplayModel.Pizza(
                    id = "pizza_pepperoni",
                    name = "Pepperoni",
                    imageUrl = "https://picsum.photos/300/300?pizza2",
                    unitPrice = 10.0,
                    unitPriceFormatted = "€10.00",
                    category = ProductCategory.PIZZA,
                    description = "Tomato sauce, mozzarella, pepperoni",
                ),
            ),
        ),

        MenuSectionDisplayModel(
            category = ProductCategory.DRINK,
            items = listOf(
                MenuItemDisplayModel.Other(
                    id = "drink_cola",
                    name = "Coca-Cola",
                    imageUrl = "https://picsum.photos/300/300?drink1",
                    unitPrice = 2.5,
                    unitPriceFormatted = "€2.50",
                    category = ProductCategory.DRINK,
                    quantity = 0,
                ),
                MenuItemDisplayModel.Other(
                    id = "drink_water",
                    name = "Mineral Water",
                    imageUrl = "https://picsum.photos/300/300?drink2",
                    unitPrice = 2.0,
                    unitPriceFormatted = "€2.00",
                    category = ProductCategory.DRINK,
                    quantity = 2,
                ),
            ),
        ),

        MenuSectionDisplayModel(
            category = ProductCategory.SAUCE,
            items = listOf(
                MenuItemDisplayModel.Other(
                    id = "sauce_bbq",
                    name = "BBQ Sauce",
                    imageUrl = "https://picsum.photos/300/300?sauce1",
                    unitPrice = 0.8,
                    unitPriceFormatted = "€0.80",
                    category = ProductCategory.SAUCE,
                    quantity = 1,
                ),
                MenuItemDisplayModel.Other(
                    id = "sauce_garlic",
                    name = "Garlic Sauce",
                    imageUrl = "https://picsum.photos/300/300?sauce2",
                    unitPrice = 0.8,
                    unitPriceFormatted = "€0.80",
                    category = ProductCategory.SAUCE,
                    quantity = 0,
                ),
            ),
        ),

        MenuSectionDisplayModel(
            category = ProductCategory.ICE_CREAM,
            items = listOf(
                MenuItemDisplayModel.Other(
                    id = "ice_cream_vanilla",
                    name = "Vanilla Ice Cream",
                    imageUrl = "https://picsum.photos/300/300?ice1",
                    unitPrice = 3.5,
                    unitPriceFormatted = "€3.50",
                    category = ProductCategory.ICE_CREAM,
                    quantity = 1,
                ),
            ),
        ),
    )
    LazyPizzaThemePreview {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "preview") { visible ->
                MenuScreenContent(
                    innerPadding = PaddingValues(0.dp), sections = previewMenuSections,
                    searchQuery = "",
                    onPizzaClick = {},
                    onSearchQueryChange = {},
                    menuTags = listOf(
                        MenuTag.PIZZA,
                        MenuTag.DRINK,
                        MenuTag.SAUCE,
                        MenuTag.ICE_CREAM,
                    ),
                    onOtherItemAddClick = {},
                    onOtherItemQuantityChange = { _, _ -> },
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun EmptyMenuScreenPreview() {
    LazyPizzaThemePreview {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "preview") { visible ->
                MenuScreenContent(
                    innerPadding = PaddingValues(0.dp),
                    sections = emptyList(),
                    searchQuery = "",
                    onPizzaClick = {},
                    onSearchQueryChange = {},
                    menuTags = emptyList(),
                    onOtherItemAddClick = {},
                    onOtherItemQuantityChange = { _, _ -> },
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                )
            }
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun MenuScreenErrorStatePreview() {
    LazyPizzaThemePreview {
        MenuScreenErrorState()
    }
}
