package com.example.rctschedule.Views

import android.util.Log
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Services.Repositories.Lce
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.SelectedDayRepository
import com.example.rctschedule.Services.Repositories.SelectedWeekRepository
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.UseCases.ScheduleNavigationUseCase
import com.example.rctschedule.ViewModels.ScheduleUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ScheduleWidgetLoader @Inject constructor(
    private val scheduleRepo: ScheduleDataRepository,
    private val weekRepo: SelectedWeekRepository,
    private val dayRepo: SelectedDayRepository,
    private val navigationUseCase: ScheduleNavigationUseCase
) {
    fun getScheduleFlow(): Flow<ScheduleUiState> {
        return combine(
            scheduleRepo.scheduleState,
            weekRepo.valueFlow,
            dayRepo.valueFlow
        ) { scheduleLce, selectedWeek, selectedDaySettings ->

            val fullSchedule = when (scheduleLce) {
                is Lce.Content -> scheduleLce.data
                else -> scheduleRepo.cachedState.value
            }

            if (fullSchedule == null) {
                return@combine when (scheduleLce) {
                    is Lce.Error -> ScheduleUiState(isLoading = false)
                    else -> ScheduleUiState(isLoading = true)
                }
            }

            val currentWeekId = navigationUseCase.provideCurrentWeekOrAny()?.weekNumber

            val targetWeek =
                fullSchedule.data.weeks.find { it.meta.weekNumber == currentWeekId }

            if(targetWeek == null)
            {
                Log.e(ScheduleWidgetLoader::class.simpleName, "targetWeek == null")
            }

            val targetDay =
                targetWeek?.weekTable?.days?.getOrNull(selectedDaySettings.selectedDayOfWeek.value - 1)

            return@combine ScheduleUiState(
                anyContent = targetDay != null,
                isLoading = (scheduleLce is Lce.Loading),
                currentDayName = selectedDaySettings.selectedDayOfWeek,
                isDayOff = (targetDay == null),
                day = targetDay ?: TransformExcelDayTable(emptyList()),
                tableMeta = targetWeek?.meta ?: ScheduleMeta(),
                group = fullSchedule.data.group,
                cacheInstance = fullSchedule
            )
        }
    }
}