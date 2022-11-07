package dev.rejfin.todoit.models.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.ValidationResult

open class BaseAuthUiState(
    email: String = "",
    password: String = "",
    var emailValidation: MutableState<ValidationResult> = mutableStateOf(ValidationResult()),
    var passwordValidation: MutableState<ValidationResult> = mutableStateOf(ValidationResult())
){
    var email: MutableState<String> = mutableStateOf(email)
    var password: MutableState<String> = mutableStateOf(password)
    var isAuthInProgress: MutableState<Boolean> = mutableStateOf(false)
    var authFailedMessage: MutableState<String?> = mutableStateOf(null)
}