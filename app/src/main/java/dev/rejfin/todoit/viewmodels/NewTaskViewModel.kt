package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.models.states.TaskUiState
import dev.rejfin.todoit.utils.CalendarUtility
import java.util.*

class NewTaskViewModel: ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    var calendarUtility: CalendarUtility
        private set

    private val database = Firebase.database
    private val dbRef = database.getReference("tasks")
    private val auth = FirebaseAuth.getInstance()
    private lateinit var userOrGroupId: String

    private var taskId: String? = null
    private var oldTimestamp: Long? = null

    fun setIdToSave(id: String?){
        userOrGroupId = id ?: auth.uid!!
    }

    init {
        calendarUtility = CalendarUtility()
        val currentDate = calendarUtility.getCurrentDate()
        taskUiState = taskUiState.copy(startDate = currentDate, endDate = currentDate.copy(hour = currentDate.hour + 1))
    }

    fun setUiState(taskModel: TaskModel){
        taskUiState = taskUiState.copy(
            taskTitle = taskModel.title,
            taskDescription = taskModel.description,
            taskParts = taskModel.taskParts,
            startDate = taskModel.startDate,
            endDate = taskModel.endDate,
            isAllDay = taskModel.allDay,
            timeConsuming = taskModel.timeConsuming,
            difficulty = taskModel.difficulty,
            xpForCompleteTask = taskModel.xpForTask,
        )

        taskId = taskModel.id
        oldTimestamp = taskModel.timestamp
    }

    fun updateTitle(title:String){
        taskUiState = taskUiState.copy(taskTitle = title)
    }

    fun updateDescription(description:String){
        taskUiState = taskUiState.copy(taskDescription = description)
    }

    fun addTaskPart(text: String){
        val newList = mutableListOf<TaskPartModel>()
        newList.addAll(taskUiState.taskParts)
        newList.add(TaskPartModel(false, text))
        taskUiState = taskUiState.copy(taskParts = newList)
    }

    fun updateTaskPart(index:Int, text: String){
        val taskList = taskUiState.taskParts.toMutableList()
        taskList[index] = taskList[index].copy(desc = text)
        taskUiState = taskUiState.copy(taskParts = taskList)
    }

    fun removeTaskPart(index:Int){
        val taskList = taskUiState.taskParts.toMutableList()
        taskList.removeAt(index)
        taskUiState = taskUiState.copy(taskParts = taskList)
    }

    fun updateDifficulty(level: Int){
        val xp = taskUiState.timeConsuming * 5 + taskUiState.difficulty * 5
        taskUiState = taskUiState.copy(difficulty = level, xpForCompleteTask = xp)
    }

    fun updateTimeConsuming(level: Int){
        val xp = taskUiState.timeConsuming * 5 + taskUiState.difficulty * 5
        taskUiState = taskUiState.copy(timeConsuming = level, xpForCompleteTask = xp)
    }

    fun updateIsAllDay(isAllDay: Boolean){
        taskUiState = taskUiState.copy(isAllDay = isAllDay)
    }

    fun updateStartHour(date: CustomDateFormat){
        taskUiState = taskUiState.copy(startDate = taskUiState.startDate.copy(hour = date.hour, minutes = date.minutes))
    }

    fun updateEndHour(date: CustomDateFormat){
        taskUiState = taskUiState.copy(endDate = taskUiState.endDate.copy(hour = date.hour, minutes = date.minutes))
    }

    fun updateDayOfTask(date: CustomDateFormat){
        taskUiState = taskUiState.copy(startDate = taskUiState.startDate.copy(year = date.year, month = date.month, day = date.day))
    }

    fun clearError(){
        taskUiState = taskUiState.copy(taskErrorMessage = null)
    }

    fun createTask(){
        taskUiState = taskUiState.copy(
            taskTitleValidation = ValidationResult(
                isError = taskUiState.taskTitle.isEmpty(),
                errorMessage = "Field can't be empty")
        )
        taskUiState = taskUiState.copy(
            taskDescriptionValidation = ValidationResult(
                isError = taskUiState.taskDescription.isEmpty(),
                errorMessage = "Field can't be empty")
        )

        if(!taskUiState.isAllDay){
            if(taskUiState.startDate.hour > taskUiState.endDate.hour){
                // hour of start is greater than end hour
                //TODO SHOW user info about that
                return
            }

            if(taskUiState.startDate.hour == taskUiState.endDate.hour){
                if(taskUiState.startDate.minutes < taskUiState.endDate.minutes){
                    // hour of start is greater than hour of end task
                    //TODO SHOW user info about that
                    return
                }
            }
        }

        if(taskUiState.taskTitleValidation.isError || taskUiState.taskDescriptionValidation.isError){
            return
        }

        //save only task parts with text inside
        val taskList = taskUiState.taskParts.filter { it.desc.isNotEmpty() }

        val timestamp = calendarUtility.timestampFromDate(taskUiState.startDate.year, taskUiState.startDate.month, taskUiState.startDate.day)

        val taskModel = TaskModel(
            id = taskId ?: UUID.randomUUID().toString(),
            title = taskUiState.taskTitle,
            description = taskUiState.taskDescription,
            taskParts = taskList,
            xpForTask = taskUiState.xpForCompleteTask,
            allDay = taskUiState.isAllDay,
            startDate = taskUiState.startDate,
            endDate = taskUiState.endDate,
            done = false,
            ownerId = auth.uid!!,
            groupId = if(userOrGroupId == auth.uid) null else userOrGroupId,
            timestamp = timestamp,
            timeConsuming = taskUiState.timeConsuming,
            difficulty = taskUiState.difficulty
        )

        if(timestamp != oldTimestamp){
            dbRef.child(userOrGroupId).child(oldTimestamp.toString()).child(taskModel.id).setValue(null)
        }

        dbRef.child(userOrGroupId).child(timestamp.toString()).child(taskModel.id).setValue(taskModel).addOnCompleteListener {
            taskUiState = if(it.isSuccessful){
                taskUiState.copy(isDataSending = false, isDateSent = true)
            }else{
                taskUiState.copy(isDataSending = false, isDateSent = false, taskErrorMessage = it.exception?.localizedMessage)
            }
        }
    }
}