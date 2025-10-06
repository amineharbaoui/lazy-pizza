package com.example.lazypizza.features.home.presentation

import com.example.lazypizza.features.home.domain.Product
import com.example.lazypizza.features.home.domain.ProductCategory

fun sampleProducts(): List<Product> = listOf(
    // 🍕.pizzas
    Product(
        id = "pizza_margherita",
        category = ProductCategory.PIZZA,
        name = "Margherita",
        description = "Tomato sauce, mozzarella, fresh basil, olive oil",
        price = 8.99,
        imageUrl = "https://www.cicis.com/media/1243/pizza_trad_pepperoni.png"
    ),
    Product(
        id = "pizza_bbq",
        category = ProductCategory.PIZZA,
        name = "BBQ Chicken",
        description = "BBQ sauce, mozzarella, grilled chicken, onion, corn",
        price = 11.49,
        imageUrl = "https://www.cicis.com/media/1249/pizza_bbqchicken.png"
    ),
    Product(
        id = "pizza_truffle",
        category = ProductCategory.PIZZA,
        name = "Truffle Mushroom",
        description = "Cream sauce, mushrooms, truffle oil, parmesan",
        price = 12.99,
        imageUrl = "https://www.cicis.com/media/1247/pizza_veggie.png"
    ),

    // 🥤 Drinks
    Product(
        id = "drink_pepsi",
        category = ProductCategory.DRINKS,
        name = "Pepsi",
        description = "Refreshing cola beverage",
        price = 1.99,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Pepsi_can_330ml.jpg/320px-Pepsi_can_330ml.jpg"
    ),
    Product(
        id = "drink_orange",
        category = ProductCategory.DRINKS,
        name = "Orange Juice",
        description = "100% pure orange juice",
        price = 2.49,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/0/0b/Orange_juice_1.jpg"
    ),

    // 🍯 Sauces
    Product(
        id = "sauce_garlic",
        category = ProductCategory.SAUCES,
        name = "Garlic Sauce",
        description = "Creamy garlic dip",
        price = 0.59,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/50/Garlic_Sauce.jpg"
    ),
    Product(
        id = "sauce_bbq",
        category = ProductCategory.SAUCES,
        name = "BBQ Sauce",
        description = "Smoky barbecue flavor",
        price = 0.59,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/8/87/BBQ_sauce.jpg"
    ),

    // 🍨 Ice Cream
    Product(
        id = "ice_vanilla",
        category = ProductCategory.ICE_CREAM,
        name = "Vanilla Ice Cream",
        description = "Classic vanilla flavor",
        price = 2.49,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/8/84/Vanilla_ice_cream_cone.jpg"
    ),
    Product(
        id = "ice_chocolate",
        category = ProductCategory.ICE_CREAM,
        name = "Chocolate Ice Cream",
        description = "Rich chocolate delight",
        price = 2.49,
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/1f/Chocolate_ice_cream_in_a_bowl.jpg"
    )
)