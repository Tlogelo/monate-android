package com.example.monatechickensteak.data.db

import android.content.ContentValues
import android.database.Cursor
import com.example.monatechickensteak.data.CatalogStore
import com.example.monatechickensteak.data.OrderStore
import com.example.monatechickensteak.data.ReviewStore
import com.example.monatechickensteak.data.SupportStore
import com.example.monatechickensteak.data.UserRecord
import com.example.monatechickensteak.data.UserStore
import com.example.monatechickensteak.model.CartItem
import com.example.monatechickensteak.model.Faq
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.Order
import com.example.monatechickensteak.model.OrderStatus
import com.example.monatechickensteak.model.Promotion
import com.example.monatechickensteak.model.Review
import com.example.monatechickensteak.model.Reward

/** Every query uses "?" placeholders, never text pasted into the SQL, so user input cannot inject SQL. */

private fun Cursor.str(column: Int): String = getString(column) ?: ""

class SqliteUserStore(private val helper: MonateDbHelper) : UserStore {

    private val columns = "id, name, email, password_hash, auth_provider, loyalty_points"

    private fun read(c: Cursor) = UserRecord(
        id = c.getInt(0),
        name = c.str(1),
        email = c.str(2),
        passwordHash = if (c.isNull(3)) null else c.getString(3),
        provider = c.str(4),
        loyaltyPoints = c.getInt(5)
    )

    override fun findByEmail(email: String): UserRecord? =
        helper.readableDatabase.rawQuery(
            "SELECT $columns FROM users WHERE email = ? COLLATE NOCASE", arrayOf(email)
        ).use { if (it.moveToFirst()) read(it) else null }

    override fun findById(id: Int): UserRecord? =
        helper.readableDatabase.rawQuery(
            "SELECT $columns FROM users WHERE id = ?", arrayOf(id.toString())
        ).use { if (it.moveToFirst()) read(it) else null }

    override fun insert(name: String, email: String, passwordHash: String?, provider: String): UserRecord {
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            if (passwordHash == null) putNull("password_hash") else put("password_hash", passwordHash)
            put("auth_provider", provider)
            put("loyalty_points", 0)
            put("created_at", java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(java.util.Date()))
        }
        val id = helper.writableDatabase.insertOrThrow("users", null, values)
        return UserRecord(id.toInt(), name, email, passwordHash, provider, 0)
    }

    override fun updateName(id: Int, name: String) {
        val values = ContentValues().apply { put("name", name) }
        helper.writableDatabase.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    override fun updatePasswordHash(id: Int, passwordHash: String) {
        val values = ContentValues().apply { put("password_hash", passwordHash) }
        helper.writableDatabase.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    override fun setPoints(id: Int, points: Int) {
        val values = ContentValues().apply { put("loyalty_points", points) }
        helper.writableDatabase.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    override fun addRedemption(userId: Int, rewardId: Int, code: String) {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("reward_id", rewardId)
            put("code", code)
            put("redeemed_at", java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(java.util.Date()))
        }
        helper.writableDatabase.insertOrThrow("redemptions", null, values)
    }

    override fun delete(id: Int) {
        helper.writableDatabase.delete("users", "id = ?", arrayOf(id.toString()))
    }
}

class SqliteOrderStore(private val helper: MonateDbHelper) : OrderStore {

    override fun insert(
        userId: Int,
        items: List<CartItem>,
        total: Double,
        collectionTime: String,
        paymentMethod: String,
        placedAt: String
    ): Order {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val orderValues = ContentValues().apply {
                put("user_id", userId)
                put("total", total)
                put("collection_time", collectionTime)
                put("payment_method", paymentMethod)
                put("status", OrderStatus.RECEIVED.name)
                put("placed_at", placedAt)
            }
            val orderId = db.insertOrThrow("orders", null, orderValues).toInt()
            items.forEach { line ->
                val lineValues = ContentValues().apply {
                    put("order_id", orderId)
                    put("menu_item_id", line.item.id)
                    put("quantity", line.quantity)
                    put("unit_price", line.item.price)
                }
                db.insertOrThrow("order_items", null, lineValues)
            }
            db.setTransactionSuccessful()
            return Order(orderId, items, total, collectionTime, paymentMethod, OrderStatus.RECEIVED, placedAt)
        } finally {
            db.endTransaction()
        }
    }

    override fun forUser(userId: Int): List<Order> =
        query("WHERE user_id = ?", arrayOf(userId.toString()))

    override fun find(orderId: Int): Order? =
        query("WHERE id = ?", arrayOf(orderId.toString())).firstOrNull()

    override fun updateStatus(orderId: Int, status: OrderStatus) {
        val values = ContentValues().apply { put("status", status.name) }
        helper.writableDatabase.update("orders", values, "id = ?", arrayOf(orderId.toString()))
    }

    /** Newest order first. [where] is one of the fixed clauses above, never user text. */
    private fun query(where: String, args: Array<String>): List<Order> {
        val db = helper.readableDatabase
        val headers = db.rawQuery(
            "SELECT id, total, collection_time, payment_method, status, placed_at " +
                    "FROM orders $where ORDER BY id DESC",
            args
        ).use { c ->
            val rows = mutableListOf<Order>()
            while (c.moveToNext()) {
                rows.add(
                    Order(
                        id = c.getInt(0),
                        items = emptyList(),
                        total = c.getDouble(1),
                        collectionTime = c.str(2),
                        paymentMethod = c.str(3),
                        status = runCatching { OrderStatus.valueOf(c.str(4)) }.getOrDefault(OrderStatus.RECEIVED),
                        placedAt = c.str(5)
                    )
                )
            }
            rows
        }
        return headers.map { order -> order.copy(items = linesFor(order.id)) }
    }

    private fun linesFor(orderId: Int): List<CartItem> =
        helper.readableDatabase.rawQuery(
            "SELECT m.id, m.name, c.name, m.description, oi.unit_price, m.emoji, oi.quantity " +
                    "FROM order_items oi " +
                    "JOIN menu_items m ON m.id = oi.menu_item_id " +
                    "JOIN categories c ON c.id = m.category_id " +
                    "WHERE oi.order_id = ? ORDER BY oi.id",
            arrayOf(orderId.toString())
        ).use { c ->
            val lines = mutableListOf<CartItem>()
            while (c.moveToNext()) {
                // unit_price is the price paid at the time, even if the menu price changes later
                val food = FoodItem(c.getInt(0), c.str(1), c.str(2), c.str(3), c.getDouble(4), c.str(5))
                lines.add(CartItem(food, c.getInt(6)))
            }
            lines
        }
}

class SqliteReviewStore(private val helper: MonateDbHelper) : ReviewStore {

    override fun all(): List<Review> =
        helper.readableDatabase.rawQuery(
            "SELECT id, item_name, rating, comment, author, created_at FROM reviews ORDER BY id DESC",
            null
        ).use { c ->
            val list = mutableListOf<Review>()
            while (c.moveToNext()) {
                list.add(Review(c.getInt(0), c.str(1), c.getInt(2), c.str(3), c.str(4), c.str(5)))
            }
            list
        }

    override fun insert(
        userId: Int,
        itemName: String,
        rating: Int,
        comment: String,
        author: String,
        date: String
    ): Review {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("item_name", itemName)
            put("rating", rating)
            put("comment", comment)
            put("author", author)
            put("created_at", date)
        }
        val id = helper.writableDatabase.insertOrThrow("reviews", null, values).toInt()
        return Review(id, itemName, rating, comment, author, date)
    }
}

class SqliteCatalogStore(private val helper: MonateDbHelper) : CatalogStore {

    override fun categories(): List<String> =
        helper.readableDatabase.rawQuery("SELECT name FROM categories ORDER BY name", null).use { c ->
            val list = mutableListOf<String>()
            while (c.moveToNext()) list.add(c.str(0))
            list
        }

    override fun menu(): List<FoodItem> =
        helper.readableDatabase.rawQuery(
            "SELECT m.id, m.name, c.name, m.description, m.price, m.emoji " +
                    "FROM menu_items m JOIN categories c ON c.id = m.category_id ORDER BY m.id",
            null
        ).use { c ->
            val list = mutableListOf<FoodItem>()
            while (c.moveToNext()) {
                list.add(FoodItem(c.getInt(0), c.str(1), c.str(2), c.str(3), c.getDouble(4), c.str(5)))
            }
            list
        }

    override fun rewards(): List<Reward> =
        helper.readableDatabase.rawQuery(
            "SELECT id, name, cost, emoji FROM rewards ORDER BY cost", null
        ).use { c ->
            val list = mutableListOf<Reward>()
            while (c.moveToNext()) list.add(Reward(c.getInt(0), c.str(1), c.getInt(2), c.str(3)))
            list
        }

    override fun promotions(): List<Promotion> =
        helper.readableDatabase.rawQuery(
            "SELECT id, title, description, emoji FROM promotions ORDER BY id", null
        ).use { c ->
            val list = mutableListOf<Promotion>()
            while (c.moveToNext()) list.add(Promotion(c.getInt(0), c.str(1), c.str(2), c.str(3)))
            list
        }

    override fun faqs(): List<Faq> =
        helper.readableDatabase.rawQuery(
            "SELECT id, question, answer FROM faqs ORDER BY id", null
        ).use { c ->
            val list = mutableListOf<Faq>()
            while (c.moveToNext()) list.add(Faq(c.getInt(0), c.str(1), c.str(2)))
            list
        }
}

class SqliteSupportStore(private val helper: MonateDbHelper) : SupportStore {

    override fun insert(userId: Int, message: String, createdAt: String) {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("message", message)
            put("created_at", createdAt)
        }
        helper.writableDatabase.insertOrThrow("support_messages", null, values)
    }

    override fun countForUser(userId: Int): Int =
        helper.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM support_messages WHERE user_id = ?", arrayOf(userId.toString())
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
}
