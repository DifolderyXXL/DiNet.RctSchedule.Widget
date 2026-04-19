package com.example.rctschedule.ViewModels.Targeted

import com.example.rctschedule.Data.primitives.DateRange
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.TransformExcelDayTable
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
sealed interface WidgetState{
    @Serializable
    data object Empty : WidgetState

    @Serializable
    data class Content(
        val courseSelectionViewModel: CourseSelectionViewModel,
        val groupSelectionViewModel: GroupSelectionViewModel,
        val widgetLceState: WidgetLce,
        val lastValidData: WidgetViewModel?,
    ) : WidgetState

    fun contentOrNull() : Content? = (this as? Content)
}

@Serializable
sealed class WidgetLce {
    @Serializable data object Loading : WidgetLce()
    @Serializable data object Content : WidgetLce()
    @Serializable data class Error(
        val message: String,
        val errType: String,
        val stacktrace: String? = null
    ) : WidgetLce(){
        constructor(e: Throwable) : this(
            message = e.message ?: "Unknown",
            errType = e.javaClass.simpleName,
            stacktrace = e.stackTraceToString()
        )
    }
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
    val available: List<ScheduleMeta>,
    val selected: Int,
    val isCurrent: Boolean
)

@Serializable
data class MetaViewModel(
    val group: Int,
    val week: Int,
    val dateRange: DateRange,
    val metaGroupName: String?
)

@Serializable
data class ContentViewModel(
    val dayTable: TransformExcelDayTable,
    val updateTimestamp: Long
)

@Serializable
data class CourseSelectionViewModel(
    val available: List<Int>,
    val course: Int
)

