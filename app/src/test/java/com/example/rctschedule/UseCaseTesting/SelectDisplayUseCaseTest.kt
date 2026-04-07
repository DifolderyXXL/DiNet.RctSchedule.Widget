package com.example.rctschedule.UseCaseTesting

import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.UseCases.SelectDisplayUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class SelectDisplayUseCaseTest {
    private val timeProvider = mockk<TimeProvider>()
    private val displayModeRepository = mockk<WidgetDisplayModeRepository>(relaxed = true)

    private val scheduleRepository = mockk<ScheduleDataRepository>()

    private lateinit var useCase: SelectDisplayUseCase

    @Before
    fun setup() {
        useCase = SelectDisplayUseCase(
            timeProvider,
            displayModeRepository,
            scheduleRepository
        )
    }

    @Test
    fun test1() = runTest {
        val fakeDate = LocalDate.of(2023, 10, 2) //monday

        every { timeProvider.getCurrentDate() } returns fakeDate

        coEvery { displayModeRepository.get() } returns WidgetDisplayMode.FollowCurrent

        val fakeSchedule = mockk<CacheEntry<ScheduleGroupWeeksData>>(relaxed = true){
            every { data.getWeekForDateSmart(fakeDate)?.meta?.weekNumber } returns 1
        }

        coEvery { scheduleRepository.getCachedSchedule() } returns fakeSchedule


        useCase(selectedWeekNumber = 2, selectedDayOfWeek = null)

        coVerify(exactly = 1) {
            displayModeRepository.set(
                WidgetDisplayMode.Fixed(weekId = 2, dayOfWeek = DayOfWeek.MONDAY)
            )
        }
    }

    @Test
    fun `when schedule is null, it aborts execution and saves nothing`() = runTest {
        // Arrange
        coEvery { scheduleRepository.getCachedSchedule() } returns null
        every { timeProvider.getCurrentDate() } returns LocalDate.now()

        // Act
        useCase(selectedWeekNumber = 2)

        // Assert
        coVerify(exactly = 0) {
            displayModeRepository.set(any())
        }
    }
}

