# Proposal

## Why

Repeat customers rebuild the same order by hand: open the menu, find each pizza, pick the same toppings again. Order history already holds every item. One tap should put them back in the cart.

## What Changes

- Each past order card on the Orders screen gets an **Order again** button.
- Tapping it adds the order's items to the current cart, on top of anything already there. Quantities and toppings come from the past order.
- Prices come from today's menu, not the price paid back then. This is the same price as adding the item by hand.
- Items no longer on the menu are skipped. A pizza still on the menu keeps the toppings that are still on the menu and drops the ones that are not.
- If anything was skipped or dropped, a dialog lists it before the user leaves the Orders screen. The dialog has "Go to cart" and "Close" buttons. If nothing was skipped, the user goes straight to the cart.
- If nothing in the order is still on the menu, the dialog says so, the cart is unchanged, and the user stays on Orders.
- If the menu cannot be loaded, comes back empty, or the cart cannot be saved, the user sees an error. The cart is unchanged (all lines are written or none are) and the user can try again.

Out of scope:
- Replacing the cart instead of adding to it.
- Keeping the historical price.
- Reordering from anywhere but the Orders screen (no order detail screen, no checkout confirmation shortcut).
- Choosing a subset of items before reordering.
- Copying the order's pickup time or comment into checkout.
- Guest reorder. Order history needs sign-in today, so only signed-in users see the button.

## Capabilities

### New Capabilities
- `order`: placing orders and working with order history. This change adds its first requirements: reordering a past order into the cart.

### Modified Capabilities
None. No main specs exist yet.

## Impact

- `:features:order:domain`: new reorder use case and result model. Gains dependencies on `:features:menu:domain` (availability and current prices) and `:features:cart:domain` (adding lines). Both are domain → domain, the graph stays acyclic.
- `:features:order:ui-past-orders`: button on `OrderCard`, new intent, one-shot events and the skipped-items dialog in `OrdersViewModel` / `OrdersScreen`.
- `:app`: `OrdersScreen` gets an `onNavigateToCart` callback wired to the cart route.
- Data layers of menu, cart, order and auth: data sources the acceptance tests fake get an interface plus a `testFixtures` fake. No behavior change.
- No Firestore schema or Room migration change.
