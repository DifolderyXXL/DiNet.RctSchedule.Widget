package com.example.rctschedule.ScheduleTheme

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider
import com.example.rctschedule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



@Immutable
data class ExcelThemeColors(
    val dark: Map<String, String>,
    val light: Map<String, String>,
) {
    fun getMappedColor(excelHex: String?, defaultProvider: ColorProvider): ColorProvider {
        if (excelHex == null) return defaultProvider

        // remain rgb
        val cleanHex = excelHex.removePrefix("FF").uppercase()

        val targetDark = dark[cleanHex]
        val targetLight = light[cleanHex]
        if(targetLight == null || targetDark==null)
            return defaultProvider

        val finalHex = if (targetDark.length == 6) "FF$targetDark" else targetDark
        val finalHexLight = if (targetLight.length == 6) "FF$targetLight" else targetLight
        return ColorProvider(Color(finalHexLight.toLong(16)), Color(finalHex.toLong(16)))
    }
}

val ScheduleTheme = staticCompositionLocalOf<ExcelThemeColors> {
    error("No theme provided")
}



@Composable
fun MyExcelAppTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val isDark = (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val currentMapping = remember(isDark){
        val jsonString =  context.resources.openRawResource(R.raw.schedule_theme_map)
            .bufferedReader()
            .use { it.readText() }
        val mapType = object : TypeToken<Map<String, Map<String, String>>>() {}.type
        val map: Map<String, Map<String, String>> = Gson().fromJson(jsonString, mapType)

        ExcelThemeColors(map["dark"]!!, map["light"]!!)
    }

    CompositionLocalProvider(ScheduleTheme provides currentMapping) {
        content()
    }

}