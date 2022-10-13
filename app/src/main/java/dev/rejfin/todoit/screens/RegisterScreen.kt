package dev.rejfin.todoit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.AuthViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.components.AppLogo
import dev.rejfin.todoit.components.ErrorDialog
import dev.rejfin.todoit.components.InfoDialog
import dev.rejfin.todoit.components.InputField
import dev.rejfin.todoit.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.screens.destinations.RegisterScreenDestination
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun RegisterScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()) {
    val _uiState = viewModel.registerUiState
    var nick by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(Modifier.fillMaxSize(0.7f)) {
            AppLogo(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 45.dp)
                .align(CenterHorizontally))
            InputField(
                label = stringResource(id = R.string.nick),
                onTextChange = {
                    nick = it
                    viewModel.clearError(_uiState.nick)
                },
                _uiState.nick,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.email),
                onTextChange = {
                    email = it
                    viewModel.clearError(_uiState.email)
                },
                _uiState.email,
                keyboardType = KeyboardType.Email,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.password),
                onTextChange = {
                    password = it
                    viewModel.clearError(_uiState.password)
                },
                _uiState.password,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Next,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.repeat_password),
                onTextChange = {
                    repeatedPassword = it
                    viewModel.clearError(_uiState.repeatedPassword)
                },
                _uiState.repeatedPassword,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Done,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            Row(modifier = Modifier
                .padding(vertical = 15.dp)
                .align(CenterHorizontally)){
                Text(stringResource(id = R.string.have_acc),
                    fontSize = 15.sp,
                    color = CustomThemeManager.colors.textColorSecond
                )
                Text(
                    stringResource(id = R.string.log_in_now),
                    color = CustomThemeManager.colors.primaryColor,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable(enabled = !_uiState.isAuthInProgress){
                    navigator?.navigate(LoginScreenDestination) {
                        popUpTo(RegisterScreenDestination) {
                            inclusive = true
                        }
                    }
                }
                )
            }
            Button(
                onClick = {
                    viewModel.registerUserWithEmail(nick, email, password, repeatedPassword)
                }, modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(140.dp, 200.dp),
                enabled = !_uiState.isAuthInProgress,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomThemeManager.colors.primaryColor)
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    color = CustomThemeManager.colors.textColorOnPrimary
                )
            }
            if(_uiState.isAuthInProgress){
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if(_uiState.authFailedMessage != null){
                ErrorDialog(title = stringResource(id = R.string.register_error), errorText = _uiState.authFailedMessage, onDialogClose = {
                    viewModel.dismissAuthError()
                })
            }
            if(_uiState.registerSuccess){
                InfoDialog(
                    title = stringResource(id = R.string.register_success),
                    infoText = stringResource(id = R.string.register_success_message),
                    onDialogClose = {
                        navigator?.navigate(LoginScreenDestination){
                            popUpTo(RegisterScreenDestination) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    CustomJetpackComposeTheme() {
        RegisterScreen(null)
    }
}