package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.TaskModel

data class HomeUiState(
    val loggedUser: String = "",
    val numberOfDoneTask:Int = 0,
    val numberOfAllTasks:Int = 0,
    val isDataLoading:Boolean = false,
    val errorMessage: String? = null,
    val showDetailsDialog: Boolean = false,
    val taskToShowDetails: TaskModel? = null
)
