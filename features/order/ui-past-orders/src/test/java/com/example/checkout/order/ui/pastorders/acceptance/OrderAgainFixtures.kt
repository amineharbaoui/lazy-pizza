package com.example.checkout.order.ui.pastorders.acceptance

import com.example.cart.domain.model.CartItem
import com.example.core.model.OrderStatus
import com.example.core.model.ProductCategory
import com.example.menu.data.model.ProductDto
import com.example.order.data.model.OrderDto
import com.example.order.data.model.OrderItemDto
import com.example.order.data.model.OrderToppingDto

const val USER_ID = "user-1"
const val ORDER_NUMBER = "order-1"
private const val ORDER_CREATED_AT_EPOCH_MS = 1_760_000_000_000L
private const val IMAGE_URL = "https://example.com/product.png"

const val MARGHERITA_ID = "margherita"
const val MARGHERITA_NAME = "Margherita"
const val PEPPERONI_ID = "pepperoni"
const val PEPPERONI_NAME = "Pepperoni"
const val COLA_ID = "cola"
const val COLA_NAME = "Cola"
const val EXTRA_CHEESE_ID = "extra-cheese"
const val EXTRA_CHEESE_NAME = "Extra cheese"

/** Only exist in past orders, never on today's menu. */
const val LEMONADE_ID = "lemonade"
const val LEMONADE_NAME = "Lemonade"
const val TRUFFLE_ID = "truffle"
const val TRUFFLE_NAME = "Truffle"

// Today's menu prices
const val MARGHERITA_PRICE_TODAY = 9.00
const val PEPPERONI_PRICE_TODAY = 10.00
const val COLA_PRICE_TODAY = 2.50
const val EXTRA_CHEESE_PRICE_TODAY = 1.50

// Prices paid in past orders
const val MARGHERITA_PRICE_PAST = 8.00
const val EXTRA_CHEESE_PRICE_PAST = 1.00
private const val PEPPERONI_PRICE_PAST = 9.00
private const val COLA_PRICE_PAST = 2.00
private const val LEMONADE_PRICE_PAST = 2.20
private const val TRUFFLE_PRICE_PAST = 3.00

/** ORDER-07: 2 x (Margherita $9.00 + 2 x Extra cheese $1.50). */
const val EXPECTED_MARGHERITA_LINE_TOTAL_TODAY = 24.00

private const val EXISTING_PEPPERONI_LINE_ID = "existing-pepperoni-line"
private const val EXISTING_MARGHERITA_LINE_ID = "existing-margherita-line"

// region Today's menu

private fun product(
    id: String,
    name: String,
    price: Double,
    category: ProductCategory,
) = ProductDto(id = id, name = name, imageUrl = IMAGE_URL, price = price, category = category.name)

val todaysMenu: Array<ProductDto> = arrayOf(
    product(MARGHERITA_ID, MARGHERITA_NAME, MARGHERITA_PRICE_TODAY, ProductCategory.PIZZA),
    product(PEPPERONI_ID, PEPPERONI_NAME, PEPPERONI_PRICE_TODAY, ProductCategory.PIZZA),
    product(COLA_ID, COLA_NAME, COLA_PRICE_TODAY, ProductCategory.DRINK),
    product(EXTRA_CHEESE_ID, EXTRA_CHEESE_NAME, EXTRA_CHEESE_PRICE_TODAY, ProductCategory.TOPPING),
)

// endregion

// region Past orders

fun pastOrder(vararg items: OrderItemDto) = OrderDto(
    orderNumber = ORDER_NUMBER,
    userId = USER_ID,
    createdAtEpochMs = ORDER_CREATED_AT_EPOCH_MS,
    pickupType = "ASAP",
    pickupAtEpochMs = ORDER_CREATED_AT_EPOCH_MS,
    status = OrderStatus.COMPLETED.name,
    total = items.sumOf { item -> (item.unitPrice + item.toppings.sumOf { it.unitPrice * it.quantity }) * item.quantity },
    items = items.toList(),
)

fun pastMargherita(
    quantity: Int = 1,
    vararg toppings: OrderToppingDto,
) = OrderItemDto(
    productId = MARGHERITA_ID,
    name = MARGHERITA_NAME,
    unitPrice = MARGHERITA_PRICE_PAST,
    quantity = quantity,
    category = ProductCategory.PIZZA.name,
    toppings = toppings.toList(),
)

fun pastCola(quantity: Int = 1) = OrderItemDto(
    productId = COLA_ID,
    name = COLA_NAME,
    unitPrice = COLA_PRICE_PAST,
    quantity = quantity,
    category = ProductCategory.DRINK.name,
)

fun pastLemonade(quantity: Int = 1) = OrderItemDto(
    productId = LEMONADE_ID,
    name = LEMONADE_NAME,
    unitPrice = LEMONADE_PRICE_PAST,
    quantity = quantity,
    category = ProductCategory.DRINK.name,
)

fun pastExtraCheese(quantity: Int = 1) = OrderToppingDto(
    id = EXTRA_CHEESE_ID,
    name = EXTRA_CHEESE_NAME,
    unitPrice = EXTRA_CHEESE_PRICE_PAST,
    quantity = quantity,
)

fun pastTruffle(quantity: Int = 1) = OrderToppingDto(
    id = TRUFFLE_ID,
    name = TRUFFLE_NAME,
    unitPrice = TRUFFLE_PRICE_PAST,
    quantity = quantity,
)

// endregion

// region Existing cart lines

fun cartPepperoni() = CartItem.Pizza(
    lineId = EXISTING_PEPPERONI_LINE_ID,
    productId = PEPPERONI_ID,
    name = PEPPERONI_NAME,
    imageUrl = IMAGE_URL,
    quantity = 1,
    category = ProductCategory.PIZZA,
    unitPrice = PEPPERONI_PRICE_PAST,
    toppings = emptyList(),
)

fun cartMargherita() = CartItem.Pizza(
    lineId = EXISTING_MARGHERITA_LINE_ID,
    productId = MARGHERITA_ID,
    name = MARGHERITA_NAME,
    imageUrl = IMAGE_URL,
    quantity = 1,
    category = ProductCategory.PIZZA,
    unitPrice = MARGHERITA_PRICE_TODAY,
    toppings = emptyList(),
)

/** Same `lineId` the menu screen gives a non-pizza line: its product id. */
fun cartCola(quantity: Int) = CartItem.Other(
    lineId = COLA_ID,
    productId = COLA_ID,
    name = COLA_NAME,
    imageUrl = IMAGE_URL,
    quantity = quantity,
    category = ProductCategory.DRINK,
    unitPrice = COLA_PRICE_TODAY,
)

// endregion

// region Cart assertions

/** A cart line without its generated `lineId`, so pizza lines compare by content. */
data class CartLine(
    val productId: String,
    val quantity: Int,
    val toppingQuantities: Map<String, Int> = emptyMap(),
)

fun CartItem.toCartLine() = CartLine(
    productId = productId,
    quantity = quantity,
    toppingQuantities = (this as? CartItem.Pizza)?.toppings?.associate { it.id to it.quantity }.orEmpty(),
)

fun pepperoniLine() = CartLine(PEPPERONI_ID, quantity = 1)

fun colaLine(quantity: Int = 1) = CartLine(COLA_ID, quantity)

fun margheritaLine(
    quantity: Int = 1,
    toppingQuantities: Map<String, Int> = emptyMap(),
) = CartLine(MARGHERITA_ID, quantity, toppingQuantities)

// endregion
