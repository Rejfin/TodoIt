package dev.rejfin.todoit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.AuthViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.components.AppLogo
import dev.rejfin.todoit.components.InputField
import dev.rejfin.todoit.ui.theme.TodoItTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.components.ErrorDialog
import dev.rejfin.todoit.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.screens.destinations.RegisterScreenDestination

@RootNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()){
    val _uiState = viewModel.loginUiState
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect("login"){
        if(viewModel.isUserAlreadyLoggedIn()){
            navigator?.navigate(HomeScreenDestination){
                popUpTo(LoginScreenDestination) {
                    inclusive = true
                }
            }
        }
    }

    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Column(Modifier.fillMaxWidth(0.7f)){
            AppLogo(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 45.dp)
                .align(Alignment.CenterHorizontally))
            InputField(
                label = stringResource(id = R.string.email),
                onTextChange = { email = it },
                _uiState.email,
                keyboardType = KeyboardType.Email,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.password),
                onTextChange = {password = it},
                _uiState.password,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Done,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_uiState.isAuthInProgress
            )
            Row(modifier = Modifier
                .padding(vertical = 15.dp)
                .align(Alignment.CenterHorizontally)){
                Text(stringResource(id = R.string.dont_have_acc), fontSize = 15.sp)
                Text(
                    stringResource(id = R.string.create_now),
                    color = MaterialTheme.colors.primary,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable(enabled = !_uiState.isAuthInProgress) {
                        navigator?.navigate(RegisterScreenDestination){
                            popUpTo(LoginScreenDestination) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            Button(onClick = {
                viewModel.loginUserWithEmail(email, password)
            }, modifier = Modifier
                .align(Alignment.End)
                .widthIn(140.dp, 200.dp),
                enabled = !_uiState.isAuthInProgress,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(id = R.string.log_in))
            }
            if(_uiState.isAuthInProgress){
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if(_uiState.authFailedMessage != null){
                ErrorDialog(title = stringResource(id = R.string.log_in_error), errorText = _uiState.authFailedMessage, onDialogClose = {
                    viewModel.dismissAuthError()
                })
            }
            if(_uiState.isUserLoggedIn){
                navigator?.navigate(HomeScreenDestination){
                    popUpTo(LoginScreenDestination) {
                        inclusive = true
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TodoItTheme {
        LoginScreen(navigator = null)
    }
}