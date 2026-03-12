package com.example.rctschedule

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.rctschedule.Views.MyAppWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tt = this
       setContent {
           MaterialTheme {
               Surface(modifier= Modifier.fillMaxSize()){

                   Column(verticalArrangement = Arrangement.Center) {
                       Button( onClick = {
                           // Принудительное обновление виджета
                           val appWidgetManager = AppWidgetManager.getInstance(tt)
                           val ids = appWidgetManager.getAppWidgetIds(
                               ComponentName(tt, ScheduleAppWidgetReceiver::class.java)
                           )

                           if (ids.isNotEmpty()) {
                               val intent = Intent(tt, ScheduleAppWidgetReceiver::class.java).apply {
                                   action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                   putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                               }
                               sendBroadcast(intent)
                               Log.d("TestActivity", "Обновление отправлено для ${ids.size} виджетов")
                           }
                       }){
                           Text("Update widgets")
                       }
                   }

               }
           }

       }
    }

    override fun onPause() {
        super.onPause()

        updateGlanceWidgets()
    }

    private fun updateGlanceWidgets() {
        val context = this

        CoroutineScope(Dispatchers.Default).launch {
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(MyAppWidget::class.java)

            ids.forEach { id ->
                MyAppWidget().update(context, id)
            }
            Log.d("TestActivity", "Glance update triggered for ${ids.size} widgets")
        }
    }
}