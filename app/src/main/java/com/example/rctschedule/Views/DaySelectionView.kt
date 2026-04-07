package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.R
import com.example.rctschedule.ViewModels.DaySelectionState
import com.example.rctschedule.Views.Callbacks.DaySelectActionCallback
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DaySelectionView(val state: DaySelectionState) : GlanceView {
    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {
        Column() {
            ControlHeader()
            DayInfo()
        }
    }

    @Composable
    private fun DayInfo()
    {
        val color = if(state.isTodaySelected) GlanceTheme.colors.primary
        else ColorProvider( Color.Transparent)

        val foreground = if(state.isTodaySelected) GlanceTheme.colors.onPrimary
        else GlanceTheme.colors.onBackground


        Row(GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally)
        {
            Row(GlanceModifier.background(color)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp))
            {
                Text(state.selectedDay.toString(),
                    style = androidx.glance.text.TextStyle(
                        color = foreground
                    )
                )
                if(state.isTodaySelected)
                    Text("(TODAY)",
                        style = androidx.glance.text.TextStyle(
                            color = foreground
                        )
                    )
            }
        }

    }

    @Composable
    private fun ControlHeader()
    {
        Row(
            GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        )
        {
            val currentDayOfWeek = state.currentDate.dayOfWeek
            state.validDays.forEach { item ->
                val color = when (item) {
                    //currentDayOfWeek -> GlanceTheme.colors.primary
                    state.selectedDay -> GlanceTheme.colors.tertiary
                    else -> GlanceTheme.colors.secondaryContainer
                }

                val fg = when (item) {
                    //currentDayOfWeek -> GlanceTheme.colors.onPrimary
                    state.selectedDay -> GlanceTheme.colors.onTertiary
                    else -> GlanceTheme.colors.onSecondaryContainer
                }

                Box(GlanceModifier
                    .defaultWeight()) {

                    ButtonWithMarker(
                        color,
                        fg,
                        item.getDisplayName(TextStyle.SHORT, Locale.ROOT),
                        currentDayOfWeek == item,
                        GlanceModifier
                            .fillMaxWidth()
                            .cornerRadius(10.dp)
                            .clickable( actionRunCallback<DaySelectActionCallback>(
                                actionParametersOf(
                                    DaySelectActionCallback.SELECT_DAY_BUTTON_KEY to SelectDayButtonType.ByIndex,
                                    DaySelectActionCallback.DAY_KEY to item
                                )))
                    )
                }
            }
        }
    }
}

@Composable
public fun ButtonWithMarker(background: ColorProvider,
                            foreground: ColorProvider,
                            text: String,
                            marker: Boolean,
                            modifier: GlanceModifier)
{
    Column(modifier
        .background(background)
        .padding(bottom = 8.dp)
    ){

        Row(GlanceModifier.height(8.dp)
            .padding(end = 4.dp)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.End,
            verticalAlignment = Alignment.Vertical.Top)
        {
            if(marker)
            {
                Box( GlanceModifier
                    .background(ImageProvider(R.drawable.baseline_circle_24),
                        colorFilter = ColorFilter.tint(ColorProvider(Color.Green)))
                    .height(8.dp)
                    .width(8.dp)
                ){ }
            }
        }

        Box(
            GlanceModifier.fillMaxWidth(),
            contentAlignment = Alignment.Center) {
            Text(text,
                style = androidx.glance.text.TextStyle(
                    color = foreground,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}


@Composable
public fun ButtonWithoutMarker(background: ColorProvider,
                               foreground: ColorProvider,
                               text: String,
                               modifier: GlanceModifier)
{
    Column(modifier
        .background(ImageProvider(R.drawable.border_only),
            colorFilter = ColorFilter.tint(background))
    ){
        Box(
            contentAlignment = Alignment.Center) {
            Text(text,
                style = androidx.glance.text.TextStyle(
                    color = foreground,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}