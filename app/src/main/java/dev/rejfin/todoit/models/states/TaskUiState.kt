package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.models.ValidationResult

class TaskUiState{
    var taskTitle by mutableStateOf("")
    var taskTitleValidation by mutableStateOf(ValidationResult())
    var taskDescription by mutableStateOf("")
    var taskDescriptionValidation by mutableStateOf(ValidationResult())
    var taskParts = mutableStateListOf<TaskPartModel>()
    var startDate by mutableStateOf(CustomDateFormat())
    var endDate by mutableStateOf(CustomDateFormat())
    var isAllDay by mutableStateOf(true)
    var difficulty by mutableStateOf(1)
    var timeConsuming by mutableStateOf(1)
    var xpForCompleteTask by mutableStateOf(10)
    var isDataSending by mutableStateOf(false)
    var isDateSent by mutableStateOf<Boolean?>(null)
    var taskErrorMessage by mutableStateOf<String?>(null)
}
