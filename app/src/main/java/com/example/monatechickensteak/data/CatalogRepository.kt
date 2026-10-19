package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.Faq
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.Promotion
import com.example.monatechickensteak.model.Reward
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Read-only restaurant content: menu, categories, rewards, promotions and FAQs. */
object CatalogRepository {

    lateinit var store: CatalogStore

    /** Category names for the filter chips, with "All" first. */
    suspend fun categories(): List<String> = withContext(Dispatchers.IO) {
        listOf("All") + store.categories()
    }

    suspend fun menu(): List<FoodItem> = withContext(Dispatchers.IO) { store.menu() }

    suspend fun menuItem(id: Int): FoodItem? = withContext(Dispatchers.IO) {
        store.menu().firstOrNull { it.id == id }
    }

    suspend fun rewards(): List<Reward> = withContext(Dispatchers.IO) { store.rewards() }

    suspend fun promotions(): List<Promotion> = withContext(Dispatchers.IO) { store.promotions() }

    suspend fun faqs(): List<Faq> = withContext(Dispatchers.IO) { store.faqs() }
}
