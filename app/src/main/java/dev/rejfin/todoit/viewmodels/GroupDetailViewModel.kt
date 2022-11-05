package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dev.rejfin.todoit.models.*
import dev.rejfin.todoit.models.states.BaseTaskUiState
import dev.rejfin.todoit.models.states.GroupDetailUiState
import kotlinx.coroutines.launch
import java.util.*

class GroupDetailViewModel : BaseTaskManagerViewModel() {
    private val _uiState by mutableStateOf(GroupDetailUiState())
    val uiState: GroupDetailUiState
        get() = _uiState

    override fun getBaseUiState(): BaseTaskUiState {
        return _uiState
    }

    fun setGroupId(id:String){
        _uiState.groupId = id
        _uiState.groupData = GroupModel(id = id)

        viewModelScope.launch {
            val initTask = launch { getInitialData() }
            val groupInfoTask = launch { getInfoAboutGroup() }
            initTask.join()
            groupInfoTask.join()
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
        groupsDbRef.child(_uiState.groupId!!).addListenerForSingleValueEvent(
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
}