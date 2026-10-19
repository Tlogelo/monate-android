package com.example.monatechickensteak.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Messages a customer sends from the Help & Support screen. They are saved in the database. */
object SupportRepository {

    const val MIN_LENGTH = 10

    lateinit var store: SupportStore

    /** Returns an error message, or null when the message can be sent. */
    fun messageError(message: String): String? =
        if (message.trim().length < MIN_LENGTH) {
            "Please tell us a bit more (at least $MIN_LENGTH characters)"
        } else {
            null
        }

    suspend fun send(userId: Int, message: String) = withContext(Dispatchers.IO) {
        val createdAt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        store.insert(userId, message.trim(), createdAt)
    }
}
