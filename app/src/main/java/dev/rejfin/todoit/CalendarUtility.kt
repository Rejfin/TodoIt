package dev.rejfin.todoit

import dev.rejfin.todoit.models.CalendarDay
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtility {
    private val sdfNumber = SimpleDateFormat("dd", Locale.getDefault())
    private val sdfName = SimpleDateFormat("EEE", Locale.getDefault())

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
        val cal: Calendar = Calendar.getInstance(Locale.UK)
        return cal.timeInMillis
    }

    fun timestampToDayNumber(timestamp:Long):String{
        return sdfNumber.format(timestamp)
    }

    fun timestampToDayName(timestamp:Long):String{
        return sdfName.format(timestamp)
    }
}