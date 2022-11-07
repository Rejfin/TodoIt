package dev.rejfin.todoit.models.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class LoginUiState(
    val isUserLoggedIn: MutableState<Boolean> = mutableStateOf(false)
): BaseAuthUiState()
