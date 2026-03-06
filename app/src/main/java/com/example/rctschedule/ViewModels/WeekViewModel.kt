package com.example.rctschedule.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.TransformExcelDayTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WeekViewModel : ViewModel()
{
    val weekDataState = MutableStateFlow<ScheduleWeekData?>(null)

    val dayState = MutableStateFlow<TransformExcelDayTable?>(null)

    val tableMetaData = MutableStateFlow(ScheduleMeta())

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

    fun SetWeekSchedule(weekData: ScheduleWeekData)
    {
        weekDataState.value = weekData
        tableMetaData.value = weekData.meta

        daySelectionViewModel.SetSelection(weekData.weekTable.days)
        daySelectionViewModel.currentDayOfWeek()
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