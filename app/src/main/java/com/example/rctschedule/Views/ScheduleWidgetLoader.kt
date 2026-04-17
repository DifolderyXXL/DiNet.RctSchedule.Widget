package com.example.rctschedule.Views

import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Services.Repositories.ApplicationSettingsRepository
import com.example.rctschedule.Services.Repositories.GroupToggleRepository
import com.example.rctschedule.Model.Lce
import com.example.rctschedule.Services.Repositories.States.WidgetDisplayMode
import com.example.rctschedule.Services.Repositories.WidgetDisplayModeRepository
import com.example.rctschedule.UseCases.GetAppSettingsUseCase
import com.example.rctschedule.UseCases.GetWidgetDisplayDataUseCase
import com.example.rctschedule.ViewModels.ContentState
import com.example.rctschedule.ViewModels.DaySelectState
import com.example.rctschedule.ViewModels.ScheduleUiState
import com.example.rctschedule.ViewModels.WeekSelectState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import javax.inject.Inject
class ScheduleWidgetLoader @Inject constructor(
    private val scheduleRepo: ScheduleDataRepository,
    private val displayModeRepository: WidgetDisplayModeRepository,
    private val getWidgetDisplayDataUseCase: GetWidgetDisplayDataUseCase,
    private val groupToggleRepository: GroupToggleRepository,
    private val applicationSettingsRepository: ApplicationSettingsRepository,
    private val getAppSettingsUseCase: GetAppSettingsUseCase
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
            applicationSettingsRepository.valueFlow

        ) { scheduleLce, displayMode, _, _ ->
            mapToUiState(scheduleLce, displayMode)
        }
    }

    private suspend fun mapToUiState(
        scheduleLce: Lce<CacheEntry<ScheduleGroupWeeksData>>?,
        displayMode: WidgetDisplayMode,
        ): ScheduleUiState {
        val fullSchedule = when (scheduleLce) {
            is Lce.Content -> scheduleLce.data
            else -> scheduleRepo.cachedState.value
        }

        val appSettings = getAppSettingsUseCase()

        if (fullSchedule == null) {
            return when (scheduleLce) {
                is Lce.Error -> ScheduleUiState(appSettings = appSettings)
                else -> ScheduleUiState(appSettings = appSettings)
            }
        }

        val displayData = getWidgetDisplayDataUseCase()


        return ScheduleUiState(
            content = ContentState(
                fullSchedule.data.group,
                appSettings.selectedCourse,
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

            appSettings = appSettings
        )
    }
}