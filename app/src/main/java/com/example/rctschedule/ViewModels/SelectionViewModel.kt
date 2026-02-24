package com.example.rctschedule.ViewModels

import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.TransformExcelDayTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.DayOfWeek
import java.time.LocalDateTime

public interface ISelectionViewModel<T>
{
    public val selectFlow: StateFlow<T?>
    public fun SetSelection(selection: List<T>)
    public fun ClearSelection()
    public fun Select(index: Int)
}

public open class BaseSelectionViewModel<T> : ViewModel(){
    private val _selectFlow = MutableStateFlow<T?>(null)
    val selectFlow: StateFlow<T?> = _selectFlow

    val selectedIndex = MutableStateFlow(-1)
    val selectionList = MutableStateFlow<List<T>>(emptyList())

    fun SetSelection(selection: List<T>) {
        selectionList.value = selection
        Select(0)
    }

    fun ClearSelection() {
        selectionList.value = emptyList()
        _selectFlow.value = null
        selectedIndex.value = -1
    }

    fun Select(index: Int) {
        val content = selectionList.value
        if(index >= 0 && index < content.size)
        {
            selectedIndex.value = index
            _selectFlow.value = content[index]
        }
    }
}

class DaySelectionViewModel : BaseSelectionViewModel<TransformExcelDayTable>()
{
    val selectedDayOfWeek = MutableStateFlow(DayOfWeek.MONDAY)

    init {
        selectedIndex.onEach { state ->
            selectedDayOfWeek.value = DayOfWeek.of(clamp(state+1, 1, 7))
        }.launchIn(viewModelScope)
    }

    public fun nextDay()
    {
        Select(this.selectedIndex.value+1)
    }

    public fun previousDay()
    {
        Select(this.selectedIndex.value-1)
    }

    public fun currentDayOfWeek()
    {
        val date = LocalDateTime.now()
        Select(date.dayOfWeek.value-1)
    }
}

class SelectionViewModel() : BaseSelectionViewModel<ScheduleWeekData>()
{
    private val _selectFlow = MutableStateFlow<ScheduleWeekData?>(null)
}