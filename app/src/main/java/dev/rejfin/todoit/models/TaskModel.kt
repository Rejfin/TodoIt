package dev.rejfin.todoit.models

data class TaskModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val taskParts: List<TaskPartModel> = emptyList(),
    val xpForTask: Int = 100,
    val isAllDay: Boolean = true,
    val startDate: CustomDateFormat = CustomDateFormat(),
    val endDate: CustomDateFormat = CustomDateFormat(),
    val isDone: Boolean = false
)
