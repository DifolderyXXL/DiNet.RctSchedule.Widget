package com.example.rctschedule.Workers

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.rctschedule.Data.primitives.Helpers.DateRangeHelper
import com.example.rctschedule.Model.extensions.nextOnFailureAsync
import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Time.TimeProvider
import com.example.rctschedule.UseCases.GetAppSettingsUseCase
import com.example.rctschedule.UseCases.GetWidgetDisplayDataUseCase
import com.example.rctschedule.UseCases.RefreshWidgetDisplayDataUseCase
import com.example.rctschedule.UseCases.schedule.GetScheduleUseCase
import com.example.rctschedule.ViewModels.Targeted.ContentViewModel
import com.example.rctschedule.ViewModels.Targeted.CourseSelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.DaySelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.GroupSelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.MetaViewModel
import com.example.rctschedule.ViewModels.Targeted.WeekSelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.WidgetLce
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.ViewModels.Targeted.WidgetViewModel
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Views.ScheduleGlanceStateDefinition
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.DayOfWeek

@HiltWorker
class InitializeWidgetWorker @AssistedInject constructor(
    val getAppSettingsUseCase: GetAppSettingsUseCase,
    val getScheduleUseCase: GetScheduleUseCase,
    val getWidgetDisplayDataUseCase: GetWidgetDisplayDataUseCase,
    val refreshWidgetDisplayDataUseCase: RefreshWidgetDisplayDataUseCase,

    val sheetRegularContextProvider: ISheetRegularContextProvider,

    val timeProvider: TimeProvider,
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters
) : CoroutineWorker(context, parameters){

    companion object{
        fun enqueue(workManager: WorkManager){
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<InitializeWidgetWorker>()
                .addTag("initialize")
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            workManager.enqueueUniqueWork(
                "initialize_widget_worker",
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }

        fun enqueue(context: Context){
            enqueue(WorkManager.getInstance(context))
        }
    }


    override suspend fun doWork(): Result {

        val appSettings = getAppSettingsUseCase()
        val regularContext = sheetRegularContextProvider.get(appSettings.selectedCourse)

        val courseSelection = CourseSelectionViewModel(
            available = sheetRegularContextProvider.getAllCourses(),
            course = appSettings.selectedCourse
        )

        val groupSelection = GroupSelectionViewModel(
            available = (0..<regularContext.getGroupCount()).toList(),
            selected = appSettings.selectedGroup
        )

        val scheduleResult = getScheduleUseCase(appSettings.selectedCourse, appSettings.selectedGroup)

        scheduleResult.onFailure {
            val content = WidgetState.ContentState(
                courseSelection,
                groupSelection,
                WidgetLce.Error(it)
            )

            GlanceAppWidgetManager(context)
                .getGlanceIds(MyAppWidget::class.java).forEach{ glanceId ->
                    updateAppWidgetState(
                        context = context,
                        definition = ScheduleGlanceStateDefinition(),
                        glanceId = glanceId,
                        updateState = {
                            content
                        }
                    )
                }
        }

        if(scheduleResult.isFailure) {
            return Result.success()
        }
        val schedule = scheduleResult.getOrThrow()

        val displayData = getWidgetDisplayDataUseCase(schedule)
            .nextOnFailureAsync {
                refreshWidgetDisplayDataUseCase(schedule)
            }.getOrNull()

        if(displayData == null)
            return Result.failure()

        val meta = displayData.week.meta
        val isCurrentWeek = DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate(),
            meta.dateRange
        ) || (timeProvider.getCurrentDayOfWeek() == DayOfWeek.SUNDAY
                && DateRangeHelper.dateInRangeWithoutYear(
            timeProvider.getCurrentDate().minusDays(1),
            meta.dateRange)
                )

        val widgetViewModel = WidgetViewModel(
            daySelectionViewModel = DaySelectionViewModel(
                available = displayData.week.weekTable.days.mapIndexed { i, p ->
                    DayOfWeek.of(i + 1)
                },
                selected = displayData.day.day,
                isCurrent = timeProvider.getCurrentDayOfWeek() == displayData.day.day
            ),
            weekSelectionViewModel = WeekSelectionViewModel(
                available = schedule.weeks.map { it.meta },
                selected = displayData.week.meta.weekNumber,
                isCurrent = isCurrentWeek
            ),
            metaViewModel = MetaViewModel(
                schedule.group,
                displayData.week.meta.weekNumber,
                displayData.week.meta.dateRange
            ),
            contentViewModel = ContentViewModel(
                displayData.day.weekTable,
                schedule.updateTimestamp
            )
        )

        val content = WidgetState.ContentState(
            courseSelectionViewModel = courseSelection,
            groupSelectionViewModel = groupSelection,
            widgetViewModel = WidgetLce.Content(widgetViewModel)
        )

        GlanceAppWidgetManager(context)
            .getGlanceIds(MyAppWidget::class.java).forEach{ glanceId ->

                updateAppWidgetState(
                    context = context,
                    definition = ScheduleGlanceStateDefinition(),
                    glanceId = glanceId,
                    updateState = {
                        content
                    }
                )
        }

        MyAppWidget().updateAll(context)

        return Result.success()
    }
}