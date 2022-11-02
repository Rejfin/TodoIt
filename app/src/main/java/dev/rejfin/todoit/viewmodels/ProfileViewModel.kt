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

    fun joinGroup(payload: Any){
        uiState = uiState.copy(showLoadingDialog = true)
        val data = payload as Map<*, *>
        val groupId = data["groupId"]

        val user = UserModel(auth.uid!!, auth.currentUser!!.displayName!!, auth.currentUser!!.photoUrl.toString())
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
}