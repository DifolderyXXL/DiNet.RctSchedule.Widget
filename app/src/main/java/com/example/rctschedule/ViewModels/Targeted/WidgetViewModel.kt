package com.example.rctschedule.ViewModels.Targeted

import com.example.rctschedule.Model.DateRange
import com.example.rctschedule.TransformExcelDayTable
import java.time.DayOfWeek

sealed interface WidgetState{
    data class CourseSelectionState(
        val courseSelectionViewModel: CourseSelectionViewModel
    ) : WidgetState

    data class GroupSelectionState(
        val courseSelectionViewModel: CourseSelectionViewModel,
        val groupSelectionViewModel: GroupSelectionViewModel,
    ) : WidgetState

    data class ContentState(
        val widgetViewModel: WidgetViewModel
    ) : WidgetState
}

data class WidgetViewModel(
    val courseSelectionViewModel: CourseSelectionViewModel,
    val groupSelectionViewModel: GroupSelectionViewModel,
    val daySelectionViewModel: DaySelectionViewModel,
    val metaViewModel: MetaViewModel,
    val contentViewModel: ContentViewModel,
    val weekSelectionViewModel: WeekSelectionViewModel
)


data class GroupSelectionViewModel(
    val available: List<Int>,
    val selected: Int?
)

data class DaySelectionViewModel(
    val available: List<DayOfWeek>,
    val selected: DayOfWeek?
)

data class WeekSelectionViewModel(
    val available: List<Int>,
    val selected: Int?
)

data class MetaViewModel(
    val group: Int,
    val week: Int,
    val dateRange: DateRange
)

data class ContentViewModel(
    val dayTable: TransformExcelDayTable
)

data class CourseSelectionViewModel(
    val course: Int
)

