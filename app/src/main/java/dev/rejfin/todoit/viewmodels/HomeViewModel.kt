package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.utils.CalendarUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
        private set

    val allTaskList = mutableMapOf<Long, MutableList<TaskModel>>()
    val taskList = mutableStateListOf<TaskModel>()


    var calendarDays = mutableStateListOf<CalendarDay>()
        private set

    private val calendarUtility: CalendarUtility = CalendarUtility()
    private val database = Firebase.database
    private val dbRef = database.getReference("tasks")
    private val firebaseAuth = Firebase.auth
    private var selectedDate = CustomDateFormat()

    init {
        CalendarUtility().getDaysInCurrentWeek().forEach {
            calendarDays.add(it)
            getTaskFromDay(it.date)
        }
        selectedDate = calendarUtility.getCurrentDate()
    }

    fun switchTaskListDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)
        taskList.clear()
        taskList.addAll(if (allTaskList[timestamp] != null) allTaskList[timestamp]!! else emptyList())
        selectedDate = date
    }

    fun endUpdateList(){
        switchTaskListDay(selectedDate)
    }

    private fun getTaskFromDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)

        dbRef.child(firebaseAuth.uid!!).child(timestamp.toString()).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // if date are exact same as today then count number of done tasks
                    if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                        homeUiState = homeUiState.copy(numberOfAllTasks = 0, numberOfDoneTask = 0)
                    }

                    // find day in list of days to update number of tasks
                    // this is used to display a blue dot next to days on which there is at least one task
                    val calendarDay = calendarDays.find { calendarUtility.areDateSame(it.date, date) }
                    val index = calendarDays.indexOf(calendarDay)
                    calendarDays[index].numberOfTasks = 0

                    // clear list before update it
                    allTaskList[timestamp] = mutableListOf()

                    // iterate over all tasks saved in firebase for given day
                    for (taskSnapshot in snapshot.children) {
                        taskSnapshot.getValue<TaskModel>()?.let {

                            allTaskList[timestamp]?.add(it)
                            val day = calendarDays[index]
                            calendarDays.removeAt(index)
                            calendarDays.add(index, day.copy(numberOfTasks = 1))

                            if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                                viewModelScope.launch(Dispatchers.Main) {
                                    homeUiState = homeUiState.copy(
                                        numberOfAllTasks = homeUiState.numberOfAllTasks + 1,
                                        numberOfDoneTask = if(it.done){
                                            homeUiState.numberOfDoneTask + 1
                                        }else{
                                            homeUiState.numberOfDoneTask
                                        }
                                    )
                                }
                            }
                        }
                    }
                    endUpdateList()
                }

                override fun onCancelled(error: DatabaseError) {
                    homeUiState = homeUiState.copy(errorMessage = error.message)
                }
            }
        )
    }

    fun clearError(){
        homeUiState = homeUiState.copy(errorMessage = null)
    }

    fun showTaskDetails(task: TaskModel){
        homeUiState = homeUiState.copy(showDetailsDialog = true, taskToShowDetails = task)
    }

    fun hideTaskDetails(){
        homeUiState = homeUiState.copy(showDetailsDialog = false, taskToShowDetails = null)
    }
}