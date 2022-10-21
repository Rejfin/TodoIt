package dev.rejfin.todoit.models

data class TaskUiState(
    val taskTitle: String = "",
    val taskTitleValidation: ValidationResult = ValidationResult(),
    val taskDescription: String = "",
    val taskDescriptionValidation: ValidationResult = ValidationResult(),
    val taskParts: List<Pair<Boolean, String>> = emptyList(),
    val startDate: CustomDateFormat = CustomDateFormat(),
    val endDate: CustomDateFormat = CustomDateFormat(),
    val isAllDay: Boolean = true,
    val difficulty: Int = 1,
    val timeConsuming: Int = 1,
    val xpForCompleteTask: Int = 10
)
