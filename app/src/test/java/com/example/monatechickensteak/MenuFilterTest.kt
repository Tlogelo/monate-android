package com.example.monatechickensteak

import com.example.monatechickensteak.data.SeedData
import com.example.monatechickensteak.util.MenuFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuFilterTest {

    private val menu = SeedData.foodItems()

    @Test fun allCategoryReturnsEveryItem() {
        assertEquals(menu.size, MenuFilter.filter(menu, "All", "").size)
    }

    @Test fun categoryFilterOnlyReturnsThatCategory() {
        val drinks = MenuFilter.filter(menu, "Drinks", "")
        assertEquals(4, drinks.size)
        assertTrue(drinks.all { it.category == "Drinks" })
    }

    @Test fun searchIsCaseInsensitive() {
        val result = MenuFilter.filter(menu, "All", "SPRITE")
        assertEquals(1, result.size)
        assertEquals("Sprite 330ml", result.first().name)
    }

    @Test fun searchMatchesDescriptionsToo() {
        // "crumbed" only appears in a description, not in any item name
        val result = MenuFilter.filter(menu, "All", "crumbed")
        assertTrue(result.any { it.name == "Crumbed Burger" })
    }

    @Test fun noMatchGivesEmptyList() {
        assertEquals(0, MenuFilter.filter(menu, "All", "zzzz").size)
    }

    @Test fun categoryAndSearchCombine() {
        assertEquals(0, MenuFilter.filter(menu, "Steak", "sprite").size)
    }
}
