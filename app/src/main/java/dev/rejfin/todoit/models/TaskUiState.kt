package dev.rejfin.todoit.models

data class TaskUiState(
    val taskTitle: ValidationResult = ValidationResult(),
    val taskDescription: ValidationResult = ValidationResult(),
    val taskParts: List<Pair<Boolean, String>> = emptyList(),
    val startTimestamp: Long? = null,
    val stopTimeStamp: Long? = null,
    val isAllDay: Boolean = false,
    val difficulty: Int = 1,
    val timeConsuming: Int = 1,
    val xpForCompleteTask: Int = 0
)
