package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.rctschedule.Views.WidgetEntryPoint
import com.example.rctschedule.WorkerScheduler
import dagger.hilt.android.EntryPointAccessors

class UpdateScheduleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WorkerScheduler(context.applicationContext)
            .forceRunNow()

        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        updateAndCloseMenu(context, ep.getGroupToggleRepository())
    }
}