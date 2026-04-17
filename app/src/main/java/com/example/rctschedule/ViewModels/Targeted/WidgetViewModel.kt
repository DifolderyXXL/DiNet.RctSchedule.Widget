package com.example.rctschedule.ViewModels.Targeted

import com.example.rctschedule.Data.primitives.DateRange
import com.example.rctschedule.Model.Lce
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.TransformExcelDayTable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
sealed interface WidgetState{
    @Serializable
    data object Loading : WidgetState

    @Serializable
    data class Error(val error: String) : WidgetState

    @Serializable
    data class ContentState(
        val courseSelectionViewModel: CourseSelectionViewModel,
        val groupSelectionViewModel: GroupSelectionViewModel,
        val widgetViewModel: Lce<WidgetViewModel>
    ) : WidgetState
}

@Serializable
data class WidgetViewModel(
    val daySelectionViewModel: DaySelectionViewModel,
    val weekSelectionViewModel: WeekSelectionViewModel,
    val metaViewModel: MetaViewModel,
    val contentViewModel: ContentViewModel
)


@Serializable
data class GroupSelectionViewModel(
    val available: List<Int>,
    val selected: Int
)

@Serializable
data class DaySelectionViewModel(
    val available: List<DayOfWeek>,
    val selected: DayOfWeek,
    val isCurrent: Boolean
)

@Serializable
data class WeekSelectionViewModel(
    @Contextual val available: List<ScheduleMeta>,
    val selected: Int,
    val isCurrent: Boolean
)

@Serializable
data class MetaViewModel(
    val group: Int,
    val week: Int,
    @Contextual val dateRange: DateRange
)

@Serializable
data class ContentViewModel(
    @Contextual val dayTable: TransformExcelDayTable,
    val updateTimestamp: Long
)

@Serializable
data class CourseSelectionViewModel(
    val available: List<Int>,
    val course: Int
)

