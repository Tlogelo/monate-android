package com.example.monatechickensteak.data

import android.content.Context
import com.example.monatechickensteak.data.db.MonateDbHelper
import com.example.monatechickensteak.data.db.SqliteCatalogStore
import com.example.monatechickensteak.data.db.SqliteOrderStore
import com.example.monatechickensteak.data.db.SqliteReviewStore
import com.example.monatechickensteak.data.db.SqliteSupportStore
import com.example.monatechickensteak.data.db.SqliteUserStore
import com.example.monatechickensteak.auth.FirebaseCredentialAuth

/** Connects every repository to the real SQLite database. Called once from MainActivity. */
object Graph {

    fun init(context: Context) {
        // Creating the helper does not open the database yet. That happens on the first
        // query, which the repositories always run on a background thread.
        val helper = MonateDbHelper.get(context)
        AuthRepository.store = SqliteUserStore(helper)
        AuthRepository.credentials = FirebaseCredentialAuth()
        OrderRepository.store = SqliteOrderStore(helper)
        ReviewRepository.store = SqliteReviewStore(helper)
        CatalogRepository.store = SqliteCatalogStore(helper)
        SupportRepository.store = SqliteSupportStore(helper)
    }
}
