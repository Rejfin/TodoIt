package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.ValidationResult

data class RegisterUiState(
    val nick: ValidationResult = ValidationResult(),
    val email: ValidationResult = ValidationResult(),
    val password: ValidationResult = ValidationResult(),
    val repeatedPassword: ValidationResult = ValidationResult(),
    val isAuthInProgress: Boolean = false,
    val authFailedMessage:String? = null,
    val registerSuccess:Boolean = false
)
