package com.example.monatechickensteak.model

data class FoodItem(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val emoji: String
)

data class CartItem(
    val item: FoodItem,
    val quantity: Int
) {
    val lineTotal: Double get() = item.price * quantity
}

enum class OrderStatus(val label: String) {
    RECEIVED("Received"),
    PREPARING("Preparing"),
    READY("Ready"),
    DELIVERED("Delivered")
}

data class Review(
    val id: Int,
    val itemName: String,
    val rating: Int,
    val comment: String,
    val author: String,
    val date: String
)

data class Order(
    val id: Int,
    val items: List<CartItem>,
    val total: Double,
    val collectionTime: String,
    val paymentMethod: String,
    val status: OrderStatus,
    val placedAt: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val loyaltyPoints: Int = 0,
    /** "password" for accounts created in the app, "google" for Google single sign-on. */
    val provider: String = "password"
)

data class Reward(
    val id: Int,
    val name: String,
    val cost: Int,
    val emoji: String
)

data class Promotion(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String
)

data class Faq(
    val id: Int,
    val question: String,
    val answer: String
)
