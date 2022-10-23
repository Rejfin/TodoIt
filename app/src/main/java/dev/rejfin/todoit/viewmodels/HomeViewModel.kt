package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    val taskList = mutableStateListOf<TaskModel>()

    var calendarDays = mutableStateListOf<CalendarDay>()
        private set

    private val calendarUtility: CalendarUtility = CalendarUtility()
    private val database = Firebase.database
    private val dbRef = database.getReference("tasks")
    private val firebaseAuth = Firebase.auth

    init {

        CalendarUtility().getDaysInCurrentWeek().forEach {
            calendarDays.add(it)
            getTaskFromDay(it.date)
        }
    }

    fun getTaskFromDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)

        dbRef.child(firebaseAuth.uid!!).child(timestamp.toString()).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                        homeUiState = homeUiState.copy(numberOfAllTasks = 0, numberOfDoneTask = 0)
                    }

                    taskList.clear()
                    val calendarDay = calendarDays.find { calendarUtility.areDateSame(it.date, date) }
                    val index = calendarDays.indexOf(calendarDay)
                    calendarDays[index].numberOfTasks = 0

                    for (taskSnapshot in snapshot.children) {
                        taskSnapshot.getValue<TaskModel>()?.let {
                            taskList.add(it)
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