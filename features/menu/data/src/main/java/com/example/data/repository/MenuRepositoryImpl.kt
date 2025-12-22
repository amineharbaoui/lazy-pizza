package com.example.data.repository

import com.example.data.datasource.ProductRemoteDataSource
import com.example.data.mapper.ProductDtoToDomainMapper
import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.Topping
import com.example.domain.repository.MenuRepository
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productMapper: ProductDtoToDomainMapper,
) : MenuRepository {

    override fun observeMenuSections(): Flow<List<MenuSection>> = combine(
        flow = productRemoteDataSource.observeProductsByCategory(ProductCategory.PIZZA),
        flow2 = productRemoteDataSource.observeProductsByCategory(ProductCategory.DRINK),
        flow3 = productRemoteDataSource.observeProductsByCategory(ProductCategory.SAUCE),
        flow4 = productRemoteDataSource.observeProductsByCategory(ProductCategory.ICE_CREAM),
    ) { pizzas, drinks, sauces, iceCreams ->
        listOf(
            MenuSection(category = ProductCategory.PIZZA, items = productMapper.mapListToMenuItems(pizzas)),
            MenuSection(category = ProductCategory.DRINK, items = productMapper.mapListToMenuItems(drinks)),
            MenuSection(category = ProductCategory.SAUCE, items = productMapper.mapListToMenuItems(sauces)),
            MenuSection(category = ProductCategory.ICE_CREAM, items = productMapper.mapListToMenuItems(iceCreams)),
        )
    }

    override fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?> = productRemoteDataSource
        .observeProductById(id).map { dto ->
            dto?.let { productMapper.mapToMenuItem(it) } as? MenuItem.PizzaItem
        }

    override fun observeToppings(): Flow<List<Topping>> = productRemoteDataSource
        .observeProductsByCategory(category = ProductCategory.TOPPING)
        .map(productMapper::mapListToToppings)
}
