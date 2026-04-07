package com.example.rctschedule.Views

import android.util.Log
import androidx.constraintlayout.solver.Cache
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Services.Repositories.Lce
import com.example.rctschedule.Services.Repositories.ScheduleDataRepository
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.UseCases.GetWidgetDisplayDataUseCase
import com.example.rctschedule.ViewModels.ContentState
import com.example.rctschedule.ViewModels.DaySelectState
import com.example.rctschedule.ViewModels.ScheduleUiState
import com.example.rctschedule.ViewModels.WeekSelectState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.time.DayOfWeek
import javax.inject.Inject
class ScheduleWidgetLoader @Inject constructor(
    private val scheduleRepo: ScheduleDataRepository,
    private val displayModeRepository: WidgetDisplayModeRepository,
    private val getWidgetDisplayDataUseCase: GetWidgetDisplayDataUseCase,
    private val groupToggleRepository: GroupToggleRepository,
) {

    suspend fun getCurrentState(): ScheduleUiState {
        val scheduleLce = scheduleRepo.scheduleState.value
        val displayMode = displayModeRepository.get()

        return mapToUiState(scheduleLce, displayMode)
    }


    suspend fun getScheduleFlow(): Flow<ScheduleUiState> {
        return combine(
            scheduleRepo.scheduleState.filterNotNull(),
            displayModeRepository.valueFlow,
            groupToggleRepository.valueFlow,

        ) { scheduleLce, displayMode, _ ->
            mapToUiState(scheduleLce, displayMode)
        }
    }

    private suspend fun mapToUiState(scheduleLce: Lce<CacheEntry<ScheduleGroupWeeksData>>?, displayMode: WidgetDisplayMode): ScheduleUiState {
        val fullSchedule = when (scheduleLce) {
            is Lce.Content -> scheduleLce.data
            else -> scheduleRepo.cachedState.value
        }

        if (fullSchedule == null) {
            return when (scheduleLce) {
                is Lce.Error -> ScheduleUiState()
                else -> ScheduleUiState()
            }
        }

        val displayData = getWidgetDisplayDataUseCase()

        return ScheduleUiState(
            content = ContentState(
                fullSchedule.data.group,
                displayData,
                fullSchedule.timestamp
            ),

            selectWeek = WeekSelectState(
                weeksMetas = fullSchedule.data.weeks.map { it.meta },
                selectedWeek = displayData?.week?.meta ?: ScheduleMeta()
            ),

            selectDay = DaySelectState(
                days = DayOfWeek.entries,
                selectedDay = displayData?.day?.day ?: DayOfWeek.MONDAY
            ),
        )
    }
}