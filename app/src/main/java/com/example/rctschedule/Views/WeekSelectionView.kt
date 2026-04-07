package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import com.example.rctschedule.Model.ScheduleMeta
import com.example.rctschedule.ViewModels.WeekSelectionState
import com.example.rctschedule.Views.Callbacks.WeekSelectActionCallback
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeekSelectionView(val weekSelectionState: WeekSelectionState) : GlanceView{
    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {
        Row(GlanceModifier.padding(top = 4.dp))
        {
            weekSelectionState.availableWeeksIds.forEach{ item ->
                SelectionItem(item, item.weekNumber == weekSelectionState.selectedWeekId)
            }
        }
    }

    @Composable
    private fun SelectionItem(data: ScheduleMeta, selected: Boolean)
    {
        val boxColor = if (selected) GlanceTheme.colors.tertiary else GlanceTheme.colors.secondaryContainer
        val textColor = if (selected) GlanceTheme.colors.onTertiary else GlanceTheme.colors.onSecondaryContainer

        Box {

            val formatter = DateTimeFormatter.ofPattern("dd.MM")

            ButtonWithoutMarker(
                boxColor,
                textColor,
                "${formatter.format(data.dateRange.from)}-${
                    formatter.format(
                        data.dateRange.to
                    )
                } (${data.weekNumber}-week)",
                GlanceModifier
                    .clickable(actionRunCallback<WeekSelectActionCallback>(
                        parameters = actionParametersOf(
                            WeekSelectActionCallback.SELECT_WEEK_BUTTON_KEY to data.weekNumber)
                    ))
                    .cornerRadius(8.dp)
                    .padding(vertical = 4.dp)
            )
        }
    }

}

