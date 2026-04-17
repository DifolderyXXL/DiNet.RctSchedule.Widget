package com.example.rctschedule.Views.ViewStates

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.Di.entryPoints.WidgetEntryPoint
import com.example.rctschedule.R
import com.example.rctschedule.Services.Repositories.ToggleData
import com.example.rctschedule.Services.Repositories.ToggleWindow
import com.example.rctschedule.ViewModels.Targeted.CourseSelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.GroupSelectionViewModel
import com.example.rctschedule.ViewModels.Targeted.WidgetLce
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.ViewModels.Targeted.WidgetViewModel
import com.example.rctschedule.Views.Callbacks.CourseSelectActionCallback
import com.example.rctschedule.Views.Callbacks.GroupSelectActionCallback
import com.example.rctschedule.Views.Callbacks.UpdateScheduleAction
import com.example.rctschedule.Views.DaySelectionView
import com.example.rctschedule.Views.Figures.HorizontalSpacer
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.VerticalSpacer
import com.example.rctschedule.Views.Figures.content_round
import com.example.rctschedule.Views.Figures.round10dpBackground
import com.example.rctschedule.Views.WeekSelectionView
import com.example.rctschedule.Views.WeekView
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date


@Composable
fun WidgetViewModelView(viewModel: WidgetViewModel,
                        entryPoint: WidgetEntryPoint){
    Column(GlanceModifier.fillMaxSize()){
        val day = entryPoint.getDaySelectionPresenter()
            .present(viewModel.weekSelectionViewModel, viewModel.daySelectionViewModel)
        DaySelectionView(day).ComposableDraw(GlanceModifier)

        LastUpdateTime(Date(viewModel.contentViewModel.updateTimestamp))

        WeekView(viewModel.contentViewModel)
            .ComposableDraw(GlanceModifier.defaultWeight())

        val week = entryPoint.getWeekSelectionPresenter().present(
            viewModel.weekSelectionViewModel
        )
        WeekSelectionView(week)
            .ComposableDraw(GlanceModifier.height(70.dp))
    }
}

@Composable
fun ContentStateView(contentState: WidgetState.ContentState,
                     entryPoint: WidgetEntryPoint){
    Box(modifier = GlanceModifier.fillMaxSize().padding(4.dp))
    {
        Column(GlanceModifier
            .fillMaxSize()
            .padding(top = 5.dp)) {

            GroupHeader(contentState.groupSelectionViewModel,
                contentState.courseSelectionViewModel,
                contentState.widgetViewModel)


            val context = LocalContext.current
            when(contentState.widgetViewModel){
                is WidgetLce.Content -> WidgetViewModelView(contentState.widgetViewModel.data, entryPoint)
                is WidgetLce.Error -> {
                    SurfaceText(context.getString(R.string.error_loading_schedule))
                    val error = "${contentState.widgetViewModel.type}: ${contentState.widgetViewModel.message}"

                    SurfaceText(error)

                    val stacktraceText = context.getString(R.string.copy_stacktrace)
                    SurfaceText(stacktraceText,
                        GlanceModifier.clickable {

                            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(stacktraceText, contentState.widgetViewModel.stacktrace)

                            clipboard.setPrimaryClip(clip)
                        })
                }
                WidgetLce.Loading -> SurfaceText(context.getString(R.string.loading))
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
private fun UpdateStateHeader(state: WidgetLce)
{
    Row(
        verticalAlignment = Alignment.CenterVertically){

        val context = LocalContext.current

        val text = when(state) {
            is WidgetLce.Content -> (context.getString(R.string.schedule_lce_content))
            is WidgetLce.Loading -> (context.getString(R.string.schedule_lce_loading))
            is WidgetLce.Error -> (context.getString(R.string.schedule_lce_error))
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
fun GroupHeader(
    groupSelection: GroupSelectionViewModel,
    courseSelection: CourseSelectionViewModel,
    lceState: WidgetLce) {

    val toggle = remember{mutableStateOf(ToggleData.Default)}

    Column{
        HeaderRow(
            courseSelection.course,
            groupSelection.selected,
            toggle,
            lceState)

        ExpandableSelector(
            courseSelection.available,
            groupSelection.available,
            toggle.value)
        HorizontalSpacer()
    }
}

@Composable
fun HeaderRow(course: Int, group: Int, state: MutableState<ToggleData>, lceState: WidgetLce){
    Row(GlanceModifier.fillMaxWidth()) {
        var backgroundGroup = GlanceTheme.colors.secondaryContainer
        var backgroundCourse = GlanceTheme.colors.secondaryContainer

        var foregroundGroup = GlanceTheme.colors.onSecondaryContainer
        var foregroundCourse = GlanceTheme.colors.onSecondaryContainer

        if(state.value.isExpanded)
            when(state.value.window){
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
            state,
            ToggleWindow.Courses)

        VerticalSpacer()

        SelectionToggleButton("${context.getString(R.string.group)} ${group + 1}",
            backgroundGroup,
            foregroundGroup,
            state,
            ToggleWindow.Groups)

        Spacer(GlanceModifier.defaultWeight())

        UpdateStateHeader(lceState)
    }
}


@Composable
fun SelectionToggleButton(
    text: String,
    background: ColorProvider,
    foreground: ColorProvider,
    state: MutableState<ToggleData>,
    window: ToggleWindow)
{
    Text(text,
        style = TextStyle(
            color = foreground),
        modifier = GlanceModifier
            .padding(5.dp, 2.dp)
            .round10dpBackground(background)
            .clickable{
                val expanded = if(!state.value.isExpanded || state.value.window == window) !state.value.isExpanded else true
                state.value = ToggleData(
                    expanded,
                    window)
            })



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
