package com.example.monatechickensteak

import com.example.monatechickensteak.data.OrderRepository
import com.example.monatechickensteak.data.next
import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.OrderStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class OrderTrackingTest {

    private val chips = FoodItem(10, "Chips", "Sides", "desc", 25.0, "🍟")
    private val items = listOf(CartItem(chips, 1))

    @Before
    fun setUp() {
        OrderRepository.store = FakeOrderStore()
    }

    @Test fun statusFollowsTheKitchenFlow() {
        assertEquals(OrderStatus.PREPARING, OrderStatus.RECEIVED.next())
        assertEquals(OrderStatus.READY, OrderStatus.PREPARING.next())
        assertEquals(OrderStatus.DELIVERED, OrderStatus.READY.next())
    }

    @Test fun deliveredHasNoNextStatus() = assertNull(OrderStatus.DELIVERED.next())

    @Test fun advanceStatusMovesTheOrderForward() = runTest {
        val order = OrderRepository.placeOrder(1, items, 50.0, "As soon as possible", "Cash on Collection")
        val updated = OrderRepository.advanceStatus(order.id)
        assertEquals(OrderStatus.PREPARING, updated!!.status)
        assertEquals(OrderStatus.PREPARING, OrderRepository.ordersFor(1).first().status)
    }

    @Test fun advancingPastDeliveredChangesNothing() = runTest {
        val order = OrderRepository.placeOrder(1, items, 50.0, "In 15 minutes", "Card Payment")
        repeat(6) { OrderRepository.advanceStatus(order.id) }
        assertEquals(OrderStatus.DELIVERED, OrderRepository.ordersFor(1).first().status)
    }

    @Test fun advancingAnUnknownOrderReturnsNull() = runTest {
        assertNull(OrderRepository.advanceStatus(424242))
    }
}
