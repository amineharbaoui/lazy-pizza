package com.example.menu.data.mapper

import com.example.core.model.ProductCategory
import com.example.menu.data.model.ProductDto
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.Topping
import javax.inject.Inject

class ProductDtoToDomainMapper @Inject constructor() {

    fun mapToMenuItem(dto: ProductDto): MenuItem = when (val categoryEnum = mapToProductCategory(dto.category)) {
        ProductCategory.PIZZA -> MenuItem.PizzaItem(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
            unitPrice = dto.price,
            description = dto.description,
            toppings = emptyList(),
            category = categoryEnum,
        )

        ProductCategory.DRINK,
        ProductCategory.SAUCE,
        ProductCategory.ICE_CREAM,
        ProductCategory.TOPPING,
        -> MenuItem.OtherMenuItem(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
            unitPrice = dto.price,
            category = categoryEnum,
        )
    }

    fun mapToTopping(dto: ProductDto): Topping {
        require(mapToProductCategory(dto.category) == ProductCategory.TOPPING) {
            "ProductDto must have category TOPPING to map to Topping"
        }
        return Topping(
            id = dto.id,
            name = dto.name,
            unitPrice = dto.price,
            imageUrl = dto.imageUrl,
        )
    }

    fun mapToProductCategory(category: String): ProductCategory = try {
        ProductCategory.valueOf(category)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid product category: $category", e)
    }

    fun mapListToMenuItems(dtos: List<ProductDto>): List<MenuItem> = dtos.map { mapToMenuItem(it) }

    fun mapListToToppings(dtos: List<ProductDto>): List<Topping> = dtos.map { mapToTopping(it) }
}
