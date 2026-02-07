package com.example.menu.domain.repository

import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSection
import com.example.menu.domain.model.Topping
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun observeMenuSections(): Flow<List<MenuSection>>
    fun observePizzaById(id: String): Flow<MenuItem.PizzaItem?>
    fun observeToppings(): Flow<List<Topping>>
}
