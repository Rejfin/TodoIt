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
import dev.rejfin.todoit.models.UserModel
import dev.rejfin.todoit.models.states.ProfileUiState
import dev.rejfin.todoit.utils.TrophyList

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val notifyDbRef = database.getReference("notify")
    private val usersDbRef = database.getReference("users")

    init {
        uiState.isUserStillLoggedIn = auth.currentUser != null
        getUserData()
        getNotificationList()
    }

    fun logOutUser(){
        auth.signOut()
        uiState.isUserStillLoggedIn = false
    }

    /** download notification and save it to list in uiState */
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
                    uiState.apply {
                        errorMessage = error.message
                        showLoadingDialog = false
                    }
                }

            }
        )
    }

    /**
     * get user data saved in firebase
     * then create list of achievements based on user profile and his data
     */
    private fun getUserData(){
        if(auth.uid == null){
            logOutUser()
        }
        usersDbRef.child(auth.uid!!).addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<UserModel>()?.let { user ->
                        uiState.apply {
                            userData = user
                            trophyList.clear()
                            trophyList.addAll(TrophyList.getTrophyList(user))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    uiState.errorMessage = error.message
                }
            }
        )
    }

    /**
     * Function allows a user to join if the target group has sent him an invitation
     */
    fun joinGroup(groupId: String, notificationId: String){
        uiState.showLoadingDialog = true

        val user = SmallUserModel(auth.uid!!, auth.currentUser!!.displayName!!, auth.currentUser!!.photoUrl.toString())
        val group = mapOf("id" to groupId)

        /** set all paths that will be updated in firebase at once */
        val childToUpdate = mutableMapOf(
            "/users/${auth.uid!!}/groups/${groupId}" to group,
            "/groups/$groupId/membersList/${auth.uid!!}" to user,
            "/notify/${user.id}/$notificationId" to null
        )

        database.reference.updateChildren(childToUpdate).addOnCompleteListener { task->
            uiState = if(task.isSuccessful){
                uiState.apply {
                    showLoadingDialog = false
                    infoMessage = "you successfully joined group"
                }
            }else{
                uiState.apply {
                    showLoadingDialog = false
                    infoMessage = "you successfully joined group"
                }
                uiState.apply {
                    showLoadingDialog = false
                    infoMessage = task.exception?.localizedMessage
                }
            }
        }
    }
}