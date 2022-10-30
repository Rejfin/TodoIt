package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.models.states.GroupDetailUiState
import dev.rejfin.todoit.utils.CalendarUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.*

class GroupDetailViewModel : ViewModel() {
    var uiState by mutableStateOf(GroupDetailUiState())
        private set

    private val calendarUtility: CalendarUtility = CalendarUtility()
    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = database.getReference("tasks")
    private val nicksDbRef = database.getReference("nicks")
    private val notifyDbRef = database.getReference("notify")
    private var selectedDate = CustomDateFormat()

    fun setGroupId(id:String){
        uiState = uiState.copy(groupData = GroupModel(id = id))

        uiState.calendarDays.clear()
        viewModelScope.launch {
            val taskList = mutableListOf<Job>()
            val groupInfoTask = launch{getInfoAboutGroup()}
            taskList.add(groupInfoTask)
            CalendarUtility().getDaysInCurrentWeek().forEach {
                uiState.calendarDays.add(it)
                val task = launch{
                    getTaskFromDay(it.date)
                }
                taskList.add(task)
            }
            taskList.joinAll()
            selectedDate = calendarUtility.getCurrentDate()
        }

    }

    fun getUserId():String{
        return auth.uid.toString()
    }

    fun switchTaskListDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)
        uiState = uiState.copy(selectedTaskList = uiState.allTaskList[timestamp] ?: emptyList())
        selectedDate = date
    }

    fun showGroupDetails(){
        uiState = uiState.copy(showGroupDetails = true)
    }

    fun closeGroupDetails(){
        uiState = uiState.copy(showGroupDetails = false)
    }

    // download data about selected group
    private fun getInfoAboutGroup(){
        database.getReference("groups").child(uiState.groupData.id).addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<GroupModel>()?.let{
                        uiState = uiState.copy(groupData = it)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState = uiState.copy(errorMessage = error.message)
                }

            }
        )
    }

    // download data about task for given day
    private fun getTaskFromDay(date: CustomDateFormat){
        val timestamp = calendarUtility.timestampFromDate(date.year, date.month, date.day)

        dbRef.child(uiState.groupData.id).child(timestamp.toString()).addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // if date are exact same as today then count number of done tasks
                    if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                        uiState = uiState.copy(numberOfAllTasks = 0, numberOfDoneTask = 0)
                    }

                    // find day in list of days to update number of tasks
                    // this is used to display a blue dot next to days on which there is at least one task
                    val calendarDay = uiState.calendarDays.find { calendarUtility.areDateSame(it.date, date) }
                    val index = uiState.calendarDays.indexOf(calendarDay)
                    uiState.calendarDays[index].numberOfTasks = 0

                    // clear list before update it
                    uiState.allTaskList[timestamp] = mutableListOf()

                    // iterate over all tasks saved in firebase for given day
                    for (taskSnapshot in snapshot.children) {
                        taskSnapshot.getValue<TaskModel>()?.let {

                            uiState.allTaskList[timestamp]?.add(it)
                            val day = uiState.calendarDays[index]
                            uiState.calendarDays.removeAt(index)
                            uiState.calendarDays.add(index, day.copy(numberOfTasks = 1))

                            if(calendarUtility.areDateSame(date, calendarUtility.getCurrentDate())){
                                viewModelScope.launch(Dispatchers.Main) {
                                   uiState = uiState.copy(
                                        numberOfAllTasks = uiState.numberOfAllTasks + 1,
                                        numberOfDoneTask = if(it.done){
                                            uiState.numberOfDoneTask + 1
                                        }else{
                                            uiState.numberOfDoneTask
                                        }
                                    )
                                }
                            }
                        }
                    }
                    switchTaskListDay(selectedDate)
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState = uiState.copy(errorMessage = error.message)
                }
            }
        )
    }

    fun sendInvitation(nick: String){
        nicksDbRef.child(nick).get().addOnCompleteListener {
            if(it.result.exists()){
                val userId = it.result.value as Map<*, *>
                val notifyId = UUID.randomUUID().toString()
                notifyDbRef.child(userId["userId"].toString()).child(notifyId).setValue(
                    NotificationModel(
                        id = notifyId,
                        NotificationType.INVITATION,
                        "Invitation to group: \"${uiState.groupData.name}\"",
                        mapOf("groupId" to uiState.groupData.id)
                    )
                )
            }
        }

    }
}