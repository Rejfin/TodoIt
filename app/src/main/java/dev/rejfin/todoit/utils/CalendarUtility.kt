package dev.rejfin.todoit.utils

import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtility {
    private val sdfName by lazy {SimpleDateFormat("EEE", Locale.getDefault())}
    private val sdfDate by lazy {SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())}
    private val sdf2Date by lazy {SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())}
    private val sdfHour by lazy {SimpleDateFormat("HH:mm", Locale.getDefault())}
    private val calendarInstance: Calendar = Calendar.getInstance(Locale.getDefault())
    private val now by lazy { Date().time }

    fun getDaysInCurrentWeek(): List<CalendarDay>{
        val cal: Calendar = Calendar.getInstance(Locale.UK)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)

        val listOfDays = mutableListOf<CalendarDay>()
        for (i in 0..6) {
            val date = getDateFromTimeStamp(cal.time.time)
            listOfDays.add(CalendarDay(date.copy(month = date.month + 1), 0, timestampToDayName(cal.time.time)))
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }
        return listOfDays
    }

    fun getCurrentTimestamp():Long{
        return System.currentTimeMillis()
    }

    private fun timestampToDayName(timestamp:Long?):String{
        if(timestamp == null) return "-"
        return sdfName.format(timestamp)
    }

    fun getCurrentDate():CustomDateFormat{
        return CustomDateFormat(
            year = calendarInstance[Calendar.YEAR],
            month = calendarInstance[Calendar.MONTH] + 1,
            day = calendarInstance[Calendar.DAY_OF_MONTH],
            hour = calendarInstance[Calendar.HOUR_OF_DAY],
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
        return date.time + calendarInstance.timeZone.rawOffset
    }

    fun timestampFromDate(date: CustomDateFormat, adding: Boolean = false): Long{
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(now)

        val mDate: Date = sdf2Date.parse("${date.day}.${date.month}.${date.year} ${date.hour}:${date.minutes}") as Date
        return if(adding) mDate.time else mDate.time + offsetFromUtc
    }

    fun areDateSame(date: CustomDateFormat, date2: CustomDateFormat):Boolean{
        return date.year == date2.year && date.month == date2.month && date.day == date2.day
    }

    fun timestampToHourString(timestamp: Long): String{
        return sdfHour.format(timestamp)
    }
}