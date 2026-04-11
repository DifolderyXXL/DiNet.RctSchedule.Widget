package com.example.rctschedule.Services.Parsing

import com.example.rctschedule.Model.GroupExcelTableDTO
import com.example.rctschedule.Model.GroupExcelWeeksDTO

interface ICourseParser{
    fun get(group: Int) : GroupExcelWeeksDTO
}


class CourseParser(
    private val regularContext: ISheetRegularContext,
    private val regularSheetParser: IRegularSheetParser,
    private val metadataParser: ISheetMetadataParser,
    private val webApi: IWebApi
) : ICourseParser{

    override fun get(group: Int): GroupExcelWeeksDTO {
        val workbook = webApi.provideSheetForCourse(
            regularContext.getCourse())

        val weeks = arrayListOf<GroupExcelTableDTO>()
        for(i in 0 until workbook.numberOfSheets)
        {
            if(workbook.isSheetHidden(i)
                || workbook.isSheetVeryHidden(i))
                continue

            val sheet = workbook.getSheetAt(i)

            val meta = metadataParser.parse(sheet)
            val table =  regularSheetParser.parse(sheet, group, regularContext)

            weeks.add(GroupExcelTableDTO(
                table,
                meta
            ))
        }

        workbook.close()

        return GroupExcelWeeksDTO(weeks, group)
    }
}