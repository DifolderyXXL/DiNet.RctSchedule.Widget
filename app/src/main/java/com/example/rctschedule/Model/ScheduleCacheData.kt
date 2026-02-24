package com.example.rctschedule.Model

import java.util.Date

data class CacheEntry<T>(val data: T, val timestamp: Long)

data class DateRange(val from: Date, val to: Date){
    constructor() : this(Date(0), Date(0)){}
}