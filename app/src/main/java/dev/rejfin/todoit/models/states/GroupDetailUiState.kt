package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.*

class GroupDetailUiState(
    groupData: GroupModel = GroupModel(),
    showGroupDetails: Boolean = false,
) : BaseTaskUiState(){
    var groupData by mutableStateOf(groupData)
    var showGroupDetails by mutableStateOf(showGroupDetails)
}