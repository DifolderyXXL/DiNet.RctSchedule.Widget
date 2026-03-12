package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.*
import com.example.rctschedule.Model.WidgetModelRepository
import com.example.rctschedule.Views.MyAppWidget
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class ScheduleAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    /*@Inject
    lateinit var repository: WidgetModelRepository*/

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)


        Log.e("onUpdate", "Update Received")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.e("Receiver", intent.action.toString())

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            Log.d("ScheduleAppWidgetReceiver", "Updating widgets: ${appWidgetIds?.joinToString()}")
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
}

