package dev.rejfin.todoit.models.states

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.ValidationResult

class RegisterUiState(
    var nick: MutableState<String> = mutableStateOf(""),
    var displayName: MutableState<String> = mutableStateOf(""),
    var repeatedPassword: MutableState<String> = mutableStateOf(""),
    val nickValidation: MutableState<ValidationResult> = mutableStateOf(ValidationResult()),
    val displayNameValidation: MutableState<ValidationResult> = mutableStateOf(ValidationResult()),
    val repeatedPasswordValidation: MutableState<ValidationResult> = mutableStateOf(ValidationResult()),
    val registerSuccess: MutableState<Boolean> = mutableStateOf(false),
    var selectedImage: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
): BaseAuthUiState()
