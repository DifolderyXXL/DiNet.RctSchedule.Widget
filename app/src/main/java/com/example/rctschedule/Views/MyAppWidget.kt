package com.example.rctschedule.Views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Model.WidgetEntryPoint
import com.example.rctschedule.R
import com.example.rctschedule.Services.Repositories.*
import com.example.rctschedule.ViewModels.ScheduleUiState
import com.example.rctschedule.Views.Callbacks.*
import com.example.rctschedule.Views.Figures.HorizontalSpacer
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.VerticalSpacer
import com.example.rctschedule.Views.Figures.button_round
import com.example.rctschedule.Views.Figures.calculateTextHeight
import com.example.rctschedule.Views.Figures.content_round
import com.example.rctschedule.Views.Figures.getLocalFontSize
import com.example.rctschedule.Views.Figures.round10dpBackground
import com.example.rctschedule.Views.Figures.round8dpBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date


class MyAppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        val entryPoint = WidgetEntry.get(context)

        val loader = entryPoint.getScheduleWidgetLoader()

        val initialState = withContext(Dispatchers.IO) {
            loader.getCurrentState()
        }

        val flow = loader.getScheduleFlow()

        CoroutineScope(Dispatchers.IO).launch {
            entryPoint.getScheduleDataRepository().loadSchedule(false)
        }

        provideContent {
            GlanceTheme()
            {
                val uiState by flow.collectAsState(initial = initialState)

                Column(
                    GlanceModifier
                        .fillMaxHeight()
                        .appWidgetBackground()
                        .round8dpBackground(GlanceTheme.colors.widgetBackground)
                ) {
                    Content(uiState, entryPoint)
                }
            }
        }

    }



    @Composable
    private fun LastUpdateTime(lastUpdate: Date)
    {
        val dtFormatter = DateTimeFormatter.ofLocalizedDateTime(
            FormatStyle.SHORT, FormatStyle.MEDIUM)
        val dt = dtFormatter.format(
            lastUpdate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )
        val context = LocalContext.current
        SurfaceText("${context.getString(R.string.last_update)} $dt")
    }

    @Composable
    private fun UpdateStateHeader(state: Lce<*>)
    {
        Row(
            verticalAlignment = Alignment.CenterVertically){

            val context = LocalContext.current

            val text = when(state) {
                is Lce.Content -> (context.getString(R.string.schedule_lce_content))
                is Lce.Loading -> (context.getString(R.string.schedule_lce_loading))
                is Lce.Error -> (context.getString(R.string.schedule_lce_error))
            }

            Text(text,
                maxLines = 1,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface))

            Image(
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                provider = ImageProvider(R.drawable.baseline_refresh_24),
                contentDescription = null,
                modifier = GlanceModifier.cornerRadius(5.dp)
                    .clickable(actionRunCallback<UpdateScheduleAction>())
            )
        }
    }


    @Composable
    private fun Content(uiState: ScheduleUiState, ep: WidgetEntryPoint)
    {
        val state by ep.getScheduleDataRepository().scheduleState.collectAsState()

        val contextProvider = ep.getSheetRegularContextProvider()
        val context = contextProvider.get(uiState.appSettings.selectedCourse)

        Box(modifier = GlanceModifier.fillMaxSize().padding(4.dp))
        {
            Column(GlanceModifier
                .fillMaxSize()
                .padding(top = 5.dp)) {

                GroupHeader(uiState.appSettings.selectedCourse,
                    contextProvider.getAllCourses(),
                    uiState.appSettings.selectedGroup,
                    (0..<context.getGroupCount()).toList(),
                    ep.getGroupToggleRepository(),
                    state)


                val day = ep.getDaySelectionPresenter()
                    .present(uiState.selectWeek, uiState.selectDay)
                DaySelectionView(day).ComposableDraw(GlanceModifier)

                when {
                    uiState.content != null -> {
                        LastUpdateTime(Date(uiState.content.timestamp))

                        WeekView(uiState.content)
                            .ComposableDraw(GlanceModifier.defaultWeight())

                        val week = ep.getWeekSelectionPresenter().present(
                            uiState,
                            uiState.selectWeek.selectedWeek.weekNumber
                        )
                        WeekSelectionView(week)
                            .ComposableDraw(GlanceModifier.height(70.dp))
                    }

                    else -> {
                        val scheduleState by ep.getScheduleDataRepository()
                            .scheduleState.collectAsState()

                        val context = LocalContext.current
                        when (scheduleState) {
                            is Lce.Loading -> SurfaceText(context.getString(R.string.loading))
                            is Lce.Error -> {
                                SurfaceText(context.getString(R.string.error_loading_schedule))
                                val error = (scheduleState as Lce.Error).throwable

                                if(error.message != null)
                                    SurfaceText("${error.message}")

                                val stacktraceText = context.getString(R.string.copy_stacktrace)
                                SurfaceText(stacktraceText,
                                    GlanceModifier.clickable {

                                        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(stacktraceText, error.stackTraceToString())

                                        clipboard.setPrimaryClip(clip)
                                    })
                            }
                            else -> SurfaceText(context.getString(R.string.day_off))
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun GroupHeader(course: Int, validCourses: List<Int>, group: Int, validGroups: List<Int>, toggle: GroupToggleRepository, lceState: Lce<*>) {
        val state by toggle.valueFlow.collectAsState(ToggleData.Default)

        Column(){
            HeaderRow(course, group, state, lceState)

            ExpandableSelector(validCourses, validGroups, state)
            HorizontalSpacer()
        }
    }

    @Composable
    fun HeaderRow(course: Int, group: Int, state: ToggleData, lceState: Lce<*>){
        Row(GlanceModifier.fillMaxWidth()) {
            var backgroundGroup = GlanceTheme.colors.secondaryContainer
            var backgroundCourse = GlanceTheme.colors.secondaryContainer

            var foregroundGroup = GlanceTheme.colors.onSecondaryContainer
            var foregroundCourse = GlanceTheme.colors.onSecondaryContainer

            if(state.isExpanded)
                when(state.window){
                    ToggleWindow.Groups -> {
                        backgroundGroup = GlanceTheme.colors.tertiary
                        foregroundGroup = GlanceTheme.colors.onTertiary
                    }
                    ToggleWindow.Courses -> {
                        backgroundCourse = GlanceTheme.colors.tertiary
                        foregroundCourse = GlanceTheme.colors.onTertiary
                    }
                }

            val context = LocalContext.current
            SelectionToggleButton("${context.getString(R.string.course)} $course",
                backgroundCourse,
                foregroundCourse,
                ToggleWindow.Courses)

            VerticalSpacer()

            SelectionToggleButton("${context.getString(R.string.group)} ${group + 1}",
                backgroundGroup,
                foregroundGroup,
                ToggleWindow.Groups)

            Spacer(GlanceModifier.defaultWeight())

            UpdateStateHeader(lceState)
        }
    }


    @Composable
    fun ExpandableSelector(validCourses: List<Int>, validGroups: List<Int>, state: ToggleData){
        if (state.isExpanded) {
            HorizontalSpacer()

            when(state.window){
                ToggleWindow.Groups -> {
                    GroupGrid(validGroups)
                }
                ToggleWindow.Courses -> {
                    CourseGrid(validCourses)
                }
            }
        }
    }

}

@Composable
fun SelectionToggleButton(
    text: String,
    background: ColorProvider,
    foreground: ColorProvider,
    toggleWindow: ToggleWindow)
{
    Text(text,
        style = TextStyle(
            color = foreground),
        modifier = GlanceModifier.clickable(
            actionRunCallback<ToggleDropdownAction>(
                actionParametersOf(ToggleDropdownAction.WINDOW_TOGGLE_KEY to toggleWindow)))
            .padding(5.dp, 2.dp)
            .round10dpBackground(background))

}



@Composable
fun CourseGrid(items: List<Int>) {
    val groups = (items).chunked(5)

    Column(modifier = GlanceModifier.fillMaxWidth()
        .cornerRadius(content_round)) {
        groups.forEach { rowItems ->
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                rowItems.forEach { i ->
                    Box(
                        modifier = GlanceModifier.padding(4.dp)
                            .background(if (i % 2 == 0) GlanceTheme.colors.inversePrimary else GlanceTheme.colors.primary)
                            .defaultWeight()
                            .clickable(
                                actionRunCallback<CourseSelectActionCallback>(
                                    parameters = actionParametersOf(
                                        CourseSelectActionCallback.SELECT_COURSE_BUTTON_KEY to i
                                    )
                                )),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            style = TextStyle(
                                color = if (i % 2 == 0) GlanceTheme.colors.onSurface else GlanceTheme.colors.onPrimary
                            ),
                            text = "${i}"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GroupGrid(items: List<Int>) {
    val groups = (items).chunked(5)

    Column(modifier = GlanceModifier.fillMaxWidth()
        .cornerRadius(content_round)) {
        groups.forEach { rowItems ->
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                rowItems.forEach { i ->
                    Box(
                        modifier = GlanceModifier.padding(4.dp)
                            .background(if (i % 2 == 0) GlanceTheme.colors.inversePrimary else GlanceTheme.colors.primary)
                            .defaultWeight()
                            .clickable(
                            actionRunCallback<GroupSelectActionCallback>(
                                parameters = actionParametersOf(
                                    GroupSelectActionCallback.SELECT_GROUP_BUTTON_KEY to i
                                )
                            )),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            style = TextStyle(
                                color = if (i % 2 == 0) GlanceTheme.colors.onSurface else GlanceTheme.colors.onPrimary
                            ),
                            text = "${i + 1}"
                        )
                    }
                }
            }
        }
    }
}

