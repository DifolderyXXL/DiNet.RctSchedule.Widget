package com.example.rctschedule.Views

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import com.example.rctschedule.Di.entryPoints.WidgetEntry
import com.example.rctschedule.Services.Repositories.createJsonSerializer
import com.example.rctschedule.ViewModels.Targeted.WidgetState
import com.example.rctschedule.Views.Figures.round8dpBackground
import com.example.rctschedule.Views.ViewStates.ContentStateView
import com.example.rctschedule.Views.ViewStates.ErrorStateView
import com.example.rctschedule.Views.ViewStates.LoadingStateView
import kotlinx.serialization.json.Json
import java.io.File


class ScheduleGlanceStateDefinition : GlanceStateDefinition<WidgetState>{
    private val DATA_STORE_PREFIX = "schedule_widget_state_"


    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetState> {
        return DataStoreFactory.create(
            createJsonSerializer<WidgetState>(
                WidgetState.Empty,
                json = Json),
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

        provideContent {
            GlanceTheme()
            {
                val state = currentState<WidgetState>()

                Column(
                    GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .round8dpBackground(GlanceTheme.colors.widgetBackground)
                ) {

                    when(state){
                        is WidgetState.Content -> ContentStateView(state, entryPoint)
                        WidgetState.Empty -> LoadingStateView()
                    }
                }
            }
        }

    }
}