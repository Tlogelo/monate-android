package com.example.monatechickensteak.util

object LoyaltyRules {

    const val RAND_PER_POINT = 10
    const val POINTS_PER_REVIEW = 5

    enum class Tier(val label: String, val minPoints: Int) {
        BRONZE("Bronze", 0),
        SILVER("Silver", 100),
        GOLD("Gold", 300)
    }

    /** 1 point for every R10 spent (rounded down). */
    fun pointsForOrder(total: Double): Int = (total / RAND_PER_POINT).toInt()

    fun tierFor(points: Int): Tier = Tier.values().last { points >= it.minPoints }

    /** The next tier up, or null when the customer is already Gold. */
    fun nextTier(points: Int): Tier? = Tier.values().firstOrNull { it.minPoints > points }
}
