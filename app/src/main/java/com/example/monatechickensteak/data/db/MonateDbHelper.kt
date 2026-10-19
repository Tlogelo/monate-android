package com.example.monatechickensteak.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.monatechickensteak.data.SeedData

/**
 * The app's SQLite database (monate.db). It is created and filled with the starting data
 * (SeedData) the first time the app runs. Tables and how they relate are drawn in docs/DATABASE.md.
 */
class MonateDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Make SQLite enforce the FOREIGN KEY rules (it is off by default)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        SCHEMA.forEach { db.execSQL(it) }
        seed(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Early development: rebuild from scratch. Replace with real migrations before release.
        TABLES_DROP_ORDER.forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    private fun seed(db: SQLiteDatabase) {
        SeedData.users.forEach { u ->
            insert(db, "users", ContentValues().apply {
                put("id", u.id)
                put("name", u.name)
                put("email", u.email)
                put("password_hash", u.passwordHash)
                put("auth_provider", "password")
                put("loyalty_points", u.points)
                put("created_at", u.createdAt)
            })
        }
        SeedData.categories.forEach { c ->
            insert(db, "categories", ContentValues().apply {
                put("id", c.id)
                put("name", c.name)
            })
        }
        SeedData.menuItems.forEach { m ->
            insert(db, "menu_items", ContentValues().apply {
                put("id", m.id)
                put("category_id", m.categoryId)
                put("name", m.name)
                put("description", m.description)
                put("price", m.price)
                put("emoji", m.emoji)
            })
        }
        SeedData.rewards.forEach { r ->
            insert(db, "rewards", ContentValues().apply {
                put("id", r.id)
                put("name", r.name)
                put("cost", r.cost)
                put("emoji", r.emoji)
            })
        }
        SeedData.orders.forEach { o ->
            insert(db, "orders", ContentValues().apply {
                put("id", o.id)
                put("user_id", o.userId)
                put("total", SeedData.orderTotal(o))
                put("collection_time", o.collectionTime)
                put("payment_method", o.paymentMethod)
                put("status", o.status.name)
                put("placed_at", o.placedAt)
            })
            o.lines.forEach { (itemId, quantity) ->
                val price = SeedData.menuItems.first { it.id == itemId }.price
                insert(db, "order_items", ContentValues().apply {
                    put("order_id", o.id)
                    put("menu_item_id", itemId)
                    put("quantity", quantity)
                    put("unit_price", price)
                })
            }
        }
        SeedData.reviews.forEach { r ->
            insert(db, "reviews", ContentValues().apply {
                put("id", r.id)
                put("user_id", r.userId)
                put("item_name", r.itemName)
                put("rating", r.rating)
                put("comment", r.comment)
                put("author", r.author)
                put("created_at", r.createdAt)
            })
        }
        SeedData.redemptions.forEach { r ->
            insert(db, "redemptions", ContentValues().apply {
                put("id", r.id)
                put("user_id", r.userId)
                put("reward_id", r.rewardId)
                put("code", r.code)
                put("redeemed_at", r.redeemedAt)
            })
        }
        SeedData.promotions.forEach { p ->
            insert(db, "promotions", ContentValues().apply {
                put("id", p.id)
                put("title", p.title)
                put("description", p.description)
                put("emoji", p.emoji)
            })
        }
        SeedData.faqs.forEach { f ->
            insert(db, "faqs", ContentValues().apply {
                put("id", f.id)
                put("question", f.question)
                put("answer", f.answer)
            })
        }
        SeedData.supportMessages.forEach { s ->
            insert(db, "support_messages", ContentValues().apply {
                put("id", s.id)
                put("user_id", s.userId)
                put("message", s.message)
                put("created_at", s.createdAt)
            })
        }
    }

    private fun insert(db: SQLiteDatabase, table: String, values: ContentValues) {
        db.insertOrThrow(table, null, values)
    }

    companion object {
        const val DB_NAME = "monate.db"
        const val DB_VERSION = 1

        @Volatile
        private var instance: MonateDbHelper? = null

        fun get(context: Context): MonateDbHelper =
            instance ?: synchronized(this) {
                instance ?: MonateDbHelper(context).also { instance = it }
            }

        /** Child tables first, so foreign keys never block a drop. */
        private val TABLES_DROP_ORDER = listOf(
            "support_messages", "faqs", "promotions", "redemptions", "rewards",
            "reviews", "order_items", "orders", "menu_items", "categories", "users"
        )

        private val SCHEMA = listOf(
            """CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                password_hash TEXT,
                auth_provider TEXT NOT NULL DEFAULT 'password',
                loyalty_points INTEGER NOT NULL DEFAULT 0 CHECK (loyalty_points >= 0),
                created_at TEXT NOT NULL
            )""",
            """CREATE TABLE categories (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE
            )""",
            """CREATE TABLE menu_items (
                id INTEGER PRIMARY KEY,
                category_id INTEGER NOT NULL REFERENCES categories(id),
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                price REAL NOT NULL CHECK (price > 0),
                emoji TEXT NOT NULL
            )""",
            """CREATE TABLE orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                total REAL NOT NULL,
                collection_time TEXT NOT NULL,
                payment_method TEXT NOT NULL,
                status TEXT NOT NULL,
                placed_at TEXT NOT NULL
            )""",
            """CREATE TABLE order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                menu_item_id INTEGER NOT NULL REFERENCES menu_items(id),
                quantity INTEGER NOT NULL CHECK (quantity > 0),
                unit_price REAL NOT NULL
            )""",
            """CREATE TABLE reviews (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
                item_name TEXT NOT NULL,
                rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
                comment TEXT NOT NULL,
                author TEXT NOT NULL,
                created_at TEXT NOT NULL
            )""",
            """CREATE TABLE rewards (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                cost INTEGER NOT NULL CHECK (cost > 0),
                emoji TEXT NOT NULL
            )""",
            """CREATE TABLE redemptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                reward_id INTEGER NOT NULL REFERENCES rewards(id),
                code TEXT NOT NULL,
                redeemed_at TEXT NOT NULL
            )""",
            """CREATE TABLE promotions (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                emoji TEXT NOT NULL
            )""",
            """CREATE TABLE faqs (
                id INTEGER PRIMARY KEY,
                question TEXT NOT NULL,
                answer TEXT NOT NULL
            )""",
            """CREATE TABLE support_messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
                message TEXT NOT NULL,
                created_at TEXT NOT NULL
            )"""
        )
    }
}
