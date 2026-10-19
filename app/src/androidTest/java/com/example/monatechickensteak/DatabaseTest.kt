package com.example.monatechickensteak

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.monatechickensteak.data.db.MonateDbHelper
import com.example.monatechickensteak.data.db.SqliteUserStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Runs on a phone or emulator against the real SQLite database.
 * Run with:  ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var helper: MonateDbHelper

    @Before
    fun setUp() {
        helper = MonateDbHelper.get(ApplicationProvider.getApplicationContext<Context>())
    }

    @Test
    fun everyTableHasAtLeastTenRecords() {
        val db = helper.readableDatabase
        listOf(
            "users", "categories", "menu_items", "orders", "order_items", "reviews",
            "rewards", "redemptions", "promotions", "faqs", "support_messages"
        ).forEach { table ->
            val count = DatabaseUtils.queryNumEntries(db, table)
            assertTrue("$table has only $count records", count >= 10)
        }
    }

    @Test
    fun foreignKeysAreEnforced() {
        val values = ContentValues().apply {
            put("user_id", 987654) // no such user
            put("total", 10.0)
            put("collection_time", "As soon as possible")
            put("payment_method", "Card Payment")
            put("status", "RECEIVED")
            put("placed_at", "01 Jan 2026, 10:00")
        }
        var rejected = false
        try {
            helper.writableDatabase.insertOrThrow("orders", null, values)
        } catch (e: SQLiteConstraintException) {
            rejected = true
        }
        assertTrue("an order for a missing user should be rejected", rejected)
    }

    @Test
    fun demoUserCanBeFoundCaseInsensitivelyAndPasswordIsHashed() {
        val store = SqliteUserStore(helper)
        val user = store.findByEmail("DEMO@monate.co.za")
        assertNotNull(user)
        assertTrue(user!!.passwordHash!!.startsWith("pbkdf2_sha256$"))
    }

    @Test
    fun newUserCanBeInsertedFoundAndDeleted() {
        val store = SqliteUserStore(helper)
        val email = "instrumented.${System.currentTimeMillis()}@monate.co.za"
        val created = store.insert("Instrumented", email, "hash", "password")
        assertEquals(email, store.findById(created.id)!!.email)
        store.delete(created.id)
        assertNull(store.findById(created.id))
    }
}
