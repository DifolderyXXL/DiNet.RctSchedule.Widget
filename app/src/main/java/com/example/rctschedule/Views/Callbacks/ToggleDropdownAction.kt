package com.example.rctschedule.Views.Callbacks

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Views.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToggleDropdownAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)

        val repo = ep.getGroupToggleRepository()

        withContext(Dispatchers.Main) {
            repo.set(!repo.get())

            MyAppWidget().updateAll(context)
        }
    }
}
