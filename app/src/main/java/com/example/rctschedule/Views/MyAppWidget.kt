package com.example.rctschedule.Views

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionParametersOf
import androidx.glance.text.Text
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.*
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.TextStyle
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Model.WidgetEntryPoint
import com.example.rctschedule.R
import com.example.rctschedule.Services.Repositories.*
import com.example.rctschedule.Services.Repositories.States.ApplicationSettings
import com.example.rctschedule.ViewModels.ScheduleUiState
import com.example.rctschedule.Views.Callbacks.*
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date


class MyAppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

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
                        .background(GlanceTheme.colors.widgetBackground)
                        .appWidgetBackground()
                        .cornerRadius(10.dp)
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
        SurfaceText("Last update at $dt")
    }

    @Composable
    private fun UpdateStateHeader(state: Lce<*>)
    {
        Row(GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){

            when(state) {
                is Lce.Content -> SurfaceText("Content")
                is Lce.Loading -> SurfaceText("Loading")
                is Lce.Error -> SurfaceText("Error")
            }
            Spacer(GlanceModifier.defaultWeight())
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
        val appSettings by ep.getAppSettingsRepository().valueFlow.collectAsState(
            ApplicationSettings.Default)

        Box(modifier = GlanceModifier.fillMaxSize().padding(4.dp))
        {
            Column(GlanceModifier
                .fillMaxSize()
                .padding(top = 5.dp)) {

                GroupHeader(appSettings.selectedGroup, ep.getGroupToggleRepository())
                UpdateStateHeader(state)

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
                        WeekSelectionView(week).ComposableDraw(GlanceModifier.height(70.dp))
                    }

                    else -> {
                        val scheduleState by ep.getScheduleDataRepository()
                            .scheduleState.collectAsState()
                        when (scheduleState) {
                            is Lce.Loading -> SurfaceText("Loading...")
                            is Lce.Error -> SurfaceText("Error loading schedule")
                            else -> SurfaceText("Day off")
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun GroupHeader(group: Int, toggle: GroupToggleRepository) {

        val isExpanded by toggle.valueFlow.collectAsState(false)

        Column(){
            SurfaceText("Group ${group + 1}",
                GlanceModifier.clickable(
                    actionRunCallback<ToggleDropdownAction>())
                    .background(GlanceTheme.colors.surface)
                    .padding(2.dp)
                    .cornerRadius(8.dp))


            if (isExpanded) {
                GroupGrid((0..9).toList())
            }
        }
    }
}


@Composable
fun GroupGrid(items: List<Int>) {
    val groups = (items).chunked(5)

    Column(modifier = GlanceModifier.fillMaxWidth()
        .cornerRadius(5.dp)) {
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

@Composable
public fun SurfaceText(text: String, modifier: GlanceModifier = GlanceModifier)
{
    Text(text,
        style = TextStyle(
            color = GlanceTheme.colors.onSurface),
        modifier = modifier)
}