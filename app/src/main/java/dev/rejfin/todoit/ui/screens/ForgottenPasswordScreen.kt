package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.ui.dialogs.InfoDialog

@Destination
@Composable
fun ForgottenPasswordScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()){
    
    val badEmailFormatMessage = stringResource(id = R.string.bad_email_format)
    var email by remember { mutableStateOf("") }
    var emailValidation by remember { mutableStateOf(ValidationResult(errorMessage = badEmailFormatMessage)) }
    var emailSent by remember { mutableStateOf(false) }

    fun checkEmail(email:String){
        emailValidation = if(!email.contains('@')){
            emailValidation.copy(isError = true)
        }else{
            emailValidation.copy(isError = false)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.enter_email_forgotten_pass),
            modifier = Modifier.fillMaxWidth(0.7f),
            color = CustomThemeManager.colors.textColorFirst
        )
        InputField(
            label = stringResource(id = R.string.email),
            onTextChange = {
                email = it
                checkEmail(it)
            },
            placeholder = email,
            validationResult = emailValidation,
            modifier = Modifier.fillMaxWidth(0.7f),
            rememberTextInternally = false
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(0.7f)
        ){
            Button(onClick = {
                navigator?.popBackStack()
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Button(onClick = {
                if(!emailValidation.isError){
                    viewModel.remindPassword(email)
                    emailSent = true
                }
            }) {
                Text(text = stringResource(id = R.string.ok))
            }
        }

        if(emailSent){
            InfoDialog(
                title = "Email sent",
                infoText = stringResource(id = R.string.reset_password_email_sent),
                onDialogClose = {
                    navigator?.popBackStack()
                })
        }
    }
}

@Composable
@Preview
fun ForgottenPasswordScreenPreview(){
    ForgottenPasswordScreen(navigator = null)
}