package com.example.rctschedule.Model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.Services.TransformService
import com.example.rctschedule.TransformExcelTable
import com.example.rctschedule.TransformWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.commons.lang3.mutable.Mutable
import java.util.Date

class WidgetViewModel(
    private val scheduleRepository: ScheduleDataRepository)
    : ViewModel()
{
    private val _state = MutableStateFlow<ScheduleCacheData>(ScheduleCacheData.None)
    private val _fetchingState = MutableStateFlow<FetchState>(FetchState.Null)

    val fetchState : StateFlow<FetchState> = _fetchingState

    val dayState = MutableStateFlow<TransformExcelTable?>(null)

    val selectedDay = MutableStateFlow(0)

    val lastUpdate = MutableStateFlow(Date(0))
    val tableMetaData = MutableStateFlow(ExcelTableMetaData())

    init {

        scheduleRepository.scheduleState.onEach { state->
            _state.value = state
        }.launchIn(viewModelScope)

        _state.onEach{ se ->
            if(se is ScheduleCacheData.Ok)
            {
                lastUpdate.value = Date(se.lastUpdateTime)
                tableMetaData.value = se.table.meta
            }

            showTargetDay(selectedDay.value)
        }.launchIn(viewModelScope)

        selectedDay.onEach{ value ->
            showTargetDay(value)
        }.launchIn(viewModelScope)

    }

    fun showTargetDay(dayIndex: Int)
    {
        Log.e("E", "EVENMT")
        if(_state.value is ScheduleCacheData.Ok)
        {
            val content = (_state.value as ScheduleCacheData.Ok)
            val week = content.table.weekTable
            if(week.days.size <= dayIndex || dayIndex < 0) {
                Log.e("E", "EXC ${week.days.size}")
                return
            }

            dayState.value = week.days[dayIndex]
        }
    }

    fun nextDay()
    {
        selectedDay.value += 1
    }

    fun previousDay()
    {
        selectedDay.value -= 1
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