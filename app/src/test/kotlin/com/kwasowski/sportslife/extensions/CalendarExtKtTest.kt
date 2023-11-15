package com.kwasowski.sportslife.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Locale

class CalendarExtKtTest {

    @Test
    fun `should return correct narrow name`() {
        //given
        val calendar = Calendar.getInstance()
        calendar.set(2023, Calendar.NOVEMBER, 15) // Ustaw datę na 15 listopada 2023

        //when
        val narrowName = calendar.getNarrowName()

        //then
        assertEquals(
            "Ś",
            narrowName
        ) // Zakładając, że 15 listopada 2023 to środa (ang. Wednesday), skrócona nazwa to "Ś" w języku polskim
    }

    @Test
    fun `should return correct narrow name with different locale`() {
        //given
        val calendar = Calendar.getInstance()
        calendar.set(2023, Calendar.NOVEMBER, 15)

        //when
        val narrowNameFrench = calendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.NARROW_FORMAT,
            Locale.FRENCH
        ) as String

        //then
        assertEquals(
            "M",
            narrowNameFrench
        ) // Zakładając, że 15 listopada 2023 to środa, skrócona nazwa w języku francuskim to "M"
    }
}