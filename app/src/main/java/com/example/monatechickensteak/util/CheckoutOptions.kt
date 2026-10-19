package com.example.monatechickensteak.util

/** The choices offered at checkout. The payment names match the radio buttons on the cart screen. */
object CheckoutOptions {

    val collectionTimes = listOf(
        "As soon as possible",
        "In 15 minutes",
        "In 30 minutes",
        "In 1 hour"
    )

    val paymentMethods = listOf(
        "Cash on Collection",
        "Card Payment",
        "Other Payment Options"
    )

    /** Returns [saved] when it is a valid choice, otherwise the first option. */
    fun validTime(saved: String?): String =
        if (saved != null && saved in collectionTimes) saved else collectionTimes.first()

    /** Returns [saved] when it is a valid choice, otherwise null (nothing pre-selected). */
    fun validPaymentOrNull(saved: String?): String? =
        if (saved != null && saved in paymentMethods) saved else null
}
