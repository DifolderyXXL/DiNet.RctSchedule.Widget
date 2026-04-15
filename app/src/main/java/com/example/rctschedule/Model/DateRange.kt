package com.example.rctschedule.Model

import java.time.LocalDate

class DateRange(val from: LocalDate, val to: LocalDate){
    constructor() : this(
        LocalDate.of(1970, 1, 1),
        LocalDate.of(1970, 1, 1)){}
}

/*
class DateRange{
    val from: Date
    val to: Date

    constructor() : this(Date(0), Date(0)){}

    constructor(from: Date, to: Date)
    {
        this.from = from
        this.to = to
    }

    constructor(from: LocalDate, to: LocalDate)
    {
        this.from = from.fromLocal()
        this.to = to
    }

    fun Date.toLocalDate(): LocalDate =
        this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()


    fun DateRange.contains(date: LocalDate): Boolean {
        val fromLocal = from.toLocalDate()
        val toLocal = to.toLocalDate()
        return date in fromLocal..toLocal
    }
}
 */