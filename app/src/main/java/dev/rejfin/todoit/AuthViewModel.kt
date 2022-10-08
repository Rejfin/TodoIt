package dev.rejfin.todoit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.rejfin.todoit.utils.RegisterUiState
import dev.rejfin.todoit.utils.ValidationResult

class AuthViewModel: ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun registerUser(nick: String, email:String, password:String, repeatedPassword: String){
        uiState = uiState.copy(isRegisterInProgress = true)
        validateNick(nick)
        validateEmail(email)
        validatePassword(password)
        validateRepeatedPassword(password, repeatedPassword)
        if(uiState.nick.isError || uiState.email.isError || uiState.password.isError || uiState.repeatedPassword.isError){
            uiState = uiState.copy(isRegisterInProgress = false, registerFailed = true)
        }
    }

    fun clearError(fieldState: ValidationResult){
        when(fieldState){
            uiState.nick -> uiState = uiState.copy(nick = ValidationResult(isError = false))
            uiState.email -> uiState = uiState.copy(email = ValidationResult(isError = false))
            uiState.password -> uiState = uiState.copy(password = ValidationResult(isError = false))
            uiState.repeatedPassword -> uiState = uiState.copy(repeatedPassword = ValidationResult(isError = false))
        }
    }

    private fun validateNick(nick:String): Boolean{
        val isNickOk = nick.length >= 4
        uiState = uiState.copy(nick = ValidationResult(isError = !isNickOk, errorMessage = "Nick has to be at least 4 characters"))
        return isNickOk
    }

    private fun validateEmail(email:String): Boolean{
        val isEmailOk = email.contains("@")
        uiState = uiState.copy(email = ValidationResult(isError = !isEmailOk, errorMessage = "bad email format"))
        return isEmailOk
    }

    private fun validatePassword(password: String): Boolean{
        val isPasswordOk = password.length >= 8 && password.any { it.isDigit() && it.isLetter() }
        uiState = uiState.copy(password = ValidationResult(isError = (!isPasswordOk), errorMessage = "your password must:\n- be at least 8 characters long\n- have at least one letter and one number"))
        return isPasswordOk
    }

    private fun validateRepeatedPassword(password: String, repeatedPassword: String): Boolean{
        val isRepeatedPasswordOk = password == repeatedPassword
        uiState = uiState.copy(repeatedPassword = ValidationResult(isError = !isRepeatedPasswordOk, errorMessage = "Your passwords doesn't match"))
        return isRepeatedPasswordOk
    }
}