package com.example.rctschedule.UseCases

import com.example.rctschedule.Model.ScheduleDayData
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleWeekData
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
class InvalidScheduleException : Exception()
class DisplayDataInvalidException : Exception()

@Singleton
class GetWidgetDisplayDataUseCase @Inject constructor(
    private val timeProvider: TimeProvider,
    private val displayModeRepository: WidgetDisplayModeRepository,
) {
    suspend operator fun invoke(schedule: ScheduleGroupWeeksData) : Result<WidgetDisplayData> {
        return when(val displayMode = displayModeRepository.valueFlow.first()){
            is WidgetDisplayMode.FollowCurrent ->{
                val data = getCurrentDay(schedule, timeProvider)
                if(data == null)
                    Result.failure(DisplayDataInvalidException())
                else
                    Result.success(data)
            }
            is WidgetDisplayMode.Fixed ->{
                val fixedWeek = schedule.weeks.firstOrNull{it.meta.weekNumber == displayMode.weekId}
                val fixedDay = fixedWeek?.weekTable?.days?.getOrNull(displayMode.dayOfWeek.value-1)

                if(fixedWeek != null) {
                    val data = WidgetDisplayData(fixedWeek,
                        ScheduleDayData(fixedDay?: TransformExcelDayTable(),
                            displayMode.dayOfWeek))

                    Result.success(data)
                }
                else
                    Result.failure(DisplayDataInvalidException())
            }
        }
    }
}

fun getCurrentDay(schedule: ScheduleGroupWeeksData, timeProvider: TimeProvider): WidgetDisplayData? {
    val today = timeProvider.getCurrentDate()
    val dayOfWeek = today.dayOfWeek

    val todayWeek = schedule.getWeekForDateSmart(today)
        ?: return null
    val todayDay = todayWeek.weekTable.days.getOrNull(dayOfWeek.value-1)
        ?: return WidgetDisplayData(todayWeek, ScheduleDayData(TransformExcelDayTable(), dayOfWeek))

    return WidgetDisplayData(todayWeek, ScheduleDayData(todayDay, dayOfWeek))
}

/*


fixed -> try get -> fallback

today -> try get -> fallback



 */