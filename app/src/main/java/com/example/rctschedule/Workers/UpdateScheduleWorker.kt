package com.example.rctschedule.Workers

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rctschedule.Data.primitives.Helpers.DateRangeHelper
import com.example.rctschedule.Di.ParserModule.sheetRegularContextProvider
import com.example.rctschedule.Model.Lce
import com.example.rctschedule.Model.extensions.nextOnFailure
import com.example.rctschedule.Model.extensions.nextOnFailureAsync
import com.example.rctschedule.Repositories.ScheduleUpdater
import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Parsing.SheetRegularContextProvider
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
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.ViewModels.Targeted.WidgetViewModel
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Views.ScheduleGlanceStateDefinition
import com.example.rctschedule.Views.ViewStates.WidgetViewModelView
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.DayOfWeek

@HiltWorker
class UpdateScheduleWorker @AssistedInject constructor(
    val scheduleUpdater: ScheduleUpdater,
    val appSettingsUseCase: GetAppSettingsUseCase,
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters
) : CoroutineWorker(context, parameters){

    override suspend fun doWork() : Result{
        try{
            Log.d("UpdateScheduleWorker", "Start worker")

            if (isStopped) {
                Log.d("UpdateScheduleWorker", "Worker stopped before starting")
                return Result.retry()
            }

            val settings = appSettingsUseCase()
            scheduleUpdater.refreshFromServer(settings.selectedCourse, settings.selectedGroup)

            MyAppWidget().updateAll(context)
        }
        catch (e: Exception)
        {
            Log.e("UpdateScheduleWorker", e.toString())
            e.printStackTrace()
            return Result.retry()
        }

        return Result.success()
    }
}


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
                Lce.Error(it)
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
                    DayOfWeek.of(i)
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
            widgetViewModel = Lce.Content(widgetViewModel)
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

        return Result.success()
    }
}

