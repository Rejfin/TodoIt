package dev.rejfin.todoit.utils

data class ValidationResult(
    val isError: Boolean = false,
    val errorMessage: String? = null
)
