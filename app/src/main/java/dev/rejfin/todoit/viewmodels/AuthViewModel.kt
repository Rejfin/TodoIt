package dev.rejfin.todoit.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dev.rejfin.todoit.models.states.LoginUiState
import dev.rejfin.todoit.models.states.RegisterUiState
import dev.rejfin.todoit.models.ValidationResult
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel: ViewModel() {
    var registerUiState by mutableStateOf(RegisterUiState())
        private set

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    private var auth: FirebaseAuth = Firebase.auth
    private val usersDbRef = Firebase.database.getReference("users")
    private val nicksDbRef = Firebase.database.getReference("nicks")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("users")

    fun registerUserWithEmail(nick: String, displayName:String, email:String, password:String, repeatedPassword: String, imageUri: Uri){
        registerUiState = registerUiState.copy(isAuthInProgress = true)

        validateNick(nick)
        validateDisplayName(displayName)
        validateEmail(email)
        validatePassword(password)
        validateRepeatedPassword(password, repeatedPassword)

        if(registerUiState.nick.isError || registerUiState.email.isError || registerUiState.password.isError || registerUiState.repeatedPassword.isError || registerUiState.displayName.isError){
            registerUiState = registerUiState.copy(isAuthInProgress = false)
        }else{
            nicksDbRef.child(nick).get().addOnSuccessListener { dbNick ->
                if(!dbNick.exists()){
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            val user = Firebase.auth.currentUser

                            nicksDbRef.child(nick).setValue(mapOf(
                                "userId" to user!!.uid
                            ))

                            var imageUrl: String? = null
                            sendImage(imageUri, user.uid){ link ->
                                imageUrl = link
                            }

                            val profileUpdate = userProfileChangeRequest {
                                this.displayName = displayName
                                photoUri = imageUrl?.toUri()
                            }

                            viewModelScope.launch {
                                val updateProfile = launch {user.updateProfile(profileUpdate)}
                                val createEntryInDatabase = launch{usersDbRef.child(user.uid).setValue(mapOf(
                                    "nick" to nick,
                                    "displayName" to displayName,
                                    "uid" to user.uid,
                                    "imageUrl" to imageUrl,
                                    "taskDone" to 0,
                                    "allTask" to 0,
                                    "xp" to 0,
                                    "level" to 1,
                                ))}

                                updateProfile.join()
                                createEntryInDatabase.join()

                                registerUiState = registerUiState.copy(isAuthInProgress = false, registerSuccess = true)
                                auth.signOut()
                            }
                        }else{
                            try{
                                throw it.exception!!
                            }catch (e: FirebaseAuthUserCollisionException){
                                registerUiState = registerUiState.copy(
                                    isAuthInProgress = false,
                                    authFailedMessage = it.exception?.localizedMessage,
                                    registerSuccess = false,
                                    email = ValidationResult(
                                        isError = true,
                                        errorMessage = "user with this email already exist"
                                    )
                                )
                            }catch (e: Exception){
                                registerUiState = registerUiState.copy(
                                    isAuthInProgress = false,
                                    authFailedMessage = it.exception?.localizedMessage,
                                    registerSuccess = false
                                )
                            }
                        }
                    }
                }else{
                    registerUiState = registerUiState.copy(nick = ValidationResult(
                        isError = true,
                        errorMessage = "user with such a nickname already exists",),
                        isAuthInProgress = false,
                        authFailedMessage = "user with such a nickname already exists :/\nLog in if this is your account, or create a new one with a different nickname"
                    )
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
            registerUiState.displayName -> registerUiState = registerUiState.copy(displayName = ValidationResult(isError = false))
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

    private fun validateDisplayName(displayName:String): Boolean{
        val isDisplayNameOk = displayName.length >= 4
        registerUiState = registerUiState.copy(displayName = ValidationResult(isError = !isDisplayNameOk, errorMessage = "Nick has to be at least 4 characters"))
        return isDisplayNameOk
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

    private fun sendImage(image: Uri, userId: String, callback: (imageUrl:String?) -> Unit){
        val ref = storageRef.child(userId)
        val uploadTask = ref.putFile(image)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                callback(null)
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result.toString())
            } else {
                callback(null)
            }
        }
    }
}