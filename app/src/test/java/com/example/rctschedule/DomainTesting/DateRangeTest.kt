package com.example.rctschedule.DomainTesting

import com.example.rctschedule.Data.primitives.Helpers.DateRangeHelper
import com.example.rctschedule.Data.primitives.DateRange
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class DateRangeTest {
    @Test
    fun timeInRangeTest(){
        val range = DateRange(
            LocalDate.of(2000, 12, 5),
            LocalDate.of(2000, 12, 25) )


        val localDate = LocalDate.of(2000, 12, 7)

        Assert.assertTrue(DateRangeHelper.dateInRangeWithoutYear(localDate, range))
    }

    @Test
    fun timeInRangeWithDiffMonthsTest(){
        val range = DateRange(
            LocalDate.of(2000, 11, 24),
            LocalDate.of(2000, 12, 5) )

        val localDate = LocalDate.of(2000, 11, 27)
        val localDate2 = LocalDate.of(2000, 12, 4)

        Assert.assertTrue(DateRangeHelper.dateInRangeWithoutYear(localDate, range))
        Assert.assertTrue(DateRangeHelper.dateInRangeWithoutYear(localDate2, range))
    }

    @Test
    fun timeInRangeWithDiffYearsTest(){
        val range = DateRange(
            LocalDate.of(2000, 11, 24),
            LocalDate.of(2000, 1, 5) )

        val localDate = LocalDate.of(2000, 11, 27)

        Assert.assertTrue(DateRangeHelper.dateInRangeWithoutYear(localDate, range))
    }
}