package dev.rejfin.todoit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dev.rejfin.todoit.models.LoginUiState
import dev.rejfin.todoit.models.RegisterUiState
import dev.rejfin.todoit.models.ValidationResult

class AuthViewModel: ViewModel() {
    var registerUiState by mutableStateOf(RegisterUiState())
        private set

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    private var auth: FirebaseAuth = Firebase.auth

    fun registerUserWithEmail(nick: String, email:String, password:String, repeatedPassword: String){
        registerUiState = registerUiState.copy(isAuthInProgress = true)

        validateNick(nick)
        validateEmail(email)
        validatePassword(password)
        validateRepeatedPassword(password, repeatedPassword)

        if(registerUiState.nick.isError || registerUiState.email.isError || registerUiState.password.isError || registerUiState.repeatedPassword.isError){
            registerUiState = registerUiState.copy(isAuthInProgress = false)
        }else{
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){

                    val user = Firebase.auth.currentUser
                    val profileUpdate = userProfileChangeRequest {
                        displayName = nick
                    }

                    user!!.updateProfile(profileUpdate).addOnCompleteListener {
                        registerUiState = registerUiState.copy(isAuthInProgress = false, registerSuccess = true)
                        auth.signOut()
                    }

                }else{
                    registerUiState = registerUiState.copy(isAuthInProgress = false, authFailedMessage = it.exception?.localizedMessage, registerSuccess = false)
                }
            }
        }
    }

    fun loginUserWithEmail(email:String, password: String){
        loginUiState = loginUiState.copy(isAuthInProgress = true)
        if(email.isEmpty() || password.isEmpty()){
            loginUiState = loginUiState.copy(isAuthInProgress = false)
        }else{
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                loginUiState = if(it.isSuccessful){
                    loginUiState.copy(isAuthInProgress = false, isUserLoggedIn = true)
                }else{
                    loginUiState.copy(isAuthInProgress = false, authFailedMessage = it.exception?.localizedMessage)
                }
            }
        }
    }

    fun isUserAlreadyLoggedIn(): Boolean{
        val isUserLoggedIn = auth.currentUser != null
        loginUiState = loginUiState.copy(isUserLoggedIn = isUserLoggedIn)
        return isUserLoggedIn
    }

    fun clearError(fieldState: ValidationResult){
        when(fieldState){
            registerUiState.nick -> registerUiState = registerUiState.copy(nick = ValidationResult(isError = false))
            registerUiState.email -> registerUiState = registerUiState.copy(email = ValidationResult(isError = false))
            registerUiState.password -> registerUiState = registerUiState.copy(password = ValidationResult(isError = false))
            registerUiState.repeatedPassword -> registerUiState = registerUiState.copy(repeatedPassword = ValidationResult(isError = false))
            loginUiState.email -> loginUiState = loginUiState.copy(email = ValidationResult(isError = false))
            loginUiState.password -> loginUiState = loginUiState.copy(password = ValidationResult(isError = false))
        }
    }

    fun dismissAuthError(){
        loginUiState = loginUiState.copy(authFailedMessage = null)
        registerUiState = registerUiState.copy(authFailedMessage = null)
    }

    private fun validateNick(nick:String): Boolean{
        val isNickOk = nick.length >= 4
        registerUiState = registerUiState.copy(nick = ValidationResult(isError = !isNickOk, errorMessage = "Nick has to be at least 4 characters"))
        return isNickOk
    }

    private fun validateEmail(email:String): Boolean{
        val isEmailOk = email.contains("@")
        registerUiState = registerUiState.copy(email = ValidationResult(isError = !isEmailOk, errorMessage = "bad email format"))
        return isEmailOk
    }

    private fun validatePassword(password: String): Boolean{
        val isPasswordOk = password.length >= 8 && password.any { it.isDigit()} && password.any{it.isLetter()}
        registerUiState = registerUiState.copy(password = ValidationResult(isError = (!isPasswordOk), errorMessage = "your password must:\n- be at least 8 characters long\n- have at least one letter and one number"))
        return isPasswordOk
    }

    private fun validateRepeatedPassword(password: String, repeatedPassword: String): Boolean{
        val isRepeatedPasswordOk = password == repeatedPassword
        registerUiState = registerUiState.copy(repeatedPassword = ValidationResult(isError = !isRepeatedPasswordOk, errorMessage = "Your passwords doesn't match"))
        return isRepeatedPasswordOk
    }
}