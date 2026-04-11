package com.example.rctschedule.Services.Parsing

import javax.inject.Inject

interface ICourseParserProvider{
    fun get(course: Int) : ICourseParser
}

class CourseParserProvider @Inject constructor(
    private val webApi: IWebApi,
    private val metadataParser: ISheetMetadataParser,
    private val regularSheetParser: IRegularSheetParser,
    private val contextProvider: ISheetRegularContextProvider
) : ICourseParserProvider{
    override fun get(course: Int): ICourseParser {
        val context = contextProvider.get(course)

        return CourseParser(
            context,
            regularSheetParser,
            metadataParser,
            webApi
        )
    }
}
