package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.*
import com.example.rctschedule.Model.WidgetModelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    @Inject
    lateinit var repository: WidgetModelRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)


        Log.e("onUpdate", "Update Recieved")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.e("Reciever", intent.action.toString())
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
}