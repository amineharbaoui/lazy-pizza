package com.example.domain.repository

import com.example.domain.model.MenuItem
import com.example.domain.model.MenuSection
import com.example.domain.model.Topping
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun observeMenuSections(): Flow<List<MenuSection>>
    fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?>
    fun observeToppings(): Flow<List<Topping>>
}
