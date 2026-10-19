package com.example.monatechickensteak

import com.example.monatechickensteak.data.SeedData
import com.example.monatechickensteak.util.PasswordHasher
import com.example.monatechickensteak.util.ReviewValidator
import com.example.monatechickensteak.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Checks the starting data that is written into the app database.
 * This is the automated proof for the requirement "at least 10 records per table".
 */
class SeedDataTest {

    private val tableSizes: Map<String, Int> = mapOf(
        "users" to SeedData.users.size,
        "categories" to SeedData.categories.size,
        "menu_items" to SeedData.menuItems.size,
        "orders" to SeedData.orders.size,
        "order_items" to SeedData.orders.sumOf { it.lines.size },
        "reviews" to SeedData.reviews.size,
        "rewards" to SeedData.rewards.size,
        "redemptions" to SeedData.redemptions.size,
        "promotions" to SeedData.promotions.size,
        "faqs" to SeedData.faqs.size,
        "support_messages" to SeedData.supportMessages.size
    )

    @Test fun everyTableHasAtLeastTenRecords() {
        tableSizes.forEach { (table, size) ->
            assertTrue("$table has only $size records", size >= 10)
        }
    }

    @Test fun idsAreUniqueInEveryTable() {
        fun unique(name: String, ids: List<Int>) = assertEquals("$name ids repeat", ids.size, ids.toSet().size)
        unique("users", SeedData.users.map { it.id })
        unique("categories", SeedData.categories.map { it.id })
        unique("menu_items", SeedData.menuItems.map { it.id })
        unique("orders", SeedData.orders.map { it.id })
        unique("reviews", SeedData.reviews.map { it.id })
        unique("rewards", SeedData.rewards.map { it.id })
        unique("redemptions", SeedData.redemptions.map { it.id })
        unique("promotions", SeedData.promotions.map { it.id })
        unique("faqs", SeedData.faqs.map { it.id })
        unique("support_messages", SeedData.supportMessages.map { it.id })
    }

    @Test fun foreignKeysPointAtRowsThatExist() {
        val userIds = SeedData.users.map { it.id }.toSet()
        val categoryIds = SeedData.categories.map { it.id }.toSet()
        val menuIds = SeedData.menuItems.map { it.id }.toSet()
        val rewardIds = SeedData.rewards.map { it.id }.toSet()

        assertTrue(SeedData.menuItems.all { it.categoryId in categoryIds })
        assertTrue(SeedData.orders.all { it.userId in userIds })
        assertTrue(SeedData.orders.all { o -> o.lines.all { (itemId, _) -> itemId in menuIds } })
        assertTrue(SeedData.reviews.all { it.userId in userIds })
        assertTrue(SeedData.redemptions.all { it.userId in userIds && it.rewardId in rewardIds })
        assertTrue(SeedData.supportMessages.all { it.userId in userIds })
    }

    @Test fun everyCategoryHasAtLeastOneMenuItem() {
        val used = SeedData.menuItems.map { it.categoryId }.toSet()
        assertTrue(SeedData.categories.all { it.id in used })
    }

    @Test fun menuPricesAreSensible() {
        assertTrue(SeedData.menuItems.all { it.price > 0 })
    }

    @Test fun orderLinesHaveAPositiveQuantity() {
        assertTrue(SeedData.orders.all { o -> o.lines.isNotEmpty() && o.lines.all { it.second > 0 } })
    }

    @Test fun orderTotalsIncludeTheDeliveryFee() {
        val first = SeedData.orders.first { it.id == 1001 } // 1 x Rump 300g (R180) + 2 x Coca Cola (R20) + R25
        assertEquals(245.0, SeedData.orderTotal(first), 0.001)
    }

    @Test fun emailsAreValidAndUnique() {
        assertTrue(SeedData.users.all { Validators.emailError(it.email) == null })
        val lower = SeedData.users.map { it.email.lowercase() }
        assertEquals(lower.size, lower.toSet().size)
    }

    @Test fun demoAccountExists() {
        assertTrue(SeedData.users.any { it.email == "demo@monate.co.za" })
    }

    @Test fun everySeededPasswordIsHashedAndMatchesTheDemoPassword() {
        SeedData.users.forEach { user ->
            assertTrue("${user.email} is not hashed", user.passwordHash.startsWith("pbkdf2_sha256$"))
            assertTrue("${user.email} hash does not verify", PasswordHasher.verify(SeedData.DEMO_PASSWORD, user.passwordHash))
        }
    }

    @Test fun seededSaltsAreAllDifferent() {
        val salts = SeedData.users.map { it.passwordHash.split("$")[2] }
        assertEquals(salts.size, salts.toSet().size)
    }

    @Test fun reviewsFollowTheAppsOwnRules() {
        SeedData.reviews.forEach { r ->
            assertNull(ReviewValidator.ratingError(r.rating))
            assertTrue(r.rating in 1..5)
            assertNull(ReviewValidator.commentError(r.comment))
        }
    }

    @Test fun orderStatusesIncludeOrdersStillInProgress() {
        // so order tracking has something to show in a demo
        assertTrue(SeedData.orders.any { it.status.name != "DELIVERED" })
    }
}
