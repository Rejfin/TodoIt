package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.utils.CalendarUtility
import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val taskList = mutableStateListOf<TaskModel>()

    var calendarDays = mutableStateListOf<CalendarDay>()
        private set

    var calendarUtility: CalendarUtility
        private set

    var numberOfDoneTask = mutableStateOf(0)
        private set

    var numberOfAllTasks = mutableStateOf(0)
        private set

    private val database = Firebase.database
    private val dbRef = database.getReference("tasks")
    private val firebaseAuth = Firebase.auth

    init {
        calendarUtility = CalendarUtility()

        val date = calendarUtility.getCurrentDate()
        getTaskFromDay(date)

        calendarDays.addAll(
            CalendarUtility().getDaysInCurrentWeek()
        )
    }

    fun getTaskFromDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)

        dbRef.child(firebaseAuth.uid!!).child(timestamp.toString()).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                        numberOfAllTasks.value = 0
                        numberOfDoneTask.value = 0
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
                                    if(it.done){
                                        numberOfDoneTask.value++
                                    }
                                    numberOfAllTasks.value++
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}