package com.example.lazypizza.features.home.data.sample

import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.models.Product
import com.example.lazypizza.features.home.domain.models.ProductCategory

object HomeSampleData {
    val sampleSections = listOf(
        CategorySection(
            category = ProductCategory.PIZZA,
            products = listOf(
                Product(
                    "p1",
                    ProductCategory.PIZZA,
                    "Margherita",
                    "Tomato sauce, mozzarella, fresh basil, olive oil",
                    8.99,
                    "https://example.com/pizza_margherita.png"
                ),
                Product(
                    "p2",
                    ProductCategory.PIZZA,
                    "Pepperoni",
                    "Tomato sauce, mozzarella, pepperoni",
                    9.99,
                    "https://example.com/pizza_pepperoni.png"
                ),
                Product(
                    "p3",
                    ProductCategory.PIZZA,
                    "Hawaiian",
                    "Tomato sauce, mozzarella, ham, pineapple",
                    10.49,
                    "https://example.com/pizza_hawaiian.png"
                ),
                Product(
                    "p4",
                    ProductCategory.PIZZA,
                    "BBQ Chicken",
                    "BBQ sauce, mozzarella, grilled chicken, onion, corn",
                    11.49,
                    "https://example.com/pizza_bbqchicken.png"
                ),
                Product(
                    "p5",
                    ProductCategory.PIZZA,
                    "Four Cheese",
                    "Mozzarella, gorgonzola, parmesan, ricotta",
                    11.99,
                    "https://example.com/pizza_fourcheese.png"
                ),
                Product(
                    "p6",
                    ProductCategory.PIZZA,
                    "Veggie Delight",
                    "Tomatoes, olives, bell pepper, onion, mushrooms",
                    9.79,
                    "https://example.com/pizza_veggiedelight.png"
                ),
                Product(
                    "p7",
                    ProductCategory.PIZZA,
                    "Meat Lovers",
                    "Tomato sauce, mozzarella, pepperoni, ham, bacon, sausage",
                    12.49,
                    "https://example.com/pizza_meatlovers.png"
                ),
                Product(
                    "p8",
                    ProductCategory.PIZZA,
                    "Spicy Inferno",
                    "Tomato sauce, mozzarella, spicy salami, jalapeños, red chili pepper",
                    11.29,
                    "https://example.com/pizza_spicyinferno.png"
                ),
                Product(
                    "p9",
                    ProductCategory.PIZZA,
                    "Seafood Special",
                    "Tomato sauce, mozzarella, shrimp, mussels, squid, parsley",
                    13.99,
                    "https://example.com/pizza_seafoodspecial.png"
                ),
                Product(
                    "p10",
                    ProductCategory.PIZZA,
                    "Truffle Mushroom",
                    "Cream sauce, mozzarella, mushrooms, truffle oil, parmesan",
                    12.99,
                    "https://example.com/pizza_trufflemushroom.png"
                )
            )
        ),
        CategorySection(
            category = ProductCategory.DRINKS,
            products = listOf(
                Product("d1", ProductCategory.DRINKS, "Mineral Water", "500ml bottle", 1.49, "https://example.com/drink_water.png"),
                Product("d2", ProductCategory.DRINKS, "7-Up", "Refreshing lemon-lime soda", 1.89, "https://example.com/drink_7up.png"),
                Product("d3", ProductCategory.DRINKS, "Pepsi", "Classic cola taste", 1.99, "https://example.com/drink_pepsi.png"),
                Product("d4", ProductCategory.DRINKS, "Orange Juice", "Fresh squeezed oranges", 2.49, "https://example.com/drink_orangejuice.png"),
                Product("d5", ProductCategory.DRINKS, "Apple Juice", "100% pure apple juice", 2.29, "https://example.com/drink_applejuice.png"),
                Product("d6", ProductCategory.DRINKS, "Iced Tea (Lemon)", "Chilled lemon-flavored tea", 2.19, "https://example.com/drink_icedtea.png")
            )
        ),
        CategorySection(
            category = ProductCategory.SAUCES,
            products = listOf(
                Product("s1", ProductCategory.SAUCES, "Garlic Sauce", "Creamy garlic flavor", 0.59, "https://example.com/sauce_garlic.png"),
                Product("s2", ProductCategory.SAUCES, "BBQ Sauce", "Sweet smoky barbecue taste", 0.59, "https://example.com/sauce_bbq.png"),
                Product("s3", ProductCategory.SAUCES, "Cheese Sauce", "Rich melted cheese dip", 0.89, "https://example.com/sauce_cheese.png"),
                Product("s4", ProductCategory.SAUCES, "Spicy Chili Sauce", "Hot and tangy chili kick", 0.59, "https://example.com/sauce_spicychili.png")
            )
        ),
        CategorySection(
            category = ProductCategory.ICE_CREAM,
            products = listOf(
                Product("i1", ProductCategory.ICE_CREAM, "Vanilla Ice Cream", "Classic creamy vanilla", 2.49, "https://example.com/icecream_vanilla.png"),
                Product("i2", ProductCategory.ICE_CREAM, "Chocolate Ice Cream", "Rich chocolate flavor", 2.49, "https://example.com/icecream_chocolate.png"),
                Product("i3", ProductCategory.ICE_CREAM, "Strawberry Ice Cream", "Sweet strawberry blend", 2.49, "https://example.com/icecream_strawberry.png"),
                Product("i4", ProductCategory.ICE_CREAM, "Cookies Ice Cream", "Cookies & cream goodness", 2.79, "https://example.com/icecream_cookies.png"),
                Product("i5", ProductCategory.ICE_CREAM, "Pistachio Ice Cream", "Nutty and creamy pistachio", 2.99, "https://example.com/icecream_pistachio.png"),
                Product("i6", ProductCategory.ICE_CREAM, "Mango Sorbet", "Refreshing tropical mango", 2.69, "https://example.com/icecream_mango.png")
            )
        )
    )
}
