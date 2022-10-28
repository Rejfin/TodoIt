package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.UserModel

data class GroupDetailUiState(
    val groupId: String = "",
    val groupName:String = "",
    val groupImage:String? = null,
    val groupMembers: List<UserModel> = emptyList(),
    val errorMessage: String? = null,
    val numberOfDoneTask:Int = 0,
    val numberOfAllTasks:Int = 0,
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedTaskList: List<TaskModel> = emptyList(),
    val allTaskList: Map<Long, List<TaskModel>> = emptyMap()
)
