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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Workers.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    @Inject lateinit var workerScheduler: WorkerScheduler

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


                       var text by remember{ mutableStateOf("Null") }

                       Button(onClick = {
                           val s = workerScheduler.getWidgetUpdateWorkerStatus()
                           val info = s.joinToString { x -> x.toString() }
                           text = info;
                       }){
                           Text("Get Full Info")
                       }

                       Button(onClick = {
                           val s = workerScheduler.getWidgetUpdateWorkerStatus()
                           val info = s.joinToString { x ->
                               val instant = Instant.ofEpochMilli(x.nextScheduleTimeMillis)

                               val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                               val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                               x.stopReason

                               dateTime.format(formatter) + '\n'
                           }
                           text = info;
                       }){
                           Text("Get Next Schedule Info")
                       }
                       Text(text)


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