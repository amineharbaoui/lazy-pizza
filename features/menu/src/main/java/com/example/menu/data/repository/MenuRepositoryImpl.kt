package com.example.menu.data.repository

import com.example.menu.data.datasource.MenuRemoteDataSource
import com.example.menu.data.mapper.toMenuItem
import com.example.menu.data.mapper.toTopping
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSection
import com.example.menu.domain.model.ProductCategory
import com.example.menu.domain.model.Topping
import com.example.menu.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val remote: MenuRemoteDataSource,
) : MenuRepository {

    override fun observeMenuSections(): Flow<List<MenuSection>> =
        combine(
            remote.observeProductsByCategory(ProductCategory.PIZZA),
            remote.observeProductsByCategory(ProductCategory.DRINK),
            remote.observeProductsByCategory(ProductCategory.SAUCE),
            remote.observeProductsByCategory(ProductCategory.ICE_CREAM)
        ) { pizzas, drinks, sauces, iceCreams ->
            listOf(
                MenuSection(category = ProductCategory.PIZZA, items = pizzas.map { it.toMenuItem() }),
                MenuSection(category = ProductCategory.DRINK, items = drinks.map { it.toMenuItem() }),
                MenuSection(category = ProductCategory.SAUCE, items = sauces.map { it.toMenuItem() }),
                MenuSection(category = ProductCategory.ICE_CREAM, items = iceCreams.map { it.toMenuItem() }),
            )
        }

    override fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?> =
        remote.observeProductById(id).map { dto ->
            dto?.toMenuItem() as? MenuItem.PizzaItem
        }

    override fun observeToppings(): Flow<List<Topping>> =
        remote.observeProductsByCategory(ProductCategory.TOPPING)
            .map { list -> list.map { it.toTopping() } }
}