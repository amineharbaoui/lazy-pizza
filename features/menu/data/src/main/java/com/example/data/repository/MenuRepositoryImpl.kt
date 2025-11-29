package com.example.data.repository

import com.example.data.datasource.ProductRemoteDataSource
import com.example.data.mapper.toMenuItem
import com.example.data.mapper.toTopping
import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.ProductCategory
import com.example.domain.model.Topping
import com.example.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : MenuRepository {

    override fun observeMenuSections(): Flow<List<MenuSection>> = combine(
        flow = productRemoteDataSource.observeProductsByCategory(ProductCategory.PIZZA),
        flow2 = productRemoteDataSource.observeProductsByCategory(ProductCategory.DRINK),
        flow3 = productRemoteDataSource.observeProductsByCategory(ProductCategory.SAUCE),
        flow4 = productRemoteDataSource.observeProductsByCategory(ProductCategory.ICE_CREAM),
    ) { pizzas, drinks, sauces, iceCreams ->
        listOf(
            MenuSection(category = ProductCategory.PIZZA, items = pizzas.map { it.toMenuItem() }),
            MenuSection(category = ProductCategory.DRINK, items = drinks.map { it.toMenuItem() }),
            MenuSection(category = ProductCategory.SAUCE, items = sauces.map { it.toMenuItem() }),
            MenuSection(category = ProductCategory.ICE_CREAM, items = iceCreams.map { it.toMenuItem() }),
        )
    }

    override fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?> = productRemoteDataSource.observeProductById(id).map { dto ->
        dto?.toMenuItem() as? MenuItem.PizzaItem
    }

    override fun observeToppings(): Flow<List<Topping>> = productRemoteDataSource.observeProductsByCategory(ProductCategory.TOPPING)
        .map { list -> list.map { it.toTopping() } }
}
