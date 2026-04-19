package com.example.rctschedule.Services.Parsing

import com.example.rctschedule.Data.dto.GroupExcelTableDTO
import com.example.rctschedule.Data.dto.GroupExcelWeeksDTO
import org.apache.poi.xssf.usermodel.XSSFWorkbook

interface ICourseParser{
    fun get(group: Int) : GroupExcelWeeksDTO?
}


class CourseParser(
    private val regularContext: ISheetRegularContext,
    private val regularSheetParser: IRegularSheetParser,
    private val metadataParser: ISheetMetadataParser,
    private val metaGroupNameParser: MetaGroupNameParser,
    private val webApi: IWebApi
) : ICourseParser{

    override fun get(group: Int): GroupExcelWeeksDTO? {
        var workbook: XSSFWorkbook? = null
        try{
            workbook = webApi.provideSheetForCourse(
                regularContext.getCourse())

            val weeks = arrayListOf<GroupExcelTableDTO>()
            for(i in 0 until workbook.numberOfSheets)
            {
                if(workbook.isSheetHidden(i)
                    || workbook.isSheetVeryHidden(i))
                    continue

                val sheet = workbook.getSheetAt(i)

                val name = metaGroupNameParser.getName(sheet, group, regularContext.getMetadataSheetContext())
                val meta = metadataParser.parse(sheet).copy(groupSpecificName = name)
                val table =  regularSheetParser.parse(sheet, group, regularContext)

                weeks.add(GroupExcelTableDTO(
                    table,
                    meta
                ))
            }

            workbook.close()

            return GroupExcelWeeksDTO(weeks, group)
        }
        catch (e: Exception){
            workbook?.close()
            return null
        }

    }
}