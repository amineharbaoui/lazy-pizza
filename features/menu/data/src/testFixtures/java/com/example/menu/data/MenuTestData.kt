package com.example.menu.data

import com.example.core.model.ProductCategory
import com.example.menu.data.datasource.FakeProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.menu.data.model.ProductDto
import com.example.menu.data.repository.MenuRepositoryImpl
import com.example.menu.data.repository.RecommendedItemsRepositoryImpl
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.Topping
import com.example.menu.domain.repository.MenuRepository
import com.example.menu.domain.repository.RecommendedItemsRepository

class MenuTestData {

    private val remoteDataSource = FakeProductRemoteDataSource()
    private val mapper = ProductDtoToDomainMapper()

    val menuRepository: MenuRepository = MenuRepositoryImpl(remoteDataSource, mapper)
    val recommendedItemsRepository: RecommendedItemsRepository = RecommendedItemsRepositoryImpl(remoteDataSource, mapper)

    /** Replaces the menu with these items and toppings, at their `unitPrice`. */
    fun givenMenu(
        items: List<MenuItem> = emptyList(),
        toppings: List<Topping> = emptyList(),
    ) {
        remoteDataSource.products.value = items.map(::toDto) + toppings.map(::toDto)
    }

    fun givenMenuFailure(error: Exception = IllegalStateException(FORCED_FAILURE)) {
        remoteDataSource.failure = error
    }

    private fun toDto(item: MenuItem) = ProductDto(
        id = item.id,
        name = item.name,
        description = (item as? MenuItem.PizzaItem)?.description.orEmpty(),
        imageUrl = item.imageUrl,
        price = item.unitPrice,
        category = item.category.name,
    )

    private fun toDto(topping: Topping) = ProductDto(
        id = topping.id,
        name = topping.name,
        imageUrl = topping.imageUrl,
        price = topping.unitPrice,
        category = ProductCategory.TOPPING.name,
    )

    private companion object {
        const val FORCED_FAILURE = "Forced failure"
    }
}
