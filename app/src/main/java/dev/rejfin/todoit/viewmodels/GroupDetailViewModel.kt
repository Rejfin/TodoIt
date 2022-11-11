package dev.rejfin.todoit.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.models.states.BaseTaskUiState
import dev.rejfin.todoit.models.states.GroupDetailUiState
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.util.*

class GroupDetailViewModel : BaseTaskManagerViewModel() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("groups")
    private val _uiState by mutableStateOf(GroupDetailUiState())
    val uiState: GroupDetailUiState
        get() = _uiState

    override fun getBaseUiState(): BaseTaskUiState {
        return _uiState
    }

    /** set group id and download initial data for group screen */
    fun setGroupId(id:String){
        _uiState.groupId = id
        _uiState.groupData = GroupModel(id = id)

        if(_uiState.calendarDays.isEmpty()){
            viewModelScope.launch {
                _uiState.isDataLoading = true
                getInfoAboutGroup()

                getInitialData{
                    _uiState.isDataLoading = false
                }
            }
        }
    }

    fun showGroupDetails(){
        _uiState.showGroupDetails = true
    }

    fun closeGroupDetails(){
        _uiState.showGroupDetails = false
    }

    /** download data about selected group */
    private fun getInfoAboutGroup(){
        groupsDbRef.child(_uiState.groupId!!).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<GroupModel>()?.let{
                        _uiState.groupData = it
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.errorMessage = error.message
                }
            }
        )
    }

    /** send invitation to group if user with given nick exist */
    fun sendInvitation(nick: String){
        nicksDbRef.child(nick).get().addOnCompleteListener {
            if(it.result.exists()){
                val userId = it.result.value as Map<*, *>
                val notifyId = UUID.randomUUID().toString()
                notifyDbRef.child(userId["userId"].toString()).child(notifyId).setValue(
                    NotificationModel(
                        id = notifyId,
                        NotificationType.INVITATION,
                        "Invitation to group: \"${_uiState.groupData.name}\"",
                        mapOf("groupId" to _uiState.groupId!!)
                    )
                )
                _uiState.infoMessage = "Invitation has been sent"
            }else{
                _uiState.errorMessage = "User does not exist"
            }
        }
    }

    /** update group info ex. group name, description and group photo */
    fun updateGroupInfo(name:String, description:String, imageUri: Uri?, groupData: GroupModel){
        if(groupData.name != name || groupData.desc != description){
            val childToUpdate = mutableMapOf<String, Any?>(
                "/groups/${groupData.id}/desc" to description,
                "/groups/${groupData.id}/name" to name,
            )

            if(imageUri != null){
                sendImage(imageUri, groupData.id){
                    childToUpdate["/groups/${groupData.id}/imageUrl"] = it

                    database.reference.updateChildren(childToUpdate)
                }
            }else{
                database.reference.updateChildren(childToUpdate)
            }
        }
    }

    /**
     * Function used to send user profile picture to firebase storage
     * if the operation is successful, the callback argument will return valid image url
     * if the operation is not successful, the callback argument will return null
     */
    private fun sendImage(image: Uri, userId: String, callback: (imageUrl:String?) -> Unit){
        val ref = storageRef.child(userId)
        val uploadTask = ref.putFile(image)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                callback(null)
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result.toString())
            } else {
                callback(null)
            }
        }
    }

    /** remove user from selected group */
    fun removeUserFromGroup(user: SmallUserModel){
        if(uiState.userId == uiState.groupData.ownerId || uiState.userId == user.id){

            val childToUpdate = mutableMapOf<String, Any?>(
                "/users/${user.id}/groups/${_uiState.groupData.id}" to null,
            )

            if(uiState.groupData.membersList.size == 1){
                childToUpdate["/groups/${_uiState.groupData.id}"] = null
                childToUpdate["/tasks/${_uiState.groupData.id}"] = null
            }else{
                childToUpdate["/groups/${_uiState.groupData.id}/membersList/${user.id}"] = null
            }

            database.reference.updateChildren(childToUpdate).addOnCompleteListener {
                if(it.isSuccessful){
                    _uiState.userRemovedFromGroup = user
                    _uiState.endedGroupRemovingUser = true
                }else{
                    _uiState.errorMessage = it.exception?.localizedMessage
                    _uiState.userRemovedFromGroup = null
                    _uiState.endedGroupRemovingUser = true
                }
            }
        }else{
            _uiState.errorMessage = "Permission denied, you can\'t remove this user from group"
        }
    }
}