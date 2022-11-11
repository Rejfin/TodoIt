package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class HomeUiState(
    loggedUserDisplayName: String = ""
): BaseTaskUiState(){
    var loggedUserDisplayName by mutableStateOf(loggedUserDisplayName)
}
