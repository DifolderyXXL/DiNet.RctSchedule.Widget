package com.example.rctschedule.Di

import com.example.rctschedule.Services.Parsing.CourseParserProvider
import com.example.rctschedule.Services.Parsing.GapSheetRegularContext
import com.example.rctschedule.Services.Parsing.ICourseParserProvider
import com.example.rctschedule.Services.Parsing.IRegularSheetParser
import com.example.rctschedule.Services.Parsing.ISheetMetadataParser
import com.example.rctschedule.Services.Parsing.ISheetRegularContextProvider
import com.example.rctschedule.Services.Parsing.IWebApi
import com.example.rctschedule.Services.Parsing.RctSheetMetadataParser
import com.example.rctschedule.Services.Parsing.RctWebApi
import com.example.rctschedule.Services.Parsing.RegularSheetParser
import com.example.rctschedule.Services.Parsing.SheetRegularContextProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindParserModule {
    @Binds
    abstract fun webApi(webApi: RctWebApi): IWebApi

    @Binds
    abstract fun courseParserProvider(courseParserProvider: CourseParserProvider)
        : ICourseParserProvider

    @Binds
    abstract fun regularSheetParser(regularSheetParser: RegularSheetParser)
            : IRegularSheetParser

    @Binds
    abstract fun sheetMetadataParser(sheetMetadataParser: RctSheetMetadataParser)
            : ISheetMetadataParser
}

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    @Provides
    fun sheetRegularContextProvider() : ISheetRegularContextProvider {
        val provider = SheetRegularContextProvider()

        provider.addContext(GapSheetRegularContext(
            listOf("N"), 1, 10, 5, 100
        ))

        provider.addContext(GapSheetRegularContext(
            listOf("N"), 2, 10, 5, 100
        ))



        return provider
    }
}
