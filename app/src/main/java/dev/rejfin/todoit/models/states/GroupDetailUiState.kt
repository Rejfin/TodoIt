package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.GroupModel
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.UserModel

data class GroupDetailUiState(
    val groupData: GroupModel = GroupModel(),
    val errorMessage: String? = null,
    val numberOfDoneTask:Int = 0,
    val numberOfAllTasks:Int = 0,
    val calendarDays: MutableList<CalendarDay> = mutableListOf(),
    val selectedTaskList: List<TaskModel> = emptyList(),
    val allTaskList: MutableMap<Long, MutableList<TaskModel>> = mutableMapOf()
)