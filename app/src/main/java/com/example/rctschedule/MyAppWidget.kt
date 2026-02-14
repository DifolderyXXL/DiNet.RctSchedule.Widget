package com.example.rctschedule

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.Button
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import com.example.rctschedule.Services.ExcelCell
import com.example.rctschedule.Services.ExcelTable
import com.example.rctschedule.Services.ExcelTableColumns
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.math.max

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        var result : TransformExcelTable? = null
        try {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("widget_data_${id}", null)
            val dr = if (json != null) {
                Gson().fromJson(json, ExcelTable::class.java)
            } else null

            if(dr != null)
                result = TransformTable(dr)

            Log.e("LOGSCHEDULE", "Sub on ${id}; json ${json}")
        }
        catch (e: Exception){
            Log.e("LOGSCHEDULE", e.toString())
        }


        provideContent {
            Log.e("LOGSCHEDULE", "Produce")
            if (result == null) {
                MyContent(id)
            } else {
                TableView(context, result)
            }
        }
    }

    @Composable
    private fun MyContent(id: GlanceId) {
        Log.e("LOGSCHEDULE", "Redraw")
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = "Home",
                    onClick = actionRunCallback<UpdateWidgetDataWorker>()
                )



            }
        }
    }


    @Composable
    fun TableView(context: Context, pr: TransformExcelTable)
    {
        Column(modifier = GlanceModifier.fillMaxWidth())
        {
            Button(
                text = "Home",
                onClick = actionRunCallback<UpdateWidgetDataWorker>()
            )
            Button(
                text = "update",
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        MyAppWidget().updateAll(context)
                    }
                }
            )


            LazyColumn()
            {
                items(items = pr.rows) { item ->
                    Column {
                        RowView(item)
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }



    @Composable
    fun RowView(c: TransformExcelRow)
    {
        Row(modifier = GlanceModifier.fillMaxWidth().wrapContentHeight().background(Color.Green))
        {
            var i = 0
            c.columns.forEach { message ->
                if(i == 2)
                    ColumnView(message, GlanceModifier.defaultWeight())
                else
                    ColumnView(message, GlanceModifier.width(50.dp))

                Spacer(modifier = GlanceModifier.width(8.dp))
                i++
            }
        }
    }

    @Composable
    fun ColumnView(c: TransformExcelColumn, modifier: GlanceModifier = GlanceModifier)
    {
        Column(modifier.fillMaxHeight()){
            c.rows.forEach { message ->
                CellView(message, GlanceModifier
                    .defaultWeight())
                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }
    }

    @Composable
    fun CellView(c: ExcelCell, modifier: GlanceModifier = GlanceModifier)
    {
        Column(modifier
                .fillMaxWidth()
                .background(if (c.isMerged) Color.Red else Color.Gray)
        )
        {
            Text(text = c.value + "\n(${c.rowSpan}, ${c.colSpan})")
        }
    }

}

public fun CombineTableColumns(table: ExcelTableColumns)
    :ExcelTable
{
    val res = ArrayList<ArrayList<ExcelCell>>()

    val rowCount: Int = table.columns[0].totalRows
    var colCount: Int = 0

    for(i in table.columns)
        colCount += i.totalCols

    for(i in 0 until rowCount)
    {
        var row = ArrayList<ExcelCell>()

        for(t in table.columns)
        {
            for(r in t.rows[i])
            {
               row.add(r)
            }
        }

        res.add(row)
    }

    return ExcelTable(res, rowCount, colCount)
}

public fun TransformTable(table: ExcelTable) : TransformExcelTable
{
    val rows = ArrayList<TransformExcelRow>()
    var r = 0
    while(r < table.totalRows)
    {
        var maxH = 1
        for(c in 0 until table.totalCols)
        {
            val cell = table.rows[r][c]
            maxH = max(maxH, cell.rowSpan)
        }
        val cols = ArrayList<TransformExcelColumn>()

        var tm = 0
        var c = 0
        while(c < table.totalCols)
        {
            val col = ArrayList<ExcelCell>()

            var maxW = 1
            for(e in 0 until maxH - table.rows[r][c].rowSpan+1)
            {
                if(r+e >= table.totalRows)
                    break

                col.add(table.rows[r+e][c])
                maxW = max(maxW, table.rows[r+e][c].colSpan)
            }

            tm = max(tm, col.count())
            cols.add(TransformExcelColumn(maxW, col))
            c += maxW
        }

        rows.add(TransformExcelRow(maxH, cols))
        r+=maxH
    }

    return TransformExcelTable(rows)
}


data class TransformExcelTable(
    val rows: List<TransformExcelRow>
)

data class TransformExcelColumn(
    val width: Int,
    val rows: List<ExcelCell>
)

data class TransformExcelRow(
    val height: Int,
    val columns: List<TransformExcelColumn>
)
