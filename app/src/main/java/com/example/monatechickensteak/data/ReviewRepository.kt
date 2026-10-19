package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ReviewRepository {

    lateinit var store: ReviewStore

    /** All reviews, newest first. */
    suspend fun all(): List<Review> = withContext(Dispatchers.IO) { store.all() }

    suspend fun add(userId: Int, itemName: String, rating: Int, comment: String, author: String): Review =
        withContext(Dispatchers.IO) {
            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            store.insert(userId, itemName, rating, comment, author, date)
        }

    fun averageRating(reviews: List<Review>): Double =
        if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
}
