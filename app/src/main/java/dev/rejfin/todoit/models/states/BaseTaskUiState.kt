package dev.rejfin.todoit.models.states

import androidx.compose.runtime.*
import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel

open class BaseTaskUiState(
    calendarDays: MutableList<CalendarDay> = mutableListOf(),
    selectedTaskList: List<TaskModel> = emptyList(),
    allTaskList: MutableMap<Long, MutableList<TaskModel>> = mutableMapOf(),
    var userId: String = "",
    numberOfAllTasks: Int = 0,
    numberOfDoneTask: Int = 0,
    selectedDate: CustomDateFormat = CustomDateFormat(),
    showDetailsDialog: Boolean = false,
    errorMessage: String? = null,
    infoMessage: String? = null,
    taskToShowDetails: TaskModel? = null,
    var groupId: String? = null
){
    var calendarDays = mutableStateListOf<CalendarDay>()
    var selectedTaskList = mutableStateListOf<TaskModel>()
    var allTaskList = mutableStateMapOf<Long, MutableList<TaskModel>>()
    var errorMessage by mutableStateOf(errorMessage)
    var infoMessage by mutableStateOf(infoMessage)
    var showDetailsDialog by mutableStateOf(showDetailsDialog)
    var taskToShowDetails by mutableStateOf(taskToShowDetails)
    var selectedDate by mutableStateOf(selectedDate)
    var numberOfAllTasks by mutableStateOf(numberOfAllTasks)
    var numberOfDoneTask by mutableStateOf(numberOfDoneTask)
    var isDataLoading by mutableStateOf(false)

    init {
        this.calendarDays.addAll(calendarDays)
        this.selectedTaskList.addAll(selectedTaskList)
        allTaskList.forEach {
            this.allTaskList[it.key] = it.value
        }
    }
}


