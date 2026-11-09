package com.example.menu.data.repository

import app.cash.turbine.test
import com.example.core.model.ProductCategory
import com.example.menu.data.MenuTestData
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSection
import com.example.menu.domain.model.Topping
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MenuRepositoryImplTest {

    private val testData = MenuTestData()

    @Test
    fun `observeMenuSections when the menu has items then emits them by category`() = runTest {
        // Given
        val margherita = pizza()
        val cola = drink()
        testData.givenMenu(items = listOf(margherita, cola), toppings = listOf(topping()))

        // When
        testData.menuRepository.observeMenuSections().test {
            // Then
            assertThat(awaitItem()).containsExactly(
                MenuSection(ProductCategory.PIZZA, listOf(margherita)),
                MenuSection(ProductCategory.DRINK, listOf(cola)),
                MenuSection(ProductCategory.SAUCE, emptyList()),
                MenuSection(ProductCategory.ICE_CREAM, emptyList()),
            )
        }
    }

    @Test
    fun `observeToppings when the menu has toppings then emits them with their prices`() = runTest {
        // Given
        val extraCheese = topping()
        testData.givenMenu(items = listOf(pizza()), toppings = listOf(extraCheese))

        // When
        testData.menuRepository.observeToppings().test {
            // Then
            assertThat(awaitItem()).containsExactly(extraCheese)
        }
    }

    @Test
    fun `observeMenuSections when the menu fails to load then throws`() = runTest {
        // Given
        testData.givenMenuFailure(IllegalStateException(FAILURE))

        // When
        testData.menuRepository.observeMenuSections().test {
            // Then
            assertThat(awaitError()).isInstanceOf(IllegalStateException::class.java).hasMessage(FAILURE)
        }
    }

    private fun pizza() = MenuItem.PizzaItem(
        id = MARGHERITA_ID,
        name = MARGHERITA,
        imageUrl = IMAGE_URL,
        unitPrice = MARGHERITA_PRICE,
        category = ProductCategory.PIZZA,
        description = MARGHERITA_DESCRIPTION,
    )

    private fun drink() = MenuItem.OtherMenuItem(
        id = COLA_ID,
        name = COLA,
        imageUrl = IMAGE_URL,
        unitPrice = COLA_PRICE,
        category = ProductCategory.DRINK,
    )

    private fun topping() = Topping(
        id = EXTRA_CHEESE_ID,
        name = EXTRA_CHEESE,
        unitPrice = EXTRA_CHEESE_PRICE,
        imageUrl = IMAGE_URL,
    )

    private companion object {
        const val MARGHERITA_ID = "margherita"
        const val MARGHERITA = "Margherita"
        const val MARGHERITA_DESCRIPTION = "Tomato sauce, mozzarella"
        const val MARGHERITA_PRICE = 9.99
        const val COLA_ID = "cola"
        const val COLA = "Cola"
        const val COLA_PRICE = 2.49
        const val EXTRA_CHEESE_ID = "extra-cheese"
        const val EXTRA_CHEESE = "Extra cheese"
        const val EXTRA_CHEESE_PRICE = 1.5
        const val IMAGE_URL = "https://example.com/image.png"
        const val FAILURE = "Forced failure"
    }
}
