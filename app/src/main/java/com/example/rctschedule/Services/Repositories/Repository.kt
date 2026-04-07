package com.example.rctschedule.Services.Repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class Repository<T: Any>(
    protected val defaultValue: T,
    private val initialize: Boolean = true,
){
    protected lateinit var _valueFlow : MutableStateFlow<T>
    val valueFlow : StateFlow<T>
        get() = _valueFlow

    protected fun initRepository() {
        val value = if (initialize) get() else defaultValue
        _valueFlow = MutableStateFlow(onInit(value))
    }

    open fun onInit(value: T) : T{
        return value
    }


    abstract fun get() : T

    abstract fun set(value: T)
}