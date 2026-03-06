package com.example.rctschedule.Views

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

import com.example.rctschedule.Model.ScheduleWeekData
import com.example.rctschedule.R
import com.example.rctschedule.SurfaceText
import com.example.rctschedule.ViewModels.DaySelectionViewModel
import com.example.rctschedule.ViewModels.SelectionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class WeekSelectionView(val viewModel: SelectionViewModel) : GlanceView{
    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {
        val selection by viewModel.selectionList.collectAsState()
        val selectedIndex by viewModel.selectedIndex.collectAsState()

        Row()
        {
            for(i in 0 until selection.size)
            {
                SelectionItem(selection[i], i == selectedIndex, i)
            }
        }
    }

    @Composable
    private fun SelectionItem(data: ScheduleWeekData, selected: Boolean, index: Int)
    {
        val boxColor = if (selected) GlanceTheme.colors.primary else GlanceTheme.colors.inversePrimary
        val textColor = if (selected) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onPrimary

        Box(GlanceModifier
            .background(boxColor)
            .cornerRadius(8.dp)
            .clickable {
                viewModel.Select(index)
            }
        ){
            val formatter = SimpleDateFormat("dd.MM", Locale.US)
            Text(
                "${formatter.format(data.meta.dateRange.from)}-${
                    formatter.format(
                        data.meta.dateRange.to
                    )
                } (${data.meta.weekNumber}-week)",
                style = TextStyle(
                    color = textColor
                )
            )
        }

    }

}

class DaySelectionView(val viewModel: DaySelectionViewModel) : GlanceView {
    @Composable
    override fun ComposableDraw(modifier: GlanceModifier) {
        Column(){
            ControlHeader()
            DayInfo()
        }
    }

    @Composable
    private fun DayInfo()
    {
        val day by viewModel.selectedDayOfWeek.collectAsState()
        val isToday by viewModel.isToday.collectAsState()

        Row()
        {
            SurfaceText(day.toString())

            if(isToday)
                SurfaceText("(Is today)")
        }
    }

    @Composable
    private fun IconButton(modifier: GlanceModifier, resId: Int)
    {
        Image(
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onTertiary),
            provider = ImageProvider(resId),
            contentDescription = null,
            modifier = modifier.cornerRadius(5.dp)
                .width(60.dp)
                .background(GlanceTheme.colors.tertiary)

        )
    }

    @Composable
    private fun ControlHeader()
    {
        Row(GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally)
        {
            IconButton(GlanceModifier.clickable{
                viewModel.previousDay()
            }, R.drawable.outline_arrow_back_24)

            Spacer(GlanceModifier.width(5.dp))

            IconButton(GlanceModifier.clickable{
                viewModel.currentDayOfWeek()
            }, R.drawable.outline_api_24)


            Spacer(GlanceModifier.width(5.dp))

            IconButton(GlanceModifier.clickable{
                viewModel.nextDay()
            }, R.drawable.outline_arrow_forward_24)
        }
    }
}