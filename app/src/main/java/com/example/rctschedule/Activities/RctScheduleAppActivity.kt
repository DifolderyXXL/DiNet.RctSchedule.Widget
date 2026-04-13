package com.example.rctschedule.Activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.rctschedule.Activities.ui.NavigationDrawer
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Workers.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RctScheduleAppActivity : ComponentActivity() {
    @Inject
    lateinit var workerScheduler: WorkerScheduler

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkTheme = isSystemInDarkTheme()

            val colorScheme = if (darkTheme) {
                darkColorScheme()
            } else {
                lightColorScheme()
            }

            MaterialTheme(colorScheme) {
                Surface(modifier = Modifier.Companion.fillMaxSize()) {
                    NavigationDrawer(entryBuilders)
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