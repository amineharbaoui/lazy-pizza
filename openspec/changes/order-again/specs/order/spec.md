# Spec Delta

## Purpose

Lets signed-in users place pizza orders and work with their order history, including putting a past order back into the cart.

## ADDED Requirements

### Requirement: Past orders offer Order again
Every past order card on the Orders screen SHALL show an "Order again" button, whatever the order's status. Users who are not signed in see no order cards and therefore no "Order again" button.

#### Scenario: ORDER-01 Order again button on every past order
- **Level**: screenshot
- **GIVEN** a signed-in user with a completed, an in-progress and a canceled past order
- **WHEN** the Orders screen shows them
- **THEN** each order card shows an enabled "Order again" button

#### Scenario: ORDER-02 No Order again when signed out
- **Level**: screenshot
- **GIVEN** a user who is not signed in
- **WHEN** the Orders screen is shown
- **THEN** the screen shows the sign-in prompt and no "Order again" button

### Requirement: Order again adds available items to the cart at today's prices
Tapping "Order again" SHALL add every item of that order that is still on the menu to the current cart, with the past order's quantities and toppings, and with today's menu name and prices matched by product id. Items already in the cart SHALL stay. A drink, sauce or ice cream already in the cart SHALL get its quantity increased. A pizza SHALL always get its own new cart line. When nothing was skipped, the app SHALL open the cart.

#### Scenario: ORDER-03 Whole order still on the menu
- **Level**: acceptance
- **GIVEN** a signed-in user with an empty cart and a past order of 2 x Margherita with 1 x Extra cheese and 1 x Cola, all still on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds 2 x Margherita with 1 x Extra cheese and 1 x Cola
- **AND** the app opens the cart without showing a dialog

#### Scenario: ORDER-04 Existing cart items are kept
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Pepperoni, and a past order of 1 x Cola still on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds 1 x Pepperoni and 1 x Cola

#### Scenario: ORDER-05 Same drink adds to its quantity
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 2 x Cola, and a past order of 1 x Cola still on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds a single Cola line with quantity 3

#### Scenario: ORDER-06 Same pizza gets its own line
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Margherita, and a past order of 1 x Margherita still on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds two separate Margherita lines, each with quantity 1

#### Scenario: ORDER-07 Today's prices apply per unit and per topping
- **Level**: acceptance
- **GIVEN** a past order of 2 x Margherita paid $8.00 each, each with 2 x Extra cheese paid $1.00 each, and today's menu prices Margherita at $9.00 and Extra cheese at $1.50
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart's Margherita line totals $24.00

#### Scenario: ORDER-08 Buttons are disabled while Order again runs
- **Level**: acceptance
- **GIVEN** a signed-in user with an empty cart and a past order of 1 x Cola still on the menu
- **WHEN** the user taps "Order again" and taps it again before the first tap has finished
- **THEN** every "Order again" button is disabled until the first tap finishes
- **AND** the cart holds 1 x Cola

### Requirement: Order again skips what is no longer on the menu and tells the user
An item SHALL be skipped when its product is not on today's menu. A topping is on the menu when it is in today's topping list. A pizza on the menu SHALL be added without its toppings that are not. When anything was skipped, a dialog SHALL name each one and stay until the user closes it. If at least one item was added, the dialog offers "Go to cart" and "Close". If none was added, the cart SHALL stay unchanged and the dialog offers only "Close". "Close" SHALL leave the user on Orders.

#### Scenario: ORDER-09 Unavailable item skipped and named
- **Level**: acceptance
- **GIVEN** a signed-in user with an empty cart and a past order of 1 x Margherita and 1 x Lemonade, where Lemonade is no longer on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds 1 x Margherita only
- **AND** a dialog titled "Some items are no longer available" lists "Lemonade" and offers "Go to cart" and "Close"

#### Scenario: ORDER-10 Unavailable topping dropped and named
- **Level**: acceptance
- **GIVEN** a signed-in user with an empty cart and a past order of 1 x Margherita with 1 x Extra cheese and 1 x Truffle, where Truffle is not in today's topping list
- **WHEN** the user taps "Order again" on that order
- **THEN** the cart holds 1 x Margherita with 1 x Extra cheese only
- **AND** a dialog titled "Some items are no longer available" lists "Truffle on Margherita"

#### Scenario: ORDER-11 Go to cart from the dialog
- **Level**: acceptance
- **GIVEN** the user tapped "Order again" on a past order of 1 x Margherita and 1 x Lemonade, where Lemonade is no longer on the menu, and the dialog is showing
- **WHEN** the user taps "Go to cart"
- **THEN** the dialog closes, the app opens the cart, and the cart holds 1 x Margherita

#### Scenario: ORDER-12 Close the dialog after a partial reorder
- **Level**: acceptance
- **GIVEN** the user tapped "Order again" on a past order of 1 x Margherita and 1 x Lemonade, where Lemonade is no longer on the menu, and the dialog is showing
- **WHEN** the user taps "Close" or presses back
- **THEN** the dialog closes and the user stays on the Orders screen
- **AND** the cart holds 1 x Margherita

#### Scenario: ORDER-13 Nothing in the order is still on the menu
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Pepperoni, and a past order of 1 x Lemonade, which is no longer on the menu
- **WHEN** the user taps "Order again" on that order
- **THEN** a dialog titled "None of these items are on the menu anymore" lists "Lemonade" and offers only "Close"
- **AND** the cart still holds only 1 x Pepperoni

#### Scenario: ORDER-14 Close after nothing was available
- **Level**: acceptance
- **GIVEN** the "None of these items are on the menu anymore" dialog is showing after "Order again"
- **WHEN** the user taps "Close"
- **THEN** the dialog closes and the user stays on the Orders screen

#### Scenario: ORDER-15 Skipped items dialog
- **Level**: screenshot
- **GIVEN** an "Order again" result where Lemonade was skipped and Truffle was dropped from Margherita
- **WHEN** the Orders screen shows the dialog
- **THEN** the dialog is titled "Some items are no longer available", lists "Lemonade" and "Truffle on Margherita", and shows "Go to cart" and "Close"

#### Scenario: ORDER-16 Nothing available dialog
- **Level**: screenshot
- **GIVEN** an "Order again" result where no item could be added and Lemonade was skipped
- **WHEN** the Orders screen shows the dialog
- **THEN** the dialog is titled "None of these items are on the menu anymore", lists "Lemonade", and shows only "Close"

### Requirement: Order again fails safely
When the menu cannot be loaded, the menu comes back empty, or the cart cannot be updated, the Orders screen SHALL show an error message, the cart SHALL be left exactly as it was, and the user SHALL stay on Orders and be able to tap "Order again" again. An empty menu SHALL count as a failed load, not as "nothing available". No raw technical error text SHALL be shown.

#### Scenario: ORDER-17 Menu cannot be loaded
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Pepperoni, a past order of 1 x Cola, and a menu that fails to load
- **WHEN** the user taps "Order again" on that order
- **THEN** the screen shows "Couldn't load the menu. Please try again."
- **AND** the cart still holds only 1 x Pepperoni
- **AND** the user is still on the Orders screen

#### Scenario: ORDER-18 Menu comes back empty
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Pepperoni, a past order of 1 x Cola, and a menu that loads with no products
- **WHEN** the user taps "Order again" on that order
- **THEN** the screen shows "Couldn't load the menu. Please try again." and no dialog
- **AND** the cart still holds only 1 x Pepperoni

#### Scenario: ORDER-19 Cart cannot be updated
- **Level**: acceptance
- **GIVEN** a signed-in user whose cart holds 1 x Pepperoni, a past order of 1 x Margherita and 1 x Cola still on the menu, and a cart that fails to save
- **WHEN** the user taps "Order again" on that order
- **THEN** the screen shows "Couldn't update your cart. Please try again."
- **AND** the cart still holds only 1 x Pepperoni
- **AND** the user is still on the Orders screen

#### Scenario: ORDER-20 Retry after the menu failed
- **Level**: acceptance
- **GIVEN** "Order again" on a past order of 1 x Cola failed because the menu could not be loaded, and the menu now loads
- **WHEN** the user taps "Order again" on the same order
- **THEN** the cart holds 1 x Cola and the app opens the cart

#### Scenario: ORDER-21 Retry after the cart failed
- **Level**: acceptance
- **GIVEN** "Order again" on a past order of 1 x Cola failed because the cart could not be saved, and the cart now saves
- **WHEN** the user taps "Order again" on the same order
- **THEN** the cart holds 1 x Cola and the app opens the cart

### Requirement: Order again end to end
A signed-in user SHALL be able to go from a past order to a cart holding its items on a real device, against real Firebase.

#### Scenario: ORDER-22 Reorder from history to cart
- **Level**: journey
- **GIVEN** the test user is signed in and has just placed an order of 1 x Margherita through checkout, so the cart is empty
- **WHEN** the user opens Orders and taps "Order again" on the most recent order
- **THEN** the cart screen opens and lists Margherita
