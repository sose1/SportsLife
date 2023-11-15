package com.kwasowski.sportslife.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date

class DateExtensionsTest {
    private val calendar = Calendar.getInstance()

    @Test
    fun `should correctly add positive days`() {
        //given
        val initialDate = createDate(2023, Calendar.NOVEMBER, 15)
        val expectedDate = createDate(2023, Calendar.NOVEMBER, 20)

        //when
        val resultDate = initialDate.addDays(5)

        //then
        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun `should correctly add negative days`() {
        //given
        val initialDate = createDate(2023, Calendar.NOVEMBER, 15)
        val expectedDate = createDate(2023, Calendar.NOVEMBER, 12)

        //when
        val resultDate = initialDate.addDays(-3)

        //then
        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun `should correctly add zero days`() {
        //given
        val initialDate = createDate(2023, Calendar.NOVEMBER, 15)

        //when
        val resultDate = initialDate.addDays(0)

        //then
        assertEquals(initialDate, resultDate)
    }

    @Test
    fun `should correctly add days crossing months`() {
        //given
        val initialDate = createDate(2023, Calendar.OCTOBER, 29)
        val expectedDate = createDate(2023, Calendar.NOVEMBER, 3)

        //when
        val resultDate = initialDate.addDays(5)

        //then
        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun `should correctly add days crossing years`() {
        //given
        val initialDate = createDate(2022, Calendar.DECEMBER, 29)
        val expectedDate = createDate(2023, Calendar.JANUARY, 3)

        //when
        val resultDate = initialDate.addDays(5)

        //then
        assertEquals(expectedDate, resultDate)
    }

    private fun createDate(year: Int, month: Int, day: Int): Date {
        calendar.set(year, month, day)
        return calendar.time
    }
}