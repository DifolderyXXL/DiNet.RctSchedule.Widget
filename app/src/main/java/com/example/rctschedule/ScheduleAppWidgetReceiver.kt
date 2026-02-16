package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.*
import com.example.rctschedule.Model.WidgetModelRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    @Inject
    lateinit var repository: WidgetModelRepository

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

}
