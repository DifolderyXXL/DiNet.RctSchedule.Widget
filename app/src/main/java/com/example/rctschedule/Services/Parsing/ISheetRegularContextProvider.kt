package com.example.rctschedule.Services.Parsing

interface ISheetRegularContextProvider{
    fun get(course: Int): ISheetRegularContext
    fun getAllCourses(): List<Int>
}


class SheetRegularContextProvider: ISheetRegularContextProvider{
    private val contexts = mutableMapOf<Int, ISheetRegularContext>()

    fun addContext(context: ISheetRegularContext){
        if(contexts.containsKey(context.getCourse()))
            throw Exception()
        contexts[context.getCourse()] = context
    }

    override fun get(course: Int): ISheetRegularContext {
        return contexts[course]
            ?: throw Exception("Context not found for course $course")
    }

    override fun getAllCourses(): List<Int> {
        return contexts.keys.toList()
    }
}

