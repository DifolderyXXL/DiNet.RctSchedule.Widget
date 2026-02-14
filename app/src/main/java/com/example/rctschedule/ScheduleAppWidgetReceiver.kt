package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ScheduleAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)



        // Логируем все входящие интенты для отладки
        println("🔥 Widget onReceive: action = ${intent.action}")

        when (intent.action) {
            "android.appwidget.action.APPWIDGET_UPDATE" -> {
                println("🔥 Received UPDATE broadcast")
                scope.launch {
                    glanceAppWidget.updateAll(context)
                }
            }
            "android.appwidget.action.APPWIDGET_ENABLED" -> {
                println("🔥 Widget enabled")
                scope.launch {
                    glanceAppWidget.updateAll(context)
                }
            }
            "android.appwidget.action.APPWIDGET_OPTIONS_CHANGED" -> {
                println("🔥 Widget options changed")
            }
            else -> {
                println("🔥 Other action: ${intent.action}")
                scope.launch {
                    glanceAppWidget.updateAll(context)
                }
            }
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        scope.cancel()
    }

}
