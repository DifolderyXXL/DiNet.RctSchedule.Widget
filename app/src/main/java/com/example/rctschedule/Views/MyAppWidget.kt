package com.example.rctschedule.Views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.glance.*
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import com.example.rctschedule.Di.entryPoints.WidgetEntryPoint
import com.example.rctschedule.Model.Lce
import com.example.rctschedule.R
import com.example.rctschedule.Services.Repositories.*
import com.example.rctschedule.ViewModels.ScheduleUiState
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.Views.Callbacks.*
import com.example.rctschedule.Views.Figures.HorizontalSpacer
import com.example.rctschedule.Views.Figures.SurfaceText
import com.example.rctschedule.Views.Figures.VerticalSpacer
import com.example.rctschedule.Views.Figures.content_round
import com.example.rctschedule.Views.Figures.round10dpBackground
import com.example.rctschedule.Views.Figures.round8dpBackground
import com.example.rctschedule.Views.ViewStates.ContentStateView
import com.example.rctschedule.Views.ViewStates.ErrorStateView
import com.example.rctschedule.Views.ViewStates.LoadingStateView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


class ScheduleGlanceStateDefinition : GlanceStateDefinition<WidgetState>{
    private val DATA_STORE_PREFIX = "schedule_widget_state_"

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetState> {
        return DataStoreFactory.create(
            createJsonSerializer<WidgetState>(WidgetState.Loading),
            produceFile = {getLocation(context, fileKey)}
        )
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {
        return context.dataStoreFile(DATA_STORE_PREFIX + fileKey.lowercase())
    }
}



class MyAppWidget : GlanceAppWidget() {

    override val stateDefinition = ScheduleGlanceStateDefinition()

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        val entryPoint = WidgetEntry.get(context)


        /*CoroutineScope(Dispatchers.IO).launch {
            entryPoint.getScheduleDataRepository().loadSchedule(false)
        }*/


        provideContent {
            GlanceTheme()
            {
                val state = currentState<WidgetState>()

                Column(
                    GlanceModifier
                        .fillMaxHeight()
                        .appWidgetBackground()
                        .round8dpBackground(GlanceTheme.colors.widgetBackground)
                ) {
                    //Content(uiState, entryPoint)

                    when(state){
                        is WidgetState.ContentState -> ContentStateView(state, entryPoint)
                        is WidgetState.Error -> ErrorStateView(state.error)
                        WidgetState.Loading -> LoadingStateView()
                    }

                }
            }
        }

    }
}