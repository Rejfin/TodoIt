package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.*

class GroupDetailUiState: BaseTaskUiState(){
    var groupData by mutableStateOf(GroupModel())
    var showGroupDetails by mutableStateOf(false)
    var endedGroupRemovingUser by mutableStateOf(false)
    var userRemovedFromGroup:SmallUserModel? = null
}