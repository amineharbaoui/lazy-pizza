package com.example.uilogin.pizzadetail

import com.example.domain.model.CartItem
import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.ProductCategory
import com.example.uilogin.utils.formatting.toFormattedCurrency

fun MenuSection.toDisplayModel() = MenuSectionDisplayModel(
    category = category,
    items = items.map { it.toDisplayModel() },
)

fun MenuItem.toDisplayModel(): MenuItemDisplayModel = when (this) {
    is MenuItem.PizzaItem -> MenuItemDisplayModel.Pizza(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
        formattedPrice = price.toFormattedCurrency(),
        description = description,
    )

    is MenuItem.OtherMenuItem -> MenuItemDisplayModel.Other(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
        formattedPrice = price.toFormattedCurrency(),
        category = category,
        quantity = 0,
    )
}

fun ProductCategory.toMenuTag(): MenuTag? = when (this) {
    ProductCategory.PIZZA -> MenuTag.PIZZA
    ProductCategory.DRINK -> MenuTag.DRINK
    ProductCategory.SAUCE -> MenuTag.SAUCE
    ProductCategory.ICE_CREAM -> MenuTag.ICE_CREAM
    ProductCategory.TOPPING -> null
}

fun MenuItemDisplayModel.toSimpleCartItem(quantity: Int): CartItem.Other = CartItem.Other(
    lineId = id,
    productId = id,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    category = (this as MenuItemDisplayModel.Other).category.name,
)

fun mapToMenuUiState(
    menuSections: List<MenuSection>,
    cart: com.example.domain.model.Cart,
    searchQuery: String,
): MenuUiState {
    val menuSectionsDisplayModel = menuSections.map { it.toDisplayModel() }

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
        .mapNotNull { it.category.toMenuTag() }
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

    return MenuUiState.Success(
        originalMenu = menuWithQuantities,
        filteredMenu = filtered,
        menuTags = tags,
        searchQuery = searchQuery,
    )
}
