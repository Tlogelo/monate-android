package com.example.monatechickensteak

import com.example.monatechickensteak.data.OrderRepository
import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.OrderStatus
import com.example.monatechickensteak.util.CheckoutOptions
import com.example.monatechickensteak.util.CheckoutValidator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CheckoutValidatorTest {

    @Test fun emptyCartIsRejected() =
        assertNotNull(CheckoutValidator.error(cartIsEmpty = true, paymentMethod = "Cash on Collection"))

    @Test fun missingPaymentIsRejected() =
        assertNotNull(CheckoutValidator.error(cartIsEmpty = false, paymentMethod = null))

    @Test fun validCheckoutPasses() =
        assertNull(CheckoutValidator.error(cartIsEmpty = false, paymentMethod = "Card Payment"))
}

class CheckoutOptionsTest {

    @Test fun savedTimeIsUsedWhenValid() =
        assertEquals("In 1 hour", CheckoutOptions.validTime("In 1 hour"))

    @Test fun unknownOrMissingTimeFallsBackToTheFirstOption() {
        assertEquals("As soon as possible", CheckoutOptions.validTime(null))
        assertEquals("As soon as possible", CheckoutOptions.validTime("Next week"))
    }

    @Test fun savedPaymentIsUsedWhenValid() =
        assertEquals("Card Payment", CheckoutOptions.validPaymentOrNull("Card Payment"))

    @Test fun unknownOrMissingPaymentGivesNothingPreSelected() {
        assertNull(CheckoutOptions.validPaymentOrNull(null))
        assertNull(CheckoutOptions.validPaymentOrNull("Bitcoin"))
    }
}

class OrderRepositoryTest {

    private val chicken = FoodItem(1, "Quarter Chicken", "Chicken", "desc", 75.0, "🍗")
    private val items = listOf(CartItem(chicken, 2))

    @Before
    fun setUp() {
        OrderRepository.store = FakeOrderStore()
    }

    @Test fun newOrderStartsAsReceived() = runTest {
        val order = OrderRepository.placeOrder(1, items, 175.0, "As soon as possible", "Cash on Collection")
        assertEquals(OrderStatus.RECEIVED, order.status)
    }

    @Test fun orderIdsIncreaseAndNewestIsFirst() = runTest {
        OrderRepository.placeOrder(1, items, 175.0, "In 15 minutes", "Cash on Collection")
        val second = OrderRepository.placeOrder(1, items, 175.0, "In 30 minutes", "Card Payment")
        val list = OrderRepository.ordersFor(1)
        assertEquals(2, list.size)
        assertEquals(second.id, list.first().id)
    }

    @Test fun orderKeepsTheItemsAndTotal() = runTest {
        val order = OrderRepository.placeOrder(1, items, 175.0, "In 1 hour", "Card Payment")
        assertEquals(175.0, order.total, 0.001)
        assertEquals(2, order.items.first().quantity)
    }

    @Test fun customersOnlySeeTheirOwnOrders() = runTest {
        OrderRepository.placeOrder(1, items, 175.0, "In 1 hour", "Card Payment")
        OrderRepository.placeOrder(2, items, 175.0, "In 1 hour", "Card Payment")
        assertEquals(1, OrderRepository.ordersFor(1).size)
        assertEquals(1, OrderRepository.ordersFor(2).size)
        assertEquals(0, OrderRepository.ordersFor(3).size)
    }
}
