package com.example.rctschedule.UseCaseTesting

import androidx.compose.material3.DateRangePicker
import com.example.rctschedule.Data.primitives.DateRange
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.TransformExcelWeek
import com.example.rctschedule.UseCases.GetWidgetDisplayDataUseCase
import com.example.rctschedule.UseCases.SelectDisplayUseCase
import com.example.rctschedule.UseCases.schedule.GetScheduleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class SelectDisplayUseCaseTest {
    private lateinit var useCase: GetWidgetDisplayDataUseCase

    private val timeProvider = mockk<TimeProvider>()
    private val widgetDisplayModeRepository = mockk<WidgetDisplayModeRepository>()

    @Before
    fun setup(){
        useCase = GetWidgetDisplayDataUseCase(
            timeProvider,
            widgetDisplayModeRepository
        )
    }

    @Test
    fun widgetDisplayTest1(){
        val flow = kotlinx.coroutines.flow.flowOf(WidgetDisplayMode.FollowCurrent)
        coEvery { widgetDisplayModeRepository.valueFlow } returns flow

        every { timeProvider.getCurrentDate() } returns LocalDate.of(2000, 1, 2)

        val dateRange = DateRange(
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2000, 1, 3)
        )

        val res = runBlocking {
            useCase.invoke(ScheduleGroupWeeksData(
                listOf(
                    ScheduleWeekData(TransformExcelWeek(emptyList()), ScheduleMeta(
                        dateRange = dateRange
                    ))), 1, 1))
        }

        assertTrue(res.isSuccess)
    }

    @Test
    fun widgetDisplayTest2(){
        val flow = kotlinx.coroutines.flow.flowOf(WidgetDisplayMode.Fixed(1, DayOfWeek.MONDAY))
        coEvery { widgetDisplayModeRepository.valueFlow } returns flow

        every { timeProvider.getCurrentDate() } returns LocalDate.of(2000, 1, 2)

        val dateRange = DateRange(
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2000, 1, 3)
        )

        val res = runBlocking {
            useCase.invoke(ScheduleGroupWeeksData(
                listOf(
                    ScheduleWeekData(TransformExcelWeek(emptyList()), ScheduleMeta(
                        dateRange = dateRange,
                        weekNumber = 1
                    ))), 1, 1))
        }

        assertTrue(res.isSuccess)
    }

/*     private val timeProvider = mockk<TimeProvider>()
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
    }*/
}

