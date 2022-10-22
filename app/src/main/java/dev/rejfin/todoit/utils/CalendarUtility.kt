package dev.rejfin.todoit.utils

import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtility {
    private val sdfNumber = SimpleDateFormat("dd", Locale.getDefault())
    private val sdfName = SimpleDateFormat("EEE", Locale.getDefault())
    private val sdfDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val calendarInstance: Calendar = Calendar.getInstance(Locale.UK)

    fun getDaysInCurrentWeek(): List<CalendarDay>{
        val cal: Calendar = Calendar.getInstance(Locale.UK)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)

        val listOfDays = mutableListOf<CalendarDay>()
        for (i in 0..6) {
            val date = getDateFromTimeStamp(cal.time.time)
            listOfDays.add(CalendarDay(date, 0, timestampToDayName(cal.time.time)))
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

    private fun timestampToDayName(timestamp:Long?):String{
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

    private fun getDateFromTimeStamp(timestamp: Long): CustomDateFormat{
        calendarInstance.timeInMillis = timestamp

        return CustomDateFormat(
            calendarInstance[Calendar.YEAR],
            calendarInstance[Calendar.MONTH],
            calendarInstance[Calendar.DAY_OF_MONTH]
        )
    }

    fun timestampFromDate(year: Int, month: Int, day: Int): Long{
        val date: Date = sdfDate.parse("$day.$month.$year") as Date
        return date.time
    }

    fun areDateSame(date: CustomDateFormat, date2: CustomDateFormat):Boolean{
        return date.year == date2.year && date.month == date2.month && date.day == date2.day
    }
}