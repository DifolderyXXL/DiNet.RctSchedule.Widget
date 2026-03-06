package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Принудительное обновление виджета
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(this, ScheduleAppWidgetReceiver::class.java)
        )

        if (ids.isNotEmpty()) {
            val intent = Intent(this, ScheduleAppWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            sendBroadcast(intent)
            Log.d("TestActivity", "Обновление отправлено для ${ids.size} виджетов")
        }

        finish()
    }
}