package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.Order
import com.example.monatechickensteak.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** The step that follows this one, or null when the order is already delivered. */
fun OrderStatus.next(): OrderStatus? = when (this) {
    OrderStatus.RECEIVED -> OrderStatus.PREPARING
    OrderStatus.PREPARING -> OrderStatus.READY
    OrderStatus.READY -> OrderStatus.DELIVERED
    OrderStatus.DELIVERED -> null
}

/** Orders belong to the customer who placed them. Each customer only sees their own. */
object OrderRepository {

    lateinit var store: OrderStore

    suspend fun ordersFor(userId: Int): List<Order> = withContext(Dispatchers.IO) {
        store.forUser(userId)
    }

    suspend fun placeOrder(
        userId: Int,
        items: List<CartItem>,
        total: Double,
        collectionTime: String,
        paymentMethod: String
    ): Order = withContext(Dispatchers.IO) {
        val placedAt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        store.insert(userId, items, total, collectionTime, paymentMethod, placedAt)
    }

    /**
     * Moves one order to its next status and returns it. Orders already delivered are
     * left alone. Returns null when the order does not exist.
     */
    suspend fun advanceStatus(orderId: Int): Order? = withContext(Dispatchers.IO) {
        val order = store.find(orderId) ?: return@withContext null
        val next = order.status.next() ?: return@withContext order
        store.updateStatus(orderId, next)
        order.copy(status = next)
    }
}
