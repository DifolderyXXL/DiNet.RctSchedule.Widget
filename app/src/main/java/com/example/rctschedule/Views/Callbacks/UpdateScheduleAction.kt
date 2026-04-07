package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Model.WidgetEntry
import com.example.rctschedule.Workers.WorkerScheduler

class UpdateScheduleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WorkerScheduler(context.applicationContext)
            .forceRunNow()

        val ep = WidgetEntry.get(context.applicationContext)
        updateAndCloseMenu(context, ep.getGroupToggleRepository())
    }
}