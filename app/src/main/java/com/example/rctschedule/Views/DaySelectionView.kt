package com.example.rctschedule.Views

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider
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
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.round10dpBackground
import com.example.rctschedule.Views.Figures.round8dpBackground
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
        else ColorProvider( Color.Transparent, Color.Transparent)

        val foreground = if(state.isTodaySelected) GlanceTheme.colors.onPrimary
        else GlanceTheme.colors.onBackground


        Box(GlanceModifier.fillMaxWidth(),
            contentAlignment = Alignment.Center)
        {
            showBackgroundIf(state.isTodaySelected, color)
            {
                val context = LocalContext.current
                val locale = context.resources.configuration.locales[0]


                Text(state.selectedDay.getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { it.uppercase() },
                    style = androidx.glance.text.TextStyle(
                        color = foreground
                    )
                )
                if(state.isTodaySelected)
                    Text("(${context.getString(R.string.today).uppercase()})",
                        style = androidx.glance.text.TextStyle(
                            color = foreground
                        )
                    )
            }
        }
    }

    @Composable
    fun showBackgroundIf(statement: Boolean, color: ColorProvider, body: @Composable (() -> Unit) ){

        if(statement)
        {
            Row(GlanceModifier
                .round8dpBackground(color)
                .padding(horizontal = 8.dp)){
                body()
            }
        }
        else{
            Row(GlanceModifier
                .padding(horizontal = 8.dp)){
                body()
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

                    val context = LocalContext.current
                    val locale = context.resources.configuration.locales[0]

                    ButtonWithMarker(
                        color,
                        fg,
                        item.getDisplayName(TextStyle.SHORT, locale)
                            .replaceFirstChar { it.uppercase() },
                        currentDayOfWeek == item,
                        GlanceModifier
                            .fillMaxWidth()
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
fun ButtonWithMarker(background: ColorProvider,
                            foreground: ColorProvider,
                            text: String,
                            marker: Boolean,
                            modifier: GlanceModifier)
{
    Column(modifier
        .round10dpBackground(background)
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
fun ButtonWithoutMarker(background: ColorProvider,
                               foreground: ColorProvider,
                               text: String,
                               modifier: GlanceModifier)
{
    Column(modifier.round10dpBackground(background)){
        Box(
            contentAlignment = Alignment.Center) {
            Text(text,
                style = androidx.glance.text.TextStyle(
                    color = foreground,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
        }
    }
}