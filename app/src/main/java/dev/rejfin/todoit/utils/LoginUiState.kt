package dev.rejfin.todoit.utils

data class LoginUiState(
    val email: ValidationResult = ValidationResult(),
    val password: ValidationResult = ValidationResult(),
    val isAuthInProgress: Boolean = false,
    val authFailedMessage:String? = null,
    val isUserLoggedIn: Boolean = false
)
