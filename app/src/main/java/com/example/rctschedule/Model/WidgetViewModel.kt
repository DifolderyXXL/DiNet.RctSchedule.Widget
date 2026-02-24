package com.example.rctschedule.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.TransformExcelDayTable
import com.example.rctschedule.ViewModels.DaySelectionViewModel
import com.example.rctschedule.ViewModels.SelectionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date

class WeekViewModel : ViewModel()
{
    val weekDataState = MutableStateFlow<ScheduleWeekData?>(null)

    val dayState = MutableStateFlow<TransformExcelDayTable?>(null)

    val tableMetaData = MutableStateFlow(ExcelTableMetaData())

    val daySelectionViewModel = DaySelectionViewModel()


    init {
        daySelectionViewModel.selectFlow.onEach { state ->
            if(state != null)
                showDay(state)
        }.launchIn(viewModelScope)

        weekDataState.onEach { state ->
            showTargetDay(daySelectionViewModel.selectedIndex.value)
        }.launchIn(viewModelScope)
    }

    public fun SetWeekSchedule(weekData: ScheduleWeekData)
    {
        weekDataState.value = weekData
        tableMetaData.value = weekData.meta

        daySelectionViewModel.SetSelection(weekData.weekTable.days)
    }

    fun showDay(day: TransformExcelDayTable)
    {
        dayState.value = day
    }

    fun showTargetDay(dayIndex: Int)
    {
        val value = weekDataState.value
        if(value != null)
        {
            if(value.weekTable.days.size <= dayIndex || dayIndex < 0) {
                return
            }

            dayState.value = value.weekTable.days[dayIndex]
        }
    }

}


class WidgetViewModel(
    private val scheduleRepository: ScheduleDataRepository)
    : ViewModel()
{
    private val _state = MutableStateFlow<Lce<CacheEntry<ScheduleGroupWeeksData>>>(Lce.Loading)
    private val _fetchingState = MutableStateFlow<FetchState>(FetchState.Null)

    val fetchState : StateFlow<FetchState> = _fetchingState

    val lastUpdate = MutableStateFlow(Date(0))
    val group = MutableStateFlow<Int>(-1)

    val weekViewModel = WeekViewModel()
    val selectionViewModel = SelectionViewModel()

    init {
        scheduleRepository.scheduleState.onEach { state->
            _state.value = state
        }.launchIn(viewModelScope)

        _state.onEach{ se ->
            if(se is Lce.Content<CacheEntry<ScheduleGroupWeeksData>>)
            {
                lastUpdate.value = Date(se.data.timestamp)
                group.value = se.data.data.group

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

    fun forceUpdateCommand()
    {
        viewModelScope.launch {
            requestUpdateInternal(true)
        }
    }
}