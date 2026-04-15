package com.example.rctschedule.Views.Figures

import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import com.example.rctschedule.TransformExcelColumn
import com.example.rctschedule.Views.WeekTableCalculation.CalculatedCell

@Composable
fun getLocalFontSize() : Float{
    val fontScale = LocalContext.current.resources.configuration.fontScale
    val baseFontSizeSp = 14f
    val actualFontSize = (baseFontSizeSp * fontScale)
    return actualFontSize
}

fun calculateTextHeight(
    text: String,
    widthDp: Int,
    fontSizeSp: Float,
    context: Context
): Int {
    val density = context.resources.displayMetrics.density
    val widthPx = (widthDp * density).toInt()

    val textPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = fontSizeSp * density
    }

    val staticLayout = StaticLayout.Builder
        .obtain(text, 0, text.length, textPaint, widthPx)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(0f, 1f)
        .setIncludePad(true)
        .build()

    return (staticLayout.height / density).toInt()
}

fun mapToCalculatedCells(c: TransformExcelColumn, widthDp: Int, fontSizeSp: Float,
                          context: Context) : List<CalculatedCell>{
    return c.rows.map {
        CalculatedCell(it, widthDp, calculateTextHeight(it.value, widthDp, fontSizeSp, context))
    }
}