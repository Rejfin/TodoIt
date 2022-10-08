package dev.rejfin.todoit.utils

data class RegisterUiState(
    val nick: ValidationResult = ValidationResult(),
    val email: ValidationResult = ValidationResult(),
    val password: ValidationResult = ValidationResult(),
    val repeatedPassword: ValidationResult = ValidationResult(),
    val isRegisterInProgress: Boolean = false,
    val registerFailed: Boolean = false
)
