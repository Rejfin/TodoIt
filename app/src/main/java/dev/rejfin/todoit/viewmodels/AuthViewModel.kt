package dev.rejfin.todoit.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dev.rejfin.todoit.models.UserModel
import dev.rejfin.todoit.models.states.LoginUiState
import dev.rejfin.todoit.models.states.RegisterUiState
import dev.rejfin.todoit.models.ValidationResult
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel: ViewModel() {
    /** ui state for register screen */
    var registerUiState by mutableStateOf(RegisterUiState())
        private set

    /** ui state for log in screen */
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    private var auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.database
    private val nicksDbRef = database.getReference("nicks")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("users")

    /** Register the user after checking the validity of the entered data in the first place */
    fun registerUserWithEmail(){
        registerUiState.isAuthInProgress.value = true

        /** validate every input field in registration form and if necessary set errors*/
        registerUiState.nickValidation.value = ValidationResult(
            isError = !validateNick(registerUiState.nick.value),
            errorMessage = "Nick has to be at least 4 characters, and cant contains spaces"
        )
        registerUiState.displayNameValidation.value = ValidationResult(
            isError = !validateDisplayName(registerUiState.displayName.value),
            errorMessage = "Display name has to be at least 4 characters"
        )
        registerUiState.emailValidation.value = ValidationResult(
            isError = !validateEmail(registerUiState.email.value),
            errorMessage = "bad email format"
        )
        registerUiState.passwordValidation.value = ValidationResult(
            isError = (!validatePassword(registerUiState.password.value)),
            errorMessage = "your password must:\n- be at least 8 characters long\n- have at least one letter and one number"
        )
        registerUiState.repeatedPasswordValidation.value = ValidationResult(
            isError = !validateRepeatedPassword(registerUiState.password.value, registerUiState.repeatedPassword.value),
            errorMessage = "Your passwords doesn't match"
        )

        if(registerUiState.nickValidation.value.isError || registerUiState.emailValidation.value.isError || registerUiState.passwordValidation.value.isError || registerUiState.repeatedPasswordValidation.value.isError || registerUiState.displayNameValidation.value.isError){
            registerUiState.isAuthInProgress.value = false
        }else{
            /**
             * first check if entered nick not exist in database
             * nick must be unique for every user, so if it exist show error
             */
            nicksDbRef.child(registerUiState.nick.value).get().addOnSuccessListener { dbNick ->
                if(!dbNick.exists()){
                    /**
                     * create user with given email and password, then set url to profile picture
                     * and create entry for user in database
                     */
                    auth.createUserWithEmailAndPassword(registerUiState.email.value, registerUiState.password.value).addOnCompleteListener {
                        if(it.isSuccessful){
                            val user = Firebase.auth.currentUser

                            sendImage(registerUiState.selectedImage.value, user!!.uid){ link ->

                                /** created user model will be sent to firebase database */
                                val userEntry = UserModel(
                                    id = user.uid,
                                    nick = registerUiState.nick.value,
                                    displayName = registerUiState.displayName.value,
                                    imageUrl = link,
                                )

                                /** set paths to update data in several places at once */
                                val childToUpdate = mutableMapOf(
                                    "/nicks/${registerUiState.nick.value}" to mapOf("userId" to user.uid),
                                    "/users/${user.uid}" to userEntry,
                                )

                                /** update user profile picture and his display name */
                                val profileUpdate = userProfileChangeRequest {
                                    this.displayName = registerUiState.displayName.value
                                    photoUri = link?.toUri()
                                }

                                /** sending data to firebase is run in coroutine to speed up the process */
                                viewModelScope.launch {
                                    val updateProfile = launch { user.updateProfile(profileUpdate) }
                                    val createEntryInDatabase = launch {
                                        database.reference.updateChildren(childToUpdate)
                                    }

                                    updateProfile.join()
                                    createEntryInDatabase.join()

                                    registerUiState.apply {
                                        isAuthInProgress.value = false
                                        registerSuccess.value = true
                                    }
                                    auth.signOut()
                                }
                            }
                        }else{
                            /**
                             * if something went wrong during registration,
                             * we throw out the received error and compare it with the errors
                             * created in the firebase library
                             * and display the appropriate error to the user
                             */
                            try{
                                throw it.exception!!
                            }catch (e: FirebaseAuthUserCollisionException){
                                registerUiState.apply {
                                    isAuthInProgress.value = false
                                    registerSuccess.value = false
                                    authFailedMessage.value = it.exception?.localizedMessage
                                    emailValidation.value = ValidationResult(
                                        isError = true,
                                        errorMessage = "user with this email already exist"
                                    )
                                }
                            }catch (e: Exception){
                                registerUiState.apply {
                                    isAuthInProgress.value = false
                                    authFailedMessage.value = it.exception?.localizedMessage
                                    registerSuccess.value = false
                                }
                            }
                        }
                    }
                }else{
                    /** user with such nick exist so we show user error and ask him to change it*/
                    registerUiState.apply {
                        nickValidation.value = ValidationResult(
                            isError = true,
                            errorMessage = "user with such a nickname already exists")
                        isAuthInProgress.value = false
                        authFailedMessage.value = "user with such a nickname already exists :/\nLog in if this is your account, or create a new one with a different nickname"
                    }
                }
            }
        }
    }

    /** Try to log in user using data entered by him */
    fun loginUserWithEmail(){
        loginUiState.isAuthInProgress.value = true
        if(loginUiState.email.value.isEmpty() || loginUiState.password.value.isEmpty()){
            loginUiState.isAuthInProgress.value = false
            loginUiState.emailValidation.value = ValidationResult(isError = loginUiState.email.value.isEmpty(), "field can't be empty")
            loginUiState.passwordValidation.value = ValidationResult(isError = loginUiState.password.value.isEmpty(), "field can't be empty")
        }else{
            auth.signInWithEmailAndPassword(loginUiState.email.value, loginUiState.password.value).addOnCompleteListener {
                loginUiState = if(it.isSuccessful){
                    loginUiState.apply {
                        isAuthInProgress.value = false
                        isUserLoggedIn.value = true
                    }
                }else{
                    loginUiState.apply {
                        isAuthInProgress.value = false
                        password.value = ""
                        authFailedMessage.value = it.exception?.localizedMessage
                    }
                }
            }
        }
    }

    /**
     * check if user is already logged in, and if it is set uiState to user data
     * next navigate user to home screen
     */
    fun isUserAlreadyLoggedIn(): Boolean{
        val isUserLoggedIn = auth.currentUser != null
        loginUiState.isUserLoggedIn.value = isUserLoggedIn
        return isUserLoggedIn
    }

    /** Function clears error for selected InputField in register and login form */
    fun clearError(fieldState: ValidationResult){
        when(fieldState){
            registerUiState.nickValidation.value -> registerUiState.nickValidation.value = ValidationResult(isError = false)
            registerUiState.displayNameValidation.value -> registerUiState.displayNameValidation.value = ValidationResult(isError = false)
            registerUiState.emailValidation.value -> registerUiState.emailValidation.value = ValidationResult(isError = false)
            registerUiState.passwordValidation.value -> registerUiState.passwordValidation.value = ValidationResult(isError = false)
            registerUiState.repeatedPasswordValidation.value -> registerUiState.repeatedPasswordValidation.value = ValidationResult(isError = false)
            loginUiState.emailValidation.value -> loginUiState.emailValidation.value = ValidationResult(isError = false)
            loginUiState.passwordValidation.value -> loginUiState.passwordValidation.value = ValidationResult(isError = false)
        }
    }

    fun dismissAuthError(){
        loginUiState.authFailedMessage.value = null
        registerUiState.authFailedMessage.value = null
    }

    private fun validateNick(nick: String): Boolean {
        return nick.length >= 4 && !nick.contains(" ")
    }

    private fun validateDisplayName(displayName: String): Boolean {
        return displayName.length >= 4
    }

    fun validateEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }

    private fun validateRepeatedPassword(password: String, repeatedPassword: String): Boolean {
        return password == repeatedPassword
    }

    /**
     * Function used to send user profile picture to firebase storage
     * if the operation is successful, the callback argument will return valid image url
     * if the operation is not successful, the callback argument will return null
     */
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

    /** function used to send reset password email to user */
    fun remindPassword(email: String){
        auth.sendPasswordResetEmail(email)
    }
}