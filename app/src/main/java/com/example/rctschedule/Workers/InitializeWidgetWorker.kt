package com.example.rctschedule.Workers

import android.content.Context
import androidx.glance.GlanceId
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
import com.example.rctschedule.Model.extensions.nextOnFailureAsync
import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Time.TodayMetadataCompareService
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

    val todayCompareService: TodayMetadataCompareService,
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
//              .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
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

        context.updateAllMyAppWidgetState {
            WidgetState.Content(
                courseSelection,
                groupSelection,
                WidgetLce.Loading,
                it.contentOrNull()?.lastValidData
            )
        }

        val scheduleResult = getScheduleUseCase(appSettings.selectedCourse, appSettings.selectedGroup)

        scheduleResult.onFailure {throwable ->
            context.updateAllMyAppWidgetState {
                WidgetState.Content(
                    courseSelection,
                    groupSelection,
                    WidgetLce.Error(throwable),
                    it.contentOrNull()?.lastValidData
                    )
            }
        }

        if(scheduleResult.isFailure) {
            return Result.failure()
        }
        val schedule = scheduleResult.getOrThrow()

        val displayDataResult = getWidgetDisplayDataUseCase(schedule)
            .nextOnFailureAsync {
                refreshWidgetDisplayDataUseCase(schedule)
            }.onFailure {throwable ->
                context.updateAllMyAppWidgetState {
                    WidgetState.Content(
                        courseSelection,
                        groupSelection,
                        WidgetLce.Error(throwable),
                        it.contentOrNull()?.lastValidData
                    )
                }
            }


        if(displayDataResult.isFailure)
            return Result.failure()

        val displayData = displayDataResult.getOrThrow()


        val meta = displayData.week.meta

        val widgetViewModel = WidgetViewModel(
            daySelectionViewModel = DaySelectionViewModel(
                available = displayData.week.weekTable.days.mapIndexed { i, p ->
                    DayOfWeek.of(i + 1)
                },
                selected = displayData.day.day,
                isCurrent = todayCompareService.isToday(displayData.day.day)
            ),
            weekSelectionViewModel = WeekSelectionViewModel(
                available = schedule.weeks.map { it.meta },
                selected = displayData.week.meta.weekNumber,
                isCurrent = todayCompareService.isCurrentWeek(meta)
            ),
            metaViewModel = MetaViewModel(
                schedule.group,
                displayData.week.meta.weekNumber,
                displayData.week.meta.dateRange,
                meta.groupSpecificName
            ),
            contentViewModel = ContentViewModel(
                displayData.day.weekTable,
                schedule.updateTimestamp
            )
        )

        val content = WidgetState.Content(
            courseSelectionViewModel = courseSelection,
            groupSelectionViewModel = groupSelection,
            widgetLceState = WidgetLce.Content,
            lastValidData = widgetViewModel
        )

        context.updateAllMyAppWidgetState {
            content
        }

        return Result.success()
    }
}

suspend fun Context.updateAllMyAppWidgetState(updateState: suspend (WidgetState) -> WidgetState){
    GlanceAppWidgetManager(this)
        .getGlanceIds(MyAppWidget::class.java).forEach{ glanceId ->
            updateAppWidgetState(
                context = this,
                definition = ScheduleGlanceStateDefinition(),
                glanceId = glanceId,
                updateState = updateState
            )
        }
    this.updateAllMyAppWidget()
}

suspend fun Context.updateMyAppWidgetState(glanceId: GlanceId, updateState: suspend (WidgetState) -> WidgetState){
    updateAppWidgetState(
        context = this,
        definition = ScheduleGlanceStateDefinition(),
        glanceId = glanceId,
        updateState = updateState
    )
    this.updateAllMyAppWidget()
}

suspend fun Context.updateAllMyAppWidget(){
    MyAppWidget().updateAll(this)
}