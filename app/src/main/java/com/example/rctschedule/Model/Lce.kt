package com.example.rctschedule.Model

import kotlinx.serialization.Serializable

@Serializable
sealed class Lce<out T> { // LCE: Loading, Content, Error
    object Loading : Lce<Nothing>()
    data class Content<T>(val data: T) : Lce<T>()
    data class Error(val throwable: Throwable) : Lce<Nothing>()

    fun contentOrNull(): T? = (this as? Content)?.data
}