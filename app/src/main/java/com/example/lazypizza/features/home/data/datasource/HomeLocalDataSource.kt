package com.example.lazypizza.features.home.data.datasource

import com.example.lazypizza.features.home.data.sample.HomeSampleData
import com.example.lazypizza.features.home.domain.Product
import javax.inject.Inject

class HomeLocalDataSource @Inject constructor() {
    fun getProducts(): List<Product> = HomeSampleData.products
}
