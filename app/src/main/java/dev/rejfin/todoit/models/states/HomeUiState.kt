package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class HomeUiState(
    loggedUserDisplayName: String = "",
    isDataLoading: Boolean = false
): BaseTaskUiState(){
    var isDataLoading by mutableStateOf(isDataLoading)
    var loggedUserDisplayName by mutableStateOf(loggedUserDisplayName)
}
