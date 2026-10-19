package com.example.monatechickensteak

import com.example.monatechickensteak.data.OrderStore
import com.example.monatechickensteak.data.ReviewStore
import com.example.monatechickensteak.data.SupportStore
import com.example.monatechickensteak.data.UserRecord
import com.example.monatechickensteak.data.UserStore
import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.Order
import com.example.monatechickensteak.model.OrderStatus
import com.example.monatechickensteak.model.Review

/** In-memory stores so the business rules can be tested on a laptop with no phone or database. */

class FakeUserStore : UserStore {
    val users = mutableListOf<UserRecord>()
    val redemptions = mutableListOf<Triple<Int, Int, String>>()
    private var nextId = 1

    override fun findByEmail(email: String) = users.firstOrNull { it.email.equals(email, ignoreCase = true) }

    override fun findById(id: Int) = users.firstOrNull { it.id == id }

    override fun insert(name: String, email: String, passwordHash: String?, provider: String): UserRecord {
        val record = UserRecord(nextId++, name, email, passwordHash, provider, 0)
        users.add(record)
        return record
    }

    private fun change(id: Int, edit: (UserRecord) -> UserRecord) {
        val i = users.indexOfFirst { it.id == id }
        if (i >= 0) users[i] = edit(users[i])
    }

    override fun updateName(id: Int, name: String) = change(id) { it.copy(name = name) }

    override fun updatePasswordHash(id: Int, passwordHash: String) =
        change(id) { it.copy(passwordHash = passwordHash) }

    override fun setPoints(id: Int, points: Int) = change(id) { it.copy(loyaltyPoints = points) }

    override fun addRedemption(userId: Int, rewardId: Int, code: String) {
        redemptions.add(Triple(userId, rewardId, code))
    }

    override fun delete(id: Int) {
        users.removeAll { it.id == id }
    }
}

class FakeOrderStore : OrderStore {
    private val orders = mutableListOf<Pair<Int, Order>>() // owner id + order
    private var nextId = 1001

    override fun insert(
        userId: Int,
        items: List<CartItem>,
        total: Double,
        collectionTime: String,
        paymentMethod: String,
        placedAt: String
    ): Order {
        val order = Order(nextId++, items, total, collectionTime, paymentMethod, OrderStatus.RECEIVED, placedAt)
        orders.add(userId to order)
        return order
    }

    override fun forUser(userId: Int) =
        orders.filter { it.first == userId }.map { it.second }.sortedByDescending { it.id }

    override fun find(orderId: Int) = orders.firstOrNull { it.second.id == orderId }?.second

    override fun updateStatus(orderId: Int, status: OrderStatus) {
        val i = orders.indexOfFirst { it.second.id == orderId }
        if (i >= 0) orders[i] = orders[i].first to orders[i].second.copy(status = status)
    }
}

class FakeReviewStore : ReviewStore {
    private val reviews = mutableListOf<Review>()
    private var nextId = 1

    override fun all() = reviews.sortedByDescending { it.id }

    override fun insert(
        userId: Int,
        itemName: String,
        rating: Int,
        comment: String,
        author: String,
        date: String
    ): Review {
        val review = Review(nextId++, itemName, rating, comment, author, date)
        reviews.add(review)
        return review
    }
}

class FakeSupportStore : SupportStore {
    val messages = mutableListOf<Pair<Int, String>>()

    override fun insert(userId: Int, message: String, createdAt: String) {
        messages.add(userId to message)
    }

    override fun countForUser(userId: Int) = messages.count { it.first == userId }
}
