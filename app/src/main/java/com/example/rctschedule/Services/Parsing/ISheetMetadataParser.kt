package com.example.rctschedule.Services.Parsing

import com.example.rctschedule.Data.ColumnArgument
import com.example.rctschedule.Data.primitives.DateRange
import com.example.rctschedule.Model.ScheduleMeta
import org.apache.poi.ss.usermodel.Sheet
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface ISheetMetadataParser{
    fun parse(sheet: Sheet) : ScheduleMeta
}

interface IMetadataSheetContext{
    fun getMetaGroupNameRow() : Int
    fun getMetaGroupNameColumn(group: Int): ColumnArgument
}
class MetadataSheetContext(private val groupNameRow: Int,
                           private val regularContext: ISheetRegularContext)
    : IMetadataSheetContext{
    override fun getMetaGroupNameRow(): Int {
        return groupNameRow
    }

    override fun getMetaGroupNameColumn(group: Int): ColumnArgument {
        return regularContext.getContentForGroupColumn(group)
    }
}


class MetaGroupNameParser @Inject constructor(){
    fun getName(sheet: Sheet, group: Int, context: IMetadataSheetContext?) : String?{
        if(context == null)
            return null

        try {
            val column = context.getMetaGroupNameColumn(group)
            val row = sheet.getRow(context.getMetaGroupNameRow())

            for(i in 0..column.colCount){
                val content = row.getCell(column.startCol+i)?.stringCellValue

                if(content != null)
                    return content
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
}

class RctSheetMetadataParser @Inject constructor() : ISheetMetadataParser{
    private val tableNameRegular = Regex("\\D*(?<fromDate>\\d*.\\d*)[^-]*-\\D*(?<toDate>\\d{2}.\\d{2})\\s*\\D*(?<weekNumber>\\d*)")

    override fun parse(sheet: Sheet): ScheduleMeta {
        val r = tableNameRegular.find(sheet.sheetName)
            ?: throw Exception("Can't parse sheet name")

        val fromDate = r.groups["fromDate"]
        val toDate = r.groups["toDate"]
        val weekNumber = r.groups["weekNumber"]
        if(fromDate == null || toDate == null || weekNumber == null)
            throw Exception("Can't parse sheet name")

        val formatter = DateTimeFormatter.ofPattern("dd.MM")

        val monthDayFrom = MonthDay.parse(fromDate.value, formatter)
        val fromLocal = monthDayFrom.atYear(LocalDate.now().year)

        val monthDayTo = MonthDay.parse(toDate.value, formatter)
        val toLocal = monthDayTo.atYear(LocalDate.now().year)

        val dateRange = DateRange(
            fromLocal,
            toLocal)
        val weekNumberInt = weekNumber.value.toInt()


        return ScheduleMeta(dateRange, weekNumberInt)
    }
}
