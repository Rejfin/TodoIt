package dev.rejfin.todoit.utils

import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtility {
    private val sdfNumber = SimpleDateFormat("dd", Locale.getDefault())
    private val sdfName = SimpleDateFormat("EEE", Locale.getDefault())
    private val calendarInstance: Calendar = Calendar.getInstance(Locale.UK)

    fun getDaysInCurrentWeek(): List<CalendarDay>{
        val cal: Calendar = Calendar.getInstance(Locale.UK)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)

        val listOfDays = mutableListOf<CalendarDay>()
        for (i in 0..6) {
            listOfDays.add(CalendarDay(cal.time.time, 0))
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }
        return listOfDays
    }

    fun getCurrentTimeStamp():Long{
        return calendarInstance.timeInMillis
    }

    fun timestampToDayNumber(timestamp:Long?):String{
        if(timestamp == null) return "-"
        return sdfNumber.format(timestamp)
    }

    fun timestampToDayName(timestamp:Long?):String{
        if(timestamp == null) return "-"
        return sdfName.format(timestamp)
    }

    fun getCurrentDate():CustomDateFormat{
        return CustomDateFormat(
            year = calendarInstance[Calendar.YEAR],
            month = calendarInstance[Calendar.MONTH],
            day = calendarInstance[Calendar.DAY_OF_MONTH],
            hour = calendarInstance[Calendar.HOUR],
            minutes = calendarInstance[Calendar.MINUTE]
        )
    }
}