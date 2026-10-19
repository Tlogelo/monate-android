package com.example.monatechickensteak

import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import com.example.monatechickensteak.data.ReviewRepository
import com.example.monatechickensteak.model.Review
import com.example.monatechickensteak.model.Reward
import com.example.monatechickensteak.util.LoyaltyRules
import com.example.monatechickensteak.util.ReviewValidator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoyaltyRulesTest {

    @Test fun onePointForEveryTenRand() = assertEquals(12, LoyaltyRules.pointsForOrder(125.0))

    @Test fun tiersChangeAtTheThresholds() {
        assertEquals(LoyaltyRules.Tier.BRONZE, LoyaltyRules.tierFor(99))
        assertEquals(LoyaltyRules.Tier.SILVER, LoyaltyRules.tierFor(100))
        assertEquals(LoyaltyRules.Tier.GOLD, LoyaltyRules.tierFor(300))
    }

    @Test fun bronzeHasSilverAsNextTier() =
        assertEquals(LoyaltyRules.Tier.SILVER, LoyaltyRules.nextTier(10))

    @Test fun goldHasNoNextTier() = assertNull(LoyaltyRules.nextTier(500))
}

class ReviewValidatorTest {

    @Test fun ratingIsRequired() = assertNotNull(ReviewValidator.ratingError(0))

    @Test fun shortCommentIsRejected() = assertNotNull(ReviewValidator.commentError("ok"))

    @Test fun tooLongCommentIsRejected() = assertNotNull(ReviewValidator.commentError("x".repeat(301)))

    @Test fun goodReviewPasses() {
        assertNull(ReviewValidator.itemError("Chips"))
        assertNull(ReviewValidator.ratingError(4))
        assertNull(ReviewValidator.commentError("Really tasty and hot."))
    }
}

class ReviewRepositoryTest {

    @Before
    fun setUp() {
        ReviewRepository.store = FakeReviewStore()
    }

    @Test fun newestReviewComesFirst() = runTest {
        ReviewRepository.add(1, "Chips", 4, "Nice and crispy", "Thabi")
        val second = ReviewRepository.add(1, "Rump 300g", 5, "Perfectly cooked", "Thabi")
        assertEquals(second.id, ReviewRepository.all().first().id)
    }

    @Test fun averageRatingIsCalculated() {
        val reviews = listOf(
            Review(1, "Chips", 5, "Great chips here", "A", "01 Oct 2026"),
            Review(2, "Chips", 4, "Pretty good chips", "B", "02 Oct 2026")
        )
        assertEquals(4.5, ReviewRepository.averageRating(reviews), 0.001)
    }

    @Test fun averageOfNoReviewsIsZero() = assertEquals(0.0, ReviewRepository.averageRating(emptyList()), 0.001)
}

class LoyaltyPointsAccountTest {

    private lateinit var users: FakeUserStore
    private val freeDrink = Reward(1, "Free drink", 50, "🥤")

    @Before
    fun setUp() {
        users = FakeUserStore()
        AuthRepository.store = users
    }

    private suspend fun newUser(email: String): Int =
        (AuthRepository.register("Points", email, "Monate@123") as AuthResult.Success).user.id

    @Test fun pointsAreAddedToAnAccount() = runTest {
        val id = newUser("points.one@monate.co.za")
        AuthRepository.addLoyaltyPoints(id, 30)
        assertEquals(30, AuthRepository.loyaltyPoints(id))
    }

    @Test fun zeroOrNegativePointsAreIgnored() = runTest {
        val id = newUser("points.zero@monate.co.za")
        AuthRepository.addLoyaltyPoints(id, 0)
        AuthRepository.addLoyaltyPoints(id, -5)
        assertEquals(0, AuthRepository.loyaltyPoints(id))
    }

    @Test fun redeemingDeductsThePointsAndGivesACode() = runTest {
        val id = newUser("points.two@monate.co.za")
        AuthRepository.addLoyaltyPoints(id, 60)
        val code = AuthRepository.redeem(id, freeDrink)
        assertNotNull(code)
        assertTrue(code!!.matches(Regex("MON-\\d{4}")))
        assertEquals(10, AuthRepository.loyaltyPoints(id))
        assertEquals(1, users.redemptions.size)
    }

    @Test fun redeemingWithTooFewPointsFails() = runTest {
        val id = newUser("points.three@monate.co.za")
        assertNull(AuthRepository.redeem(id, freeDrink))
        assertEquals(0, AuthRepository.loyaltyPoints(id))
        assertEquals(0, users.redemptions.size)
    }
}
