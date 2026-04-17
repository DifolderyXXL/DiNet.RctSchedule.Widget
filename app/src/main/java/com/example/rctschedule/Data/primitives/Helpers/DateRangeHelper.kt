package com.example.rctschedule.Data.primitives.Helpers

import com.example.rctschedule.Data.primitives.DateRange
import java.time.LocalDate

class DateRangeHelper {
    companion object{
        public fun dateInRangeWithoutYear(date: LocalDate, dateRange: DateRange)
            : Boolean
        {
            val from = dateRange.from
            var to = dateRange.to

            if(to.isBefore(from))
            {
                to = to.plusYears(1)
            }

            return date in from..to
        }
    }
}