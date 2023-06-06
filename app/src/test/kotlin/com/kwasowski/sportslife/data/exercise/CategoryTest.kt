package com.kwasowski.sportslife.data.exercise

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CategoryTest {

    @Nested
    inner class FromString {
        private val categoriesEN =
            arrayOf("Aerobics", "American soccer", "Basketball", "Boxing", "Calisthenics")
        private val categoriesPL =
            arrayOf("Aerobik", "Futbol amerykański", "Koszykówka", "Boks", "Kalistenika")

        @Test
        fun `It should return the correct category if there is a space in the text`() {
            val categoryForPL = Category.fromString("Futbol amerykański", categoriesPL)

            assertEquals(Category.AMERICAN_SOCCER, categoryForPL)
        }

        @Test
        fun `The value returned should be the same for the difference in languages`() {
            val categoryForPL = Category.fromString("Aerobik", categoriesPL)
            val categoryForEN = Category.fromString("Aerobics", categoriesEN)

            assertEquals(categoryForPL, categoryForEN)
        }

        @Test
        fun `It should return null if it does not find a valid category`() {
            val categoryForEN = Category.fromString("INVALID CATEGORY", categoriesEN)

            assertEquals(null, categoryForEN)
        }
    }
}
