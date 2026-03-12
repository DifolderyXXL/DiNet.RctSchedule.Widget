package com.example.rctschedule.UseCases

import android.util.Log
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Services.Repositories.States.DayFollowingType
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.SelectedDayRepository
import com.example.rctschedule.Services.Repositories.States.SelectedDaySettings
import com.example.rctschedule.Services.Repositories.SelectedWeekRepository
import com.example.rctschedule.Services.Repositories.States.SelectedWeekSettings
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleNavigationUseCase @Inject constructor(
    private val dayRepository: SelectedDayRepository,
    private val weekRepository: SelectedWeekRepository,
    private val scheduleRepository: ScheduleDataRepository
) {
    private val currentRealDay: DayOfWeek
        get() = LocalDate.now().dayOfWeek

    private suspend fun getActualScheduleData() : ScheduleGroupWeeksData?
    {
        val currentScheduleState = scheduleRepository.getCurrentSchedule()

        if (currentScheduleState == null) {
            Log.e(ScheduleNavigationUseCase::class.simpleName, "Skipped selection")
            return null
        }

        return currentScheduleState.data
    }

    suspend fun selectDay(dayOfWeek: DayOfWeek)
    {
        val actualData = getActualScheduleData()

        if(actualData == null)
        {
            Log.e(ScheduleNavigationUseCase::class.simpleName, "actualData == null")
            return
        }

        val weekSettings = provideCurrentWeekSuspend()
        if(weekSettings == null)
        {
            Log.e(ScheduleNavigationUseCase::class.simpleName, "Cant get week")
            return
        }

        val week = actualData.weeks.firstOrNull{it.meta.weekNumber == weekSettings.weekNumber}

        if(week == null){
            Log.e(ScheduleNavigationUseCase::class.simpleName, "Skipped selection")
            return
        }

        val dayIndex = dayOfWeek.value-1
        val day = week.weekTable.days.getOrNull(dayIndex)
            ?: return

        val targetDay = DayOfWeek.of(dayIndex+1)
        val follow = if(currentRealDay == targetDay)
            DayFollowingType.FollowToday
        else
            DayFollowingType.FollowSingleSelected

        dayRepository.set(SelectedDaySettings(
            targetDay,
            follow
        ))

        weekRepository.set(SelectedWeekSettings(
            week.meta.weekNumber))

        Log.e(ScheduleNavigationUseCase::class.simpleName, "Done")
    }

    suspend fun selectWeek(weekNumber: Int)
    {
        val actualData = getActualScheduleData()
            ?:return

        val week = actualData.weeks.firstOrNull{it.meta.weekNumber == weekNumber}
            ?:actualData.weeks.firstOrNull()
            ?:return

        weekRepository.set(SelectedWeekSettings(
            week.meta.weekNumber))
    }


     suspend fun provideCurrentWeekSuspend() : ScheduleMeta? {
        val actualData = scheduleRepository.cachedState.value?.data
            ?:scheduleRepository.getCurrentSchedule()?.data
            ?:return null

        val weekNumber = weekRepository.get().selectedWeekNumber

        val week = actualData.weeks.firstOrNull{it.meta.weekNumber == weekNumber}
            ?:actualData.weeks.firstOrNull()
            ?:return null

        weekRepository.set(SelectedWeekSettings(
            week.meta.weekNumber))

        return week.meta
    }

    fun provideCurrentWeekOrAny() : ScheduleMeta? {
        val actualData = scheduleRepository.cachedState.value?.data
            ?:return null

        val weekNumber = weekRepository.get().selectedWeekNumber

        val week = actualData.weeks.firstOrNull{it.meta.weekNumber == weekNumber}
            ?:actualData.weeks.firstOrNull()
            ?:return null

        weekRepository.set(SelectedWeekSettings(
            week.meta.weekNumber))

        return week.meta
    }
}

