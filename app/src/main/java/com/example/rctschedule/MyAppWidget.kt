package com.example.rctschedule

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.text.Text
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.TextStyle
import com.example.rctschedule.Model.FetchState
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.ViewModels.WidgetViewModel
import com.example.rctschedule.Views.WeekSelectionView
import com.example.rctschedule.Views.WeekView
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val vm = WidgetModelRepository.get(context).loadOrCreate()

        vm.scheduleRepository.loadSynchronously()

        provideContent {
            GlanceTheme()
            {
                Column(GlanceModifier
                    .fillMaxHeight()
                    .background(GlanceTheme.colors.widgetBackground)
                    .appWidgetBackground()) {

                    Content(vm)
                }
            }
        }
    }

    @Composable
    private fun FetchingStateContent(viewModel: WidgetViewModel)
    {
        val state by viewModel.fetchState.collectAsState(FetchState.Null)

        Box {
            when (state) {
                is FetchState.Null -> {
                    Text("Null",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                }

                is FetchState.Error -> {
                    Text("Error")
                }

                is FetchState.Fetching -> {
                    Text("Fetching")
                }

                is FetchState.Completed -> {
                    Text("Completed")
                }
            }
        }
    }

    @Composable
    private fun LastUpdateTime(viewModel: WidgetViewModel)
    {
        val lastUpdateDate by viewModel.lastUpdate.collectAsState()

        val dtFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)
        val dt = dtFormatter.format(
            lastUpdateDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )

        SurfaceText("Last update at $dt")

    }

    @Composable
    private fun UpdateStateHeader(viewModel: WidgetViewModel)
    {
        Row(GlanceModifier.fillMaxWidth()){
            FetchingStateContent(viewModel)
            Spacer(modifier = GlanceModifier.defaultWeight())

            Image(
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                provider = ImageProvider(R.drawable.baseline_refresh_24),
                contentDescription = null,
                modifier = GlanceModifier.cornerRadius(5.dp).clickable{
                    viewModel.forceUpdateCommand()
                }
            )
        }
    }


    @Composable
    private fun Content(viewModel: WidgetViewModel)
    {
        Box(modifier = GlanceModifier.fillMaxSize().padding(8.dp))
        {
            Column(GlanceModifier
                .fillMaxSize()
                .padding(top = 5.dp)) {

                GroupHeader(viewModel)

                LastUpdateTime(viewModel)

                UpdateStateHeader(viewModel)

                WeekView(viewModel.weekViewModel).ComposableDraw(
                    GlanceModifier.defaultWeight())

                WeekSelectionView(viewModel.selectionViewModel).ComposableDraw(
                    GlanceModifier.height(70.dp))
            }
        }
    }

    @Composable
    fun GroupHeader(viewModel: WidgetViewModel) {
        var expanded by remember { mutableStateOf(false) }

        val group by viewModel.group.collectAsState()
        val displaying by viewModel.displayingGroup.collectAsState()

        Column(){
            SurfaceText("Displaying Group ${displaying + 1}")

            SurfaceText("Group ${group + 1}",
                GlanceModifier.clickable{expanded = !expanded}
                    .background(GlanceTheme.colors.surface)
                    .padding(2.dp)
                    .cornerRadius(8.dp))
            if (expanded) {
                LazyColumn{
                    items(10){ i ->
                        Box(GlanceModifier
                            .background(if(i%2==0) GlanceTheme.colors.inversePrimary else GlanceTheme.colors.primary)
                            .clickable{
                                expanded = !expanded
                                viewModel.setGroup(i)
                            }){
                            SurfaceText("${i+1}", GlanceModifier.fillMaxWidth())
                        }
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
