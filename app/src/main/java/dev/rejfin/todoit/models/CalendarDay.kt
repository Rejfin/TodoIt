package dev.rejfin.todoit.models

data class CalendarDay(
    var date: CustomDateFormat = CustomDateFormat(),
    var numberOfTasks: Int = 0,
    var dayName: String = ""
)
