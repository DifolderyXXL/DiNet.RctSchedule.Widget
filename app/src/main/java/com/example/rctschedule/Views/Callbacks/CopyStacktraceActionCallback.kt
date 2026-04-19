package com.example.rctschedule.Views.Callbacks

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class CopyStacktraceActionCallback : ActionCallback {
    companion object{
        val STACKTRACE_KEY = ActionParameters.Key<String>("STACKTRACE_KEY")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val parameter = parameters[STACKTRACE_KEY]
            ?: return

        val appContext = context.applicationContext
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Stacktrace", parameter)

        clipboard.setPrimaryClip(clip)
    }
}