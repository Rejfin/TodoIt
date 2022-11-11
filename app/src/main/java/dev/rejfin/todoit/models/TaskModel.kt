package dev.rejfin.todoit.models

import java.io.Serializable

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
    val timestamp: Long = 0L,
    val endTimestamp: Long = 0L,
    val startTimestamp: Long = 0L,
    val timeConsuming: Int = 0,
    val difficulty: Int = 0
): Serializable
