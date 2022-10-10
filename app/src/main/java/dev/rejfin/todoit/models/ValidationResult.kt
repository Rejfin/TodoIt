package dev.rejfin.todoit.models

data class ValidationResult(
    val isError: Boolean = false,
    val errorMessage: String? = null
)
