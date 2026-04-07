package com.example.rctschedule.UseCaseTesting

import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.UseCases.Helpers.ActualSystemTime
import com.example.rctschedule.UseCases.Helpers.WidgetModeCalculator
import org.junit.Assert
import org.junit.Test
import java.time.DayOfWeek

class WidgetModeCalculatorTest{
    @Test
    fun selectedTodayDayAndWeekTest(){
        val selectWeek = 1
        val selectDay = DayOfWeek.TUESDAY
        val newVal = WidgetModeCalculator.calculateNewMode(
            WidgetDisplayMode.Fixed(0, DayOfWeek.MONDAY),
            ActualSystemTime(selectWeek, selectDay),
            selectWeek,
            selectDay
        )

        val expected = WidgetDisplayMode.FollowCurrent

        Assert.assertEquals(expected, newVal)
    }

    @Test
    fun selectNotTodayDayTest(){
        val selectWeek = 1
        val selectDay = DayOfWeek.TUESDAY
        val newVal = WidgetModeCalculator.calculateNewMode(
            WidgetDisplayMode.Fixed(0, DayOfWeek.MONDAY),
            ActualSystemTime(selectWeek, DayOfWeek.MONDAY),
            selectWeek,
            selectDay
        )

        val expected = WidgetDisplayMode.FollowCurrent

        Assert.assertNotEquals(expected, newVal)
    }

    @Test
    fun selectNotTodayWeekTest(){
        val selectWeek = 1
        val selectDay = DayOfWeek.TUESDAY
        val newVal = WidgetModeCalculator.calculateNewMode(
            WidgetDisplayMode.Fixed(0, DayOfWeek.MONDAY),
            ActualSystemTime(0, selectDay),
            selectWeek,
            selectDay
        )

        val expected = WidgetDisplayMode.FollowCurrent

        Assert.assertNotEquals(expected, newVal)
    }
}