package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.utils.CalendarUtility
import java.util.*

class NewTaskViewModel: ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    var calendarUtility: CalendarUtility
        private set

    private val database = Firebase.database
    private val dbRef = database.getReference("tasks")
    private val firebaseAuth = Firebase.auth

    init {
        calendarUtility = CalendarUtility()
        val currentDate = calendarUtility.getCurrentDate()
        taskUiState = taskUiState.copy(startDate = currentDate, endDate = currentDate.copy(hour = currentDate.hour + 1))
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
                return
            }

            if(taskUiState.startDate.hour == taskUiState.endDate.hour){
                if(taskUiState.startDate.minutes < taskUiState.endDate.minutes){
                    // hour of start is greater than hour of end task
                    return
                }
            }
        }

        if(taskUiState.taskTitleValidation.isError || taskUiState.taskDescriptionValidation.isError){
            return
        }

        if(firebaseAuth.currentUser == null){
            return
        }

        //save only task parts with text inside
        val taskList = taskUiState.taskParts.filter { it.desc.isNotEmpty() }

        val timestamp = calendarUtility.timestampFromDate(taskUiState.startDate.year, taskUiState.startDate.month, taskUiState.startDate.day)

        val taskModel = TaskModel(
            id = UUID.randomUUID().toString(),
            title = taskUiState.taskTitle,
            description = taskUiState.taskDescription,
            taskParts = taskList,
            xpForTask = taskUiState.xpForCompleteTask,
            allDay = taskUiState.isAllDay,
            startDate = taskUiState.startDate,
            endDate = taskUiState.endDate,
            done = false
        )

        dbRef.child(firebaseAuth.uid!!).child(timestamp.toString()).child(taskModel.id).setValue(taskModel).addOnCompleteListener {
            taskUiState = if(it.isSuccessful){
                taskUiState.copy(isDataSending = false, isDateSent = true)
            }else{
                taskUiState.copy(isDataSending = false, isDateSent = false, taskErrorMessage = it.exception?.localizedMessage)
            }
        }
    }
}