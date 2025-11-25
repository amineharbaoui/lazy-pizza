package com.example.lazypizza.features.home.data.repository

import com.example.core.firebase.firestore.datasource.ProductDatasource
import com.example.lazypizza.data.mapper.toDomain
import com.example.lazypizza.features.home.data.mapper.toSections
import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import com.example.menu.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val productDatasource: ProductDatasource,
) : HomeRepository {

    override fun observeHomeSections(): Flow<List<CategorySection>> =
        productDatasource.observeSections().map { list ->
            list.map { it.toDomain() }
                .toSections()
                .filterNot { it.category == ProductCategory.TOPPING }
        }.also {  }
}
