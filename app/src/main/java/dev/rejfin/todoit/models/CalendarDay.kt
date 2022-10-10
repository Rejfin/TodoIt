package dev.rejfin.todoit.models

data class CalendarDay(
    var timestamp: Long,
    var numberOfTasks: Int = 0
)
