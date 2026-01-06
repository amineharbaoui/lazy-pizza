package com.example.menu.home.mapper

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.menu.home.MenuContentUiState
import com.example.menu.home.MenuItemDisplayModel
import com.example.menu.home.MenuSectionDisplayModel
import com.example.menu.home.MenuTag
import com.example.menu.utils.formatting.CurrencyFormatter
import com.example.model.ProductCategory
import javax.inject.Inject

class MenuDomainToUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {

    fun mapToMenuContentUiState(
        menuSections: List<MenuSection>,
        cart: Cart,
        searchQuery: String,
    ): MenuContentUiState {
        val menuSectionsDisplayModel = menuSections.map { mapMenuSection(it) }

        val qtyByProductId = cart.items
            .filterIsInstance<CartItem.Other>()
            .associate { it.productId to it.quantity }

        val menuWithQuantities = menuSectionsDisplayModel.map { section ->
            section.copy(
                items = section.items.map { item ->
                    if (item is MenuItemDisplayModel.Other) {
                        val qty = qtyByProductId[item.id] ?: 0
                        item.copy(quantity = qty)
                    } else {
                        item
                    }
                },
            )
        }

        val tags = menuWithQuantities
            .mapNotNull { mapCategoryToMenuTag(it.category) }
            .distinct()

        val filtered = if (searchQuery.isBlank()) {
            menuWithQuantities
        } else {
            menuWithQuantities.mapNotNull { section ->
                val filteredItems = section.items.filter { item ->
                    item.name.contains(searchQuery, ignoreCase = true)
                }
                if (filteredItems.isEmpty()) null else section.copy(items = filteredItems)
            }
        }

        return MenuContentUiState.Ready(
            originalMenu = menuWithQuantities,
            filteredMenu = filtered,
            menuTags = tags,
            searchQuery = searchQuery,
        )
    }

    fun mapMenuSection(section: MenuSection): MenuSectionDisplayModel = MenuSectionDisplayModel(
        category = section.category,
        items = section.items.map { mapMenuItem(it) },
    )

    fun mapMenuItem(item: MenuItem): MenuItemDisplayModel = when (item) {
        is MenuItem.PizzaItem -> MenuItemDisplayModel.Pizza(
            id = item.id,
            name = item.name,
            imageUrl = item.imageUrl,
            unitPrice = item.unitPrice,
            unitPriceFormatted = currencyFormatter.format(item.unitPrice),
            description = item.description,
            category = item.category,
        )

        is MenuItem.OtherMenuItem -> MenuItemDisplayModel.Other(
            id = item.id,
            name = item.name,
            imageUrl = item.imageUrl,
            unitPrice = item.unitPrice,
            unitPriceFormatted = currencyFormatter.format(item.unitPrice),
            category = item.category,
            quantity = 0,
        )
    }

    fun mapCategoryToMenuTag(category: ProductCategory): MenuTag? = when (category) {
        ProductCategory.PIZZA -> MenuTag.PIZZA
        ProductCategory.DRINK -> MenuTag.DRINK
        ProductCategory.SAUCE -> MenuTag.SAUCE
        ProductCategory.ICE_CREAM -> MenuTag.ICE_CREAM
        ProductCategory.TOPPING -> null
    }

    fun mapToSimpleCartItem(
        menuItem: MenuItemDisplayModel,
        quantity: Int,
    ): CartItem.Other = CartItem.Other(
        lineId = menuItem.id,
        productId = menuItem.id,
        name = menuItem.name,
        imageUrl = menuItem.imageUrl,
        unitPrice = menuItem.unitPrice,
        quantity = quantity,
        category = menuItem.category,
    )
}
