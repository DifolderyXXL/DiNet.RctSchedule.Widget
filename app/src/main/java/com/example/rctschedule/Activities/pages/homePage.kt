package com.example.rctschedule.Activities.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.rctschedule.R
import com.example.rctschedule.ScheduleAppWidgetReceiver
import com.example.rctschedule.Views.MyAppWidget
import com.example.rctschedule.Workers.InitializeWidgetWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data object HomePage : NavKey

fun EntryProviderScope<NavKey>.homeContentEntryBuilder(){
    entry<HomePage>{
        HomePageContent()
    }
}

@Composable
fun HomePageContent(){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {   pinWidgetOnScreenAction(context, coroutineScope) }
            .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
            )
            Text(stringResource(R.string.add_widget),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

fun pinWidgetOnScreenAction(context: Context, coroutineScope: CoroutineScope) {
    coroutineScope.launch(Dispatchers.Main) {
        try {
            val manager = GlanceAppWidgetManager(context)

            val success = manager.requestPinGlanceAppWidget(
                receiver = ScheduleAppWidgetReceiver::class.java,
                preview = MyAppWidget()
            )

            Log.d("WidgetPin", "Request sent: $success")

        } catch (e: Exception) {
            Log.e("WidgetPin", "Crash: ${e.message}")
        }
    }
}
