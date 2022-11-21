package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.ui.dialogs.CustomDialog
import dev.rejfin.todoit.ui.dialogs.DialogType

@Destination
@Composable
fun ForgottenPasswordScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()){
    
    val badEmailFormatMessage = stringResource(id = R.string.bad_email_format)
    var email by remember { mutableStateOf("") }
    var emailValidation by remember { mutableStateOf(ValidationResult(errorMessage = badEmailFormatMessage)) }
    var emailSent by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.enter_email_forgotten_pass),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.7f),
            color = CustomThemeManager.colors.textColorFirst
        )
        InputField(
            label = stringResource(id = R.string.email),
            onTextChange = {
                email = it
                emailValidation = emailValidation.copy(isError = !viewModel.validateEmail(it))
            },
            text = email,
            validationResult = emailValidation,
            modifier = Modifier.fillMaxWidth(0.7f),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(0.7f)
        ){
            Button(onClick = {
                navigator?.popBackStack()
            },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = CustomThemeManager.colors.errorColor,
                    contentColor = CustomThemeManager.colors.textColorOnPrimary
                ),
            modifier = Modifier.weight(0.1f)) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if(!emailValidation.isError){
                    viewModel.remindPassword(email)
                    emailSent = true
                }
            },
            modifier = Modifier.weight(0.1f)) {
                Text(text = stringResource(id = R.string.ok))
            }
        }

        if(emailSent){
            CustomDialog(
                dialogType = DialogType.INFO,
                title = stringResource(id = R.string.email_sent),
                message = stringResource(id = R.string.reset_password_email_sent),
                onConfirmClick = {
                    navigator?.popBackStack()
                },
            )
        }
    }
}

@Composable
@Preview
fun ForgottenPasswordScreenPreview(){
    ForgottenPasswordScreen(navigator = null)
}