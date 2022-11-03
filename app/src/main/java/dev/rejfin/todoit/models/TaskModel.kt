package dev.rejfin.todoit.models

data class TaskModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val taskParts: List<TaskPartModel> = emptyList(),
    val xpForTask: Int = 10,
    val allDay: Boolean = true,
    val startDate: CustomDateFormat = CustomDateFormat(),
    val endDate: CustomDateFormat = CustomDateFormat(),
    val done: Boolean = false,
    val ownerId: String = "",
    val groupId: String? = null,
    val timestamp: Long = 0L
)
