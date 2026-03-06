package com.example.rctschedule.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.Model.CacheEntry
import com.example.rctschedule.Model.FetchState
import com.example.rctschedule.Services.Lce
import com.example.rctschedule.Services.ScheduleDataRepository
import com.example.rctschedule.Model.ScheduleGroupWeeksData
import com.example.rctschedule.Services.ApplicationSettings
import com.example.rctschedule.Services.ApplicationSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date


class WidgetViewModel(
    val scheduleRepository: ScheduleDataRepository,
    val appSettingsRepository: ApplicationSettingsRepository
)
    : ViewModel()
{
    private val _state = MutableStateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>(Lce.Loading)
    private val _fetchingState = MutableStateFlow<FetchState>(FetchState.Null)

    val fetchState : StateFlow<FetchState> = _fetchingState

    val lastUpdate = MutableStateFlow(Date(0))
    val group = MutableStateFlow<Int>(-1)
    val displayingGroup = MutableStateFlow<Int>(-1)

    val weekViewModel = WeekViewModel()
    val selectionViewModel = SelectionViewModel()

    init {
        appSettingsRepository.settingsFlow.onEach { state->
            group.value = state.selectedGroup
        }.launchIn(viewModelScope)

        scheduleRepository.scheduleState.onEach { state->
            _state.value = state
        }.launchIn(viewModelScope)

        _state.onEach{ se ->
            if(se is Lce.Content<CacheEntry<ScheduleGroupWeeksData>>)
            {
                lastUpdate.value = Date(se.data.timestamp)
                displayingGroup.value = se.data.data.group

                selectionViewModel.SetSelection(se.data.data.weeks)
            }
        }.launchIn(viewModelScope)

        selectionViewModel.selectFlow.onEach { value ->
            if(value != null)
            {
                weekViewModel.SetWeekSchedule(value)
            }
        }.launchIn(viewModelScope)
    }


    suspend fun requestUpdateInternal(force: Boolean = false)
    {
        _fetchingState.value = FetchState.Fetching

        val result = scheduleRepository.requestUpdate(force)

        if(result.isSuccess)
            _fetchingState.value = FetchState.Completed
        else
            _fetchingState.value = FetchState.Error
    }

    fun setGroup(groupIndex: Int)
    {
        appSettingsRepository.set(ApplicationSettings(groupIndex))

        viewModelScope.launch {
            scheduleRepository.loadSynchronously()
        }
    }

    fun forceUpdateCommand()
    {
        viewModelScope.launch {
            requestUpdateInternal(true)
        }
    }
}