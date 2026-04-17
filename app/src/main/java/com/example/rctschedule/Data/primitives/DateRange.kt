package com.example.rctschedule.Data.primitives

import java.time.LocalDate

class DateRange(val from: LocalDate, val to: LocalDate){
    constructor() : this(
        LocalDate.of(1970, 1, 1),
        LocalDate.of(1970, 1, 1)){}
}