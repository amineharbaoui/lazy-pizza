package com.example.menu.data.repository

import com.example.core.model.ProductCategory
import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSection
import com.example.menu.domain.model.MenuSnapshot
import com.example.menu.domain.model.MenuSnapshotResult
import com.example.menu.domain.model.Topping
import com.example.menu.domain.repository.MenuRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

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

    // A Firestore listener with no cache and no network can wait forever instead of failing.
    override suspend fun getMenuSnapshot(): MenuSnapshotResult = try {
        withTimeoutOrNull(MENU_SNAPSHOT_TIMEOUT) {
            combine(observeMenuSections(), observeToppings()) { sections, toppings ->
                MenuSnapshot(items = sections.flatMap { it.items }, toppings = toppings)
            }.first()
        }?.let(MenuSnapshotResult::Success) ?: MenuSnapshotResult.Unavailable
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        MenuSnapshotResult.Unavailable
    }

    private companion object {
        val MENU_SNAPSHOT_TIMEOUT = 10.seconds
    }
}
