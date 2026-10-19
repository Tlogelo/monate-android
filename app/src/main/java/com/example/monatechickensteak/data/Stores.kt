package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.Faq
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.Order
import com.example.monatechickensteak.model.OrderStatus
import com.example.monatechickensteak.model.Promotion
import com.example.monatechickensteak.model.Review
import com.example.monatechickensteak.model.Reward
import com.example.monatechickensteak.model.User

/**
 * The stores are the only code that talks to the database. The repositories
 * (AuthRepository, OrderRepository, ...) hold the rules and call a store, so the
 * rules can be unit tested with a simple in-memory store and no phone.
 * The real implementations live in data/db/SqliteStores.kt.
 */

/** A row of the users table. passwordHash is null for Google single sign-on accounts. */
data class UserRecord(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String?,
    val provider: String,
    val loyaltyPoints: Int
) {
    fun toUser() = User(id, name, email, loyaltyPoints, provider)
}

interface UserStore {
    fun findByEmail(email: String): UserRecord?
    fun findById(id: Int): UserRecord?
    fun insert(name: String, email: String, passwordHash: String?, provider: String): UserRecord
    fun updateName(id: Int, name: String)
    fun updatePasswordHash(id: Int, passwordHash: String)
    fun setPoints(id: Int, points: Int)
    fun addRedemption(userId: Int, rewardId: Int, code: String)
    fun delete(id: Int)
}

interface OrderStore {
    fun insert(
        userId: Int,
        items: List<CartItem>,
        total: Double,
        collectionTime: String,
        paymentMethod: String,
        placedAt: String
    ): Order

    fun forUser(userId: Int): List<Order>
    fun find(orderId: Int): Order?
    fun updateStatus(orderId: Int, status: OrderStatus)
}

interface ReviewStore {
    fun all(): List<Review>
    fun insert(userId: Int, itemName: String, rating: Int, comment: String, author: String, date: String): Review
}

interface CatalogStore {
    fun categories(): List<String>
    fun menu(): List<FoodItem>
    fun rewards(): List<Reward>
    fun promotions(): List<Promotion>
    fun faqs(): List<Faq>
}

interface SupportStore {
    fun insert(userId: Int, message: String, createdAt: String)
    fun countForUser(userId: Int): Int
}
