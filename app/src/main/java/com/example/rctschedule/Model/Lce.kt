package com.example.rctschedule.Model

import kotlinx.serialization.Serializable

@Serializable
sealed class Lce<out T> { // LCE: Loading, Content, Error
    @Serializable
    object Loading : Lce<Nothing>()
    @Serializable
    data class Content<T>(val data: T) : Lce<T>()
    @Serializable
    data class Error(
        val message: String,
        val type: String,
        val stacktrace: String? = null) : Lce<Nothing>(){
        constructor(e: Throwable) : this(
            message = e.message ?: "Unknown",
            type = e.javaClass.simpleName,
            stacktrace = e.stackTraceToString()
        )

    }

    fun contentOrNull(): T? = (this as? Content)?.data
}