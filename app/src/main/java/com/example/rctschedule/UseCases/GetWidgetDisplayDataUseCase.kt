package com.example.rctschedule.UseCases

import com.example.rctschedule.Helpers.DateRangeHelper
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleDayData
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.TransformExcelDayTable
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class WidgetDisplayData(
    val week: ScheduleWeekData,
    val day: ScheduleDayData)

@Singleton
class GetWidgetDisplayDataUseCase @Inject constructor(
    private val timeProvider: TimeProvider,
    private val displayModeRepository: WidgetDisplayModeRepository,
    private val scheduleRepository: ScheduleDataRepository
) {
    suspend operator fun invoke() : WidgetDisplayData? {
        val displayMode = displayModeRepository.valueFlow.first()

        val schedule = scheduleRepository.getCachedSchedule()
            ?: return null

        return when(displayMode){
            is WidgetDisplayMode.FollowCurrent ->{
                getCurrentDay(schedule)
            }
            is WidgetDisplayMode.Fixed ->{
                val fixedWeek = schedule.data.weeks.firstOrNull{it.meta.weekNumber == displayMode.weekId}
                val fixedDay = fixedWeek?.weekTable?.days?.getOrNull(displayMode.dayOfWeek.value-1)

                if(fixedWeek != null) {
                    WidgetDisplayData(fixedWeek,
                        ScheduleDayData(fixedDay?: TransformExcelDayTable(),
                            displayMode.dayOfWeek))
                }
                else{
                    displayModeRepository.set(WidgetDisplayMode.FollowCurrent)
                    getCurrentDay(schedule)
                }
            }
        }
    }

    private fun getCurrentDay(schedule: CacheEntry<ScheduleGroupWeeksData>): WidgetDisplayData? {
        val today = timeProvider.getCurrentDate()
        val dayOfWeek = today.dayOfWeek

        val todayWeek = schedule.data.getWeekForDateSmart(today)
            ?: return null
        val todayDay = todayWeek.weekTable.days.getOrNull(dayOfWeek.value-1)
            ?: return WidgetDisplayData(todayWeek, ScheduleDayData(TransformExcelDayTable(), dayOfWeek))

        return WidgetDisplayData(todayWeek, ScheduleDayData(todayDay, dayOfWeek))
    }
}