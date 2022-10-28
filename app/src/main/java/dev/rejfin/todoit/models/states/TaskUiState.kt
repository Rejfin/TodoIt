package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.models.ValidationResult

data class TaskUiState(
    val taskTitle: String = "",
    val taskTitleValidation: ValidationResult = ValidationResult(),
    val taskDescription: String = "",
    val taskDescriptionValidation: ValidationResult = ValidationResult(),
    val taskParts: List<TaskPartModel> = emptyList(),
    val startDate: CustomDateFormat = CustomDateFormat(),
    val endDate: CustomDateFormat = CustomDateFormat(),
    val isAllDay: Boolean = true,
    val difficulty: Int = 1,
    val timeConsuming: Int = 1,
    val xpForCompleteTask: Int = 10,
    val isDataSending: Boolean = false,
    val isDateSent: Boolean? = null,
    val taskErrorMessage: String? = null
)
