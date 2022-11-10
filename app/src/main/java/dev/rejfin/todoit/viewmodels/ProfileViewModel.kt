package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dev.rejfin.todoit.models.NotificationModel
import dev.rejfin.todoit.models.SmallUserModel
import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.models.UserModel
import dev.rejfin.todoit.models.states.ProfileUiState

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val notifyDbRef = database.getReference("notify")
    private val groupsDbRef = database.getReference("groups")
    private val usersDbRef = database.getReference("users")

    init {
        uiState = uiState.copy(isUserStillLoggedIn = auth.currentUser != null)
        getUserData()
        getNotificationList()
    }

    fun logOutUser(){
        auth.signOut()
        uiState = uiState.copy(isUserStillLoggedIn = false)
    }

    fun showNotificationList(){
        uiState = uiState.copy(showNotificationListDialog = true)
    }

    fun hideNotificationList(){
        uiState = uiState.copy(showNotificationListDialog = false)
    }

    private fun getNotificationList(){
        notifyDbRef.child(auth.uid.toString()).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(notificationData in snapshot.children){
                        notificationData.getValue<NotificationModel>()?.let{notification->
                            uiState.notificationList.add(notification)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState = uiState.copy(showLoadingDialog = false, errorMessage = error.message)
                }

            }
        )
    }

    private fun getUserData(){
        if(auth.uid == null){
            logOutUser()
        }
        usersDbRef.child(auth.uid!!).addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<UserModel>()?.let {
                        uiState = uiState.copy(userData = it)

                        uiState.trophyList.clear()
                        uiState.trophyList.addAll(
                            listOf(
                                TrophyModel(id = 0, title = "I will figure it out", description = "create your first task (${if (it.allTask > 1) 1 else it.allTask}/1)", unlocked = it.allTask >= 1),
                                TrophyModel(id = 1, title = "It's not that hard", description = "create 10 tasks (${if (it.allTask > 10) 10 else it.allTask}/10)", unlocked = it.allTask >= 10),
                                TrophyModel(id = 2, title = "I even like it", description = "create 20 tasks (${if (it.allTask > 20) 20 else it.allTask}/20)", unlocked = it.allTask >= 20),
                                TrophyModel(id = 3, title = "Can't live without tasks", description = "create 50 tasks (${if (it.allTask > 50) 50 else it.allTask}/50)", unlocked = it.allTask >= 50),
                                TrophyModel(id = 4, title = "Obsessive planner", description = "create 100 tasks (${if (it.allTask > 100) 100 else it.allTask}/100)", unlocked = it.allTask >= 100),
                                TrophyModel(id = 5, title = "Shy guest", description = "be a member of at least one group (${if (it.groups.size > 1) 1 else it.groups.size}/1)", unlocked = it.groups.isNotEmpty()),
                                TrophyModel(id = 6, title = "Social butterfly", description = "be a member of at least five group (${if (it.groups.size > 5) 5 else it.groups.size}/5)", unlocked = it.groups.size >= 5),
                                TrophyModel(id = 7, title = "So it began", description = "complete your first task (${if (it.taskDone > 1) 1 else it.taskDone}/1)", unlocked = it.taskDone >= 1),
                                TrophyModel(id = 8, title = "Things are starting to come together", description = "complete 10 tasks (${if (it.taskDone > 10) 10 else it.taskDone}/10)", unlocked = it.taskDone >= 10),
                                TrophyModel(id = 9, title = "Busy gentleman", description = "complete 20 tasks (${if (it.taskDone > 20) 20 else it.taskDone}/20)", unlocked = it.taskDone >= 20),
                                TrophyModel(id = 10, title = "Task master", description = "complete 50 tasks (${if (it.taskDone > 50) 50 else it.taskDone}/50)", unlocked = it.taskDone >= 50),
                                TrophyModel(id = 11, title = "Life is a task", description = "complete 100 tasks (${if (it.taskDone > 100) 100 else it.taskDone}/100)", unlocked = it.taskDone >= 100),
                                TrophyModel(id = 12, title = "Guest", description = "reach lvl 2 (${if (it.xp >= 150) 2 else (it.xp/150 + 1)}/2)", unlocked = it.xp >= 150),
                                TrophyModel(id = 13, title = "Newbie", description = "reach lvl 5 (${if (it.xp >= 600) 5 else (it.xp/150 + 1)}/5)", unlocked = it.xp >= 600),
                                TrophyModel(id = 14, title = "Novice", description = "reach lvl 10 (${if (it.xp >= 1350) 10 else (it.xp/150 + 1)}/10)", unlocked = it.xp >= 1350),
                                TrophyModel(id = 15, title = "Apprentice", description = "reach lvl 20 (${if (it.xp >= 2850) 20 else (it.xp/150 + 1)}/20)", unlocked = it.xp >= 2850),
                                TrophyModel(id = 16, title = "Advanced", description = "reach lvl 30 (${if (it.xp >= 4350) 30 else (it.xp/150 + 1)}/30)", unlocked = it.xp >= 4350),
                                TrophyModel(id = 17, title = "Expert", description = "reach lvl 40 (${if (it.xp >= 5850) 40 else (it.xp/150 + 1)}/40)", unlocked = it.xp >= 5850),
                                TrophyModel(id = 18, title = "Master", description = "reach lvl 50 (${if (it.xp >= 7350) 50 else (it.xp/150 + 1)}/50)", unlocked = it.xp >= 7350),
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState = uiState.copy(errorMessage = error.message)
                }

            }
        )
    }

    fun joinGroup(payload: Any){
        uiState = uiState.copy(showLoadingDialog = true)
        val data = payload as Map<*, *>
        val groupId = data["groupId"]

        val user = SmallUserModel(auth.uid!!, auth.currentUser!!.displayName!!, auth.currentUser!!.photoUrl.toString())
        groupsDbRef.child(groupId.toString()).child("membersList").child(auth.uid!!).setValue(user).addOnCompleteListener { task1 ->
            if(task1.isSuccessful){
                val group = mapOf("id" to groupId)
                usersDbRef.child(auth.uid!!).child("groups").child(groupId.toString()).setValue(group).addOnCompleteListener {task2 ->
                    uiState = if(task2.isSuccessful){
                        uiState.copy(showLoadingDialog = false, infoMessage = "you successfully joined group")
                    }else{
                        uiState.copy(showLoadingDialog = false, errorMessage = task2.exception?.localizedMessage)
                    }
                }
            }else{
                uiState = uiState.copy(showLoadingDialog = false, errorMessage = task1.exception?.localizedMessage)
            }
        }
    }

    fun clearError(){
        uiState = uiState.copy(errorMessage = null)
    }

    fun clearInfo(){
        uiState = uiState.copy(infoMessage = null)
    }

    fun showInfoDialog(text: String){
        uiState = uiState.copy(infoMessage = text)
    }
}