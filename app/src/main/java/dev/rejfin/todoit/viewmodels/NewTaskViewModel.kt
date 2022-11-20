package dev.rejfin.todoit.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.models.states.TaskUiState
import dev.rejfin.todoit.utils.CalendarUtility
import dev.rejfin.todoit.utils.TaskNotificationManager
import java.util.*

class NewTaskViewModel: ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    var calendarUtility: CalendarUtility
        private set

    var readyTaskModel: TaskModel = TaskModel()

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private lateinit var userOrGroupId: String

    private var taskId: String? = null
    private var oldTimestamp: Long? = null

    /**
     * same ui is using in homeView ang groupView to create tasks
     * so we need function to set id for account to save in firebase
     */
    fun setIdToSave(id: String?){
        userOrGroupId = id ?: auth.uid!!
    }

    init {
        calendarUtility = CalendarUtility()
        val currentDate = calendarUtility.getCurrentDate()
        taskUiState.apply {
            startDate = currentDate
            endDate = currentDate.copy(hour = currentDate.hour + 1, minutes = 0)
        }
    }

    /** function used if user wants to edit existing task */
    fun setUiState(taskModel: TaskModel){
        taskUiState.apply{
            taskTitle = taskModel.title
            taskDescription = taskModel.description
            taskParts.addAll(taskModel.taskParts)
            startDate = taskModel.startDate
            endDate = taskModel.endDate
            isAllDay = taskModel.allDay
            timeConsuming = taskModel.timeConsuming
            difficulty = taskModel.difficulty
            xpForCompleteTask = taskModel.xpForTask
        }

        taskId = taskModel.id
        oldTimestamp = taskModel.timestamp
    }

    fun addTaskPart(){
        taskUiState.taskParts.add(TaskPartModel(false, ""))
    }

    fun updateTaskPart(index:Int, text: String){
        taskUiState.taskParts[index] = taskUiState.taskParts[index].copy(desc = text)
    }

    fun removeTaskPart(index:Int){
        taskUiState.taskParts.removeAt(index)
    }

    /** this method will update difficulty of task and xp for completion */
    fun updateDifficulty(level: Int){
        val xp = taskUiState.timeConsuming * 5 + taskUiState.difficulty * 5
        taskUiState.apply {
            difficulty = level
            xpForCompleteTask = xp
        }
    }

    /** this method will update timeConsuming variable of task and xp for completion */
    fun updateTimeConsuming(level: Int){
        val xp = taskUiState.timeConsuming * 5 + taskUiState.difficulty * 5
        taskUiState.apply {
            timeConsuming = level
            xpForCompleteTask = xp
        }
    }

    fun updateStartHour(date: CustomDateFormat){
        taskUiState.startDate = taskUiState.startDate.copy(hour = date.hour, minutes = date.minutes)
    }

    fun updateEndHour(date: CustomDateFormat){
        taskUiState.endDate = taskUiState.endDate.copy(hour = date.hour, minutes = date.minutes)
    }

    /**
     * In order for the month to be displayed to the user correctly we need to add 1 to it
     * This is because the java library called Calendar, which I use to retrieve
     * the time and days, starts counting down the months from 0
     */
    fun updateDayOfTask(date: CustomDateFormat){
        taskUiState.apply {
            startDate = taskUiState.startDate.copy(year = date.year, month = date.month + 1, day = date.day)
            endDate = taskUiState.endDate.copy(year = date.year, month = date.month + 1, day = date.day)
        }
    }

    /**
     * The function, after checking the correctness of the data entered by the user,
     * will create in the firebase database a new task appropriately assigned to the group
     * or the logged-in user
     * while if the function encounters any problem it will return the appropriate information to the user
     */
    fun createTask(context: Context){
        taskUiState.taskTitleValidation = ValidationResult(
            isError = taskUiState.taskTitle.isEmpty(),
            errorMessage = "Field can't be empty"
        )

        taskUiState.taskDescriptionValidation = ValidationResult(
                isError = taskUiState.taskDescription.isEmpty(),
                errorMessage = "Field can't be empty")

        /** checking if title and description are not empty */
        if(taskUiState.taskTitleValidation.isError || taskUiState.taskDescriptionValidation.isError){
            return
        }

        /** if task is not all day, we must check if end time is not earlier than start time */
        if(!taskUiState.isAllDay){
            if(taskUiState.startDate.hour > taskUiState.endDate.hour){
                // hour of start is greater than end hour
                taskUiState.taskErrorMessage = "The set date of the task is not correct, make sure the set date and time has not passed"
                return
            }

            if(taskUiState.startDate.hour == taskUiState.endDate.hour){
                if(taskUiState.startDate.minutes >= taskUiState.endDate.minutes){
                    // hour of start is greater than hour of end task or is equal
                    taskUiState.taskErrorMessage = "The set date of the task is not correct, make sure the set date and time has not passed"
                    return
                }
            }
        }

        /**
         * before sending the task to the database,
         * we filter out from the task the parts that do not contain any description
         */
        val taskList = taskUiState.taskParts.filter { it.desc.isNotEmpty() }

        /** we create general task timestamp used to determine the specific day of the task */
        val timestamp = calendarUtility.timestampFromDate(
            taskUiState.startDate.year,
            taskUiState.startDate.month,
            taskUiState.startDate.day
        )

        /**
         * With the start timestamp value, I determine when the notification should be triggered (if set),
         * while endTimestamp is used to determine if the task can still be mark as done or deleted
         */
        val startTimestamp: Long
        val endTimestamp: Long
        if(taskUiState.isAllDay){
            startTimestamp = calendarUtility.timestampFromDate(taskUiState.startDate.copy(hour = 0, minutes = 0), true)
            endTimestamp = calendarUtility.timestampFromDate(taskUiState.endDate.copy(hour = 23, minutes = 59), true)
        }else{
            startTimestamp = calendarUtility.timestampFromDate(taskUiState.startDate, true)
            endTimestamp = calendarUtility.timestampFromDate(taskUiState.endDate, true)
        }

        /**
         * check if endTimestamp value is lower
         * than current timestamp (user cant add task that already passed)
         */
        if(endTimestamp < calendarUtility.getCurrentTimestamp()){
            taskUiState.taskErrorMessage = "The set date of the task is not correct, make sure the set date and time has not passed"
            return
        }

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
            difficulty = taskUiState.difficulty,
            endTimestamp = endTimestamp,
            startTimestamp = startTimestamp,
        )

        val childToUpdate = mutableMapOf<String, Any?>(
            "/tasks/$userOrGroupId/${taskModel.timestamp}/${taskModel.id}" to taskModel,
        )

        if(userOrGroupId == auth.uid){
            childToUpdate["/users/${auth.uid}/allTask"] = ServerValue.increment(1)
        }

        val taskNotificationManager = TaskNotificationManager()

        /**
         * during update task old timestamp and new one can be different
         * if they are not same we must remove old task and add new with new timestamp,
         * remove also notification if exist
         */
        if(timestamp != oldTimestamp){
            childToUpdate["/tasks/$userOrGroupId/$oldTimestamp/${taskModel.id}"] = null
            taskNotificationManager.removeAlarm(context = context, taskModel)
        }

        /**
         * if the task being added is private and is a task with a specific timeframe
         * then set a notification ( notification will appear only if the start time is early enough
         * (current time + amount of time before notification which is set in settings, default 15min) )
         */
        if(userOrGroupId == auth.uid && !taskModel.allDay){
            taskNotificationManager.setAlarm(context = context, taskModel)
        }

        database.reference.updateChildren(childToUpdate).addOnCompleteListener {
            taskUiState = if(it.isSuccessful){
                readyTaskModel = taskModel
                taskUiState.apply {
                    isDataSending = false
                    isDateSent = true
                }
            }else{
                taskUiState.apply {
                    isDataSending = false
                    isDateSent = false
                    taskErrorMessage = it.exception?.localizedMessage
                }
            }
        }
    }
}