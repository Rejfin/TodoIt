package dev.rejfin.todoit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.states.BaseTaskUiState
import dev.rejfin.todoit.utils.CalendarUtility
import kotlinx.coroutines.*

abstract class BaseTaskManagerViewModel: ViewModel() {
    protected val calendarUtility: CalendarUtility = CalendarUtility()
    protected val auth = FirebaseAuth.getInstance()
    protected val database = Firebase.database
    private val tasksDbRef = database.getReference("tasks")
    protected val nicksDbRef = database.getReference("nicks")
    protected val notifyDbRef = database.getReference("notify")
    protected val groupsDbRef = database.getReference("groups")

    /**
     * abstract function that will be implemented in viewModel who is inherited from this class
     * and will return uiState used in
     */
    abstract fun getBaseUiState(): BaseTaskUiState

    /**
     * Get initial data for view i.e., list of tasks per day and
     * populate days to calendar days list (used by calendar component) in uiState
     */
    suspend fun getInitialData(callback: () -> Unit = {}) = coroutineScope{
        getBaseUiState().userId = auth.uid!!
        CalendarUtility().getDaysInCurrentWeek().forEach {
            getBaseUiState().calendarDays.add(it)

            getTaskFromDay(it.date){
                callback()
            }
        }

        getBaseUiState().selectedDate = calendarUtility.getCurrentDate()

    }

    /**
     * The function responsible for changing the currently displayed task list for a specific day
     */
    fun switchTaskListDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)
        getBaseUiState().selectedTaskList.clear()
        getBaseUiState().selectedTaskList.addAll(getBaseUiState().allTaskList[timestamp] ?: emptyList())
        getBaseUiState().selectedDate = date
    }

    /**
     * Download data about task for given day and listen for changes
     */
    private fun getTaskFromDay(date: CustomDateFormat, callback: () -> Unit = {}){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)

        val id = getBaseUiState().groupId ?: getBaseUiState().userId

        tasksDbRef.child(id).child(timestamp.toString()).addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    /** if date are exact same as today then count number of done tasks */
                    if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                        getBaseUiState().numberOfAllTasks = 0
                        getBaseUiState().numberOfDoneTask = 0
                    }

                    /**
                     * find day in list of days to update number of tasks
                     * this is used to display a blue dot next to days on which there is at least one task
                     */
                    val calendarDay = getBaseUiState().calendarDays.find { calendarUtility.areDateSame(it.date, date) }
                    val index = getBaseUiState().calendarDays.indexOf(calendarDay)
                    getBaseUiState().calendarDays[index].numberOfTasks = 0

                    /** clear list before update it */
                    getBaseUiState().allTaskList[timestamp] = mutableListOf()

                    /** iterate over all tasks saved in firebase for given day */
                    for (taskSnapshot in snapshot.children) {
                        taskSnapshot.getValue<TaskModel>()?.let {

                            getBaseUiState().allTaskList[timestamp]?.add(it)
                            val day = getBaseUiState().calendarDays[index]
                            getBaseUiState().calendarDays.removeAt(index)
                            getBaseUiState().calendarDays.add(index, day.copy(numberOfTasks = 1))

                            if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                                viewModelScope.launch(Dispatchers.Main) {
                                    getBaseUiState().numberOfAllTasks++
                                    if(it.done){
                                        getBaseUiState().numberOfDoneTask++
                                    }
                                }
                            }
                        }
                    }
                    switchTaskListDay(getBaseUiState().selectedDate)
                    callback()
                }

                override fun onCancelled(error: DatabaseError) {
                    getBaseUiState().errorMessage = error.message
                    callback()
                }
            }
        )
    }

    /**
     * Clear info message, thanks to this function ui can hide info dialog
     */
    fun clearInfoMessages(){
        getBaseUiState().infoMessage = null
    }

    /**
     * Clear error message, thanks to this function ui can hide error dialog
     */
    fun clearErrorMessages(){
        getBaseUiState().errorMessage = null
    }

    /**
     * remove task from database (only if timestamp of task is not too old)
     * if something goes wrong (ex. bad task id, permission error, etc.) set an error message
     * to let the user know what went wrong
     */
    fun removeTask(task: TaskModel){
        val id = getBaseUiState().groupId ?: getBaseUiState().userId

        val childToUpdate = mutableMapOf(
            "/tasks/$id/${task.timestamp}/${task.id}" to null,
            "/users/${auth.uid}/allTask" to ServerValue.increment(-1),
        )

        database.reference.updateChildren(childToUpdate).addOnCompleteListener {
            if(!it.isSuccessful){
                getBaseUiState().errorMessage = it.exception?.localizedMessage
            }
        }
    }

    fun showTaskDetails(task: TaskModel){
        getBaseUiState().taskToShowDetails = task
        getBaseUiState().showDetailsDialog = true
    }

    fun hideTaskDetails(){
        getBaseUiState().taskToShowDetails = null
        getBaseUiState().showDetailsDialog = false
    }

    fun markTaskAsDone(task: TaskModel){
        val parts = task.taskParts.map { it.copy(status = true) }
        val mTask = task.copy(done = true, taskParts = parts)
        val id = getBaseUiState().groupId ?: getBaseUiState().userId

        val childToUpdate = mutableMapOf(
            "/tasks/$id/${task.timestamp}/${task.id}" to mTask,
            "/users/${auth.uid}/taskDone" to ServerValue.increment(1),
            "/users/${auth.uid}/xp" to ServerValue.increment(task.xpForTask.toLong())
        )

        if(task.groupId != null){
            childToUpdate["/users/${auth.uid!!}/allTask"] = ServerValue.increment(1)
        }

        database.reference.updateChildren(childToUpdate).addOnCompleteListener {
            if(!it.isSuccessful){
                getBaseUiState().errorMessage = it.exception?.localizedMessage
            }
        }
    }

    fun taskPartsUpdate(task: TaskModel){
        if(task.done){
            return
        }
        val id = getBaseUiState().groupId ?: getBaseUiState().userId

        val childToUpdate = mutableMapOf<String, Any>(
            "/tasks/$id/${task.timestamp}/${task.id}/taskParts" to task.taskParts,
        )

        if(task.taskParts.all { it.status }){
            childToUpdate["/tasks/$id/${task.timestamp}/${task.id}/done"] = true
            childToUpdate["/users/${auth.uid}/xp"] = ServerValue.increment(task.xpForTask.toLong())
        }

        database.reference.updateChildren(childToUpdate)
    }
}