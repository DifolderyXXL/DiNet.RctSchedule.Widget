package com.example.rctschedule

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.action.clickable
import androidx.glance.text.TextStyle
import com.example.rctschedule.Model.FetchState
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Model.WidgetViewModel
import com.example.rctschedule.Views.WeekSelectionView
import com.example.rctschedule.Views.WeekView

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val vm = WidgetModelRepository.get(context).loadOrCreate()

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
                UpdateStateHeader(viewModel)
                WeekView(viewModel.weekViewModel).ComposableDraw(
                    GlanceModifier.defaultWeight())

                WeekSelectionView(viewModel.selectionViewModel).ComposableDraw(
                    GlanceModifier.height(70.dp))
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
