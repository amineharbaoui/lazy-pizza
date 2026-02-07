package com.example.menu.data.repository

import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.Topping
import com.example.domain.repository.MenuRepository
import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productMapper: ProductDtoToDomainMapper,
) : MenuRepository {

    override fun observeMenuSections(): Flow<List<MenuSection>> {
        val categories = listOf(
            ProductCategory.PIZZA,
            ProductCategory.DRINK,
            ProductCategory.SAUCE,
            ProductCategory.ICE_CREAM,
        )
        val categoryFlows = categories.map { category ->
            productRemoteDataSource.observeProductsByCategory(category)
        }
        return combine(categoryFlows) { productsArray ->
            productsArray.mapIndexed { index, products ->
                MenuSection(
                    category = categories[index],
                    items = productMapper.mapListToMenuItems(products),
                )
            }
        }
    }

    override fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?> = productRemoteDataSource.observeProductById(id)
        .map { dto ->
            if (dto == null) return@map null

            val item = productMapper.mapToMenuItem(dto)
            require(item is MenuItem.PizzaItem) {
                "Product $id is not a PizzaItem (got ${item::class.simpleName})"
            }
            item
        }

    override fun observeToppings(): Flow<List<Topping>> = productRemoteDataSource
        .observeProductsByCategory(category = ProductCategory.TOPPING)
        .map(productMapper::mapListToToppings)
}
